package cn.thecover.lib.schemeutils

import android.content.Context
import cn.thecover.lib.schemeutils.constant.DISPATCHER_PACKAGE_NAME
import cn.thecover.lib.schemeutils.lib_annotation.Root
import cn.thecover.lib.schemeutils.utils.ClassUtils
import com.thecover.lib.schemeutils.idispatchers.Dispatchers

import dalvik.system.DexFile


/**
 * @author:likewen
 * @date:2022/9/2
 * desc: schemeutil初始化类，包含初始化，root类，黑名单控制
 */
object SUInit {

    var rootMap = hashMapOf<String, Dispatchers>()
    var blackListForInsideInvoke : MutableList<String> = mutableListOf()
    var blackListForOutsideInvoke : MutableList<String> = mutableListOf()



    fun init(context: Context) {
        val names = ClassUtils.getFileNameByPackageName(context, DISPATCHER_PACKAGE_NAME)
        for (name in names) {
            val clazz = Class.forName(name)
            if (Dispatchers::class.java.isAssignableFrom(clazz) && clazz.getAnnotation(Root::class.java) != null) {
                val dispatcher = clazz.newInstance() as Dispatchers
                rootMap[clazz.getAnnotation(Root::class.java).value] = dispatcher
            }
        }
    }

    fun invokeSchema(context: Context?,module: String?, action: String, params: String) {

        for (value in rootMap.keys) {
            if (module?.let { value.contains(it) } == true) {
                rootMap[value]?.dispatch(context, module, action, params)
            }
        }
    }

    /**
     * 内部调用时action校验
     */
    fun insideInvokePermissionControl(action: String): Boolean = blackListForInsideInvoke.contains(action)
    /**
     * 外部调用时action校验
     */
    fun outsideInvokePermissionControl(action: String): Boolean = blackListForOutsideInvoke.contains(action)

    fun getClassName(context: Context, packageName :String): MutableList<String> {
        val names = mutableListOf<String>()
        runCatching {
            val path =context.packageManager.getApplicationInfo(context.packageName, 0).sourceDir
            val dexFile =  DexFile(path)
            val enumeration = dexFile.entries()
            while (enumeration.hasMoreElements()) {
                val className = enumeration.nextElement()
                if (className.startsWith(packageName)) {
                    names.add(className)
                }
            }
        }.onFailure {

        }
        return names
    }
}