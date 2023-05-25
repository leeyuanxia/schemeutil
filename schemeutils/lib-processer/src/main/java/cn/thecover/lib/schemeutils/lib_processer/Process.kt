package cn.thecover.lib.schemeutils.lib_processer


import cn.thecover.lib.schemeutils.lib_annotation.*
import cn.thecover.lib.schemeutils.constant.DISPATCHER_CLASS_NAME_PREFIX
import cn.thecover.lib.schemeutils.constant.DISPATCHER_PACKAGE_NAME
import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

/**
 * @author:likewen
 * @date:2022/9/22
 * desc: 注解处理器，获取注解生成dispatcher文件
 */

const val packageName = DISPATCHER_PACKAGE_NAME
const val className = DISPATCHER_CLASS_NAME_PREFIX


@SupportedOptions("SCHEME_MODULE_NAME")
@SupportedAnnotationTypes("cn.thecover.lib.schemeutils.lib_annotation.Module")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class Process: AbstractProcessor() {
    var moduleMap: MutableMap<String, String> = hashMapOf()
    var rootName: String? = null
    var filer: Filer? = null
    var mMessage: Messager? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        filer = processingEnv?.filer
        mMessage = processingEnv?.messager
        mMessage?.printMessage(Diagnostic.Kind.NOTE, "init开始" )
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val types = hashSetOf<String>()
        types.add(Module::class.java.canonicalName)
        val options = processingEnv.options
        if (options.isNotEmpty()) {
            rootName = options["SCHEME_MODULE_NAME"]
        }
        mMessage?.printMessage(Diagnostic.Kind.NOTE, "getSupportedAnnotationTypes开始" )
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return super.getSupportedSourceVersion()
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        val elementsAnnotatedWith = p1?.getElementsAnnotatedWith(Module::class.java)
        mMessage?.printMessage(Diagnostic.Kind.NOTE, "解析注解开始" )
        if (elementsAnnotatedWith != null) {
            for (element in elementsAnnotatedWith) {
                val moduleName = element.getAnnotation(Module::class.java).name
                mMessage?.printMessage(Diagnostic.Kind.NOTE, "解析注解：$moduleName")
                moduleMap[moduleName] = (element as TypeElement).qualifiedName.toString()
            }
        }
        mMessage?.printMessage(Diagnostic.Kind.NOTE, "解析注解结束" )
        generateFile()
        return false
    }

    private fun generateFile() {
        val stringBuilder = StringBuilder(" new HashMap<String, String>(){\n\t{")
        for (module in moduleMap) {
            stringBuilder.append("\n\t\tput(\"${module.key}\",\"${module.value}\");\n\t")
        }
        stringBuilder.append("}\n\t}")
        val mapFieldSpec = FieldSpec.builder(HashMap::class.java, "moduleMap", Modifier.PRIVATE, Modifier.STATIC)
            .initializer(stringBuilder.toString()).build()


        val dispatchMethod = MethodSpec.methodBuilder("dispatch")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(Boolean::class.java)
            .addParameter(ClassName.get("android.content", "Context"),"context")
            .addParameter(String::class.java, "module")
            .addParameter(String::class.java, "action")
            .addParameter(String::class.java, "params")
            .addStatement("String fullName = (String) moduleMap.get(module);\n" +
                    "      try {\n" +
                    "          Class clazz = Class.forName(fullName);\n" +
                    "          for (${'$'}T method : clazz.getDeclaredMethods()) {\n" +
                    "              if (method.getAnnotation(${'$'}T.class) != null && action.equals(method.getAnnotation(${'$'}T.class).name())) {\n" +
                    "                  method.invoke(clazz.getDeclaredConstructor().newInstance(),context, params);\n" +
                    "              return true;}\n" +
                    "          }\n" +
                    "      } catch (Exception e) {\n" +
                    "          e.printStackTrace();\n" +
                    "      }" +
                    "return true", Method::class.java,  Action::class.java, Action::class.java)
            .build()
        val paramsMethod = MethodSpec.methodBuilder("praseParams").addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(HashMap::class.java, String::class.java, Object::class.java))
            .addParameter(ParameterizedTypeName.get(Map::class.java, String::class.java, String::class.java), "params")
            .addParameter(ArrayTypeName.of(Annotation::class.java), "annotations")
            .addStatement("HashMap<String, String> paramsTypes = new HashMap<>();\n" +
                    "      HashMap<String, Object> castParams = new HashMap<>();\n" +
                    "      for (Annotation annotation : annotations) {\n" +
                    "          if (${'$'}T.class.getName().equals(annotation.annotationType().getName())) {\n" +
                    "              paramsTypes.put(annotation.getClass().getAnnotation(Param.class).name(), annotation.getClass().getAnnotation(Param.class).type());\n" +
                    "          }\n" +
                    "      }\n" +
                    "\n" +
                    "      for (String paramsTypeKey : paramsTypes.keySet()) {\n" +
                    "          if (params.containsKey(paramsTypeKey)) {\n" +
                    "              castParams.put(paramsTypeKey, castClass(paramsTypeKey, params.get(paramsTypeKey)));\n" +
                    "          }\n" +
                    "      }\n" +
                    "      return castParams", Param::class.java).build()

        val castMethod = MethodSpec.methodBuilder("castClass").addModifiers(Modifier.PUBLIC)
            .returns(Object::class.java)
            .addParameter(String::class.java, "classType")
            .addParameter(String::class.java, "value")
            .addCode("switch (classType) {\n" +
                    "          case \"int\" :\n" +
                    "              return Integer.parseInt(value);\n" +
                    "          case \"boolean\":\n" +
                    "              if (\"0\".equals(value)) {\n" +
                    "                  return false;\n" +
                    "              } else {\n" +
                    "                  return true;\n" +
                    "              }\n" +
                    "          case \"float\": return Float.parseFloat(value);\n" +
                    "          case \"double\": return Double.parseDouble(value);\n" +
                    "          case \"long\": return Long.parseLong(value);\n" +
                    "          default: return value;\n" +
                    "      }").build()

        val annotationSpec = AnnotationSpec.builder(Root::class.java).addMember("name",
            "${'$'}S", rootName
        ).addMember("value","${'$'}S", moduleMap.keys.toString()).build()
        val classBuilder = TypeSpec.classBuilder(rootName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.ROOT) else it.toString() } + className)
            .addSuperinterface(Class.forName("com.thecover.lib.schemeutils.idispatchers.Dispatchers")).addAnnotation(annotationSpec)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(dispatchMethod)
            .addMethod(paramsMethod)
            .addMethod(castMethod)
            .addField(mapFieldSpec).build()
        val javaFile = JavaFile.builder(packageName, classBuilder).build()
        mMessage?.printMessage(Diagnostic.Kind.NOTE, "生成文件：" + javaFile.packageName)
        runCatching { javaFile.writeTo(filer) }
    }

}