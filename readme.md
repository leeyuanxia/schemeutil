scheme: head://module/action?params={json}

#使用方法：
## 1。在android的module的build.gradle 中引入
`
plugin  id 'kotlin-kapt'
`
和在
`
android {
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [SCHEME_MODULE_NAME: project.getName(), SCHEME_GENERATE_DOC: "enable"]
            }
        }
    }
}
`

加入
`implementation 'cn.thecover.lib.schemeutils:schemeutil:VERSION@aar'
implementation 'cn.thecover.lib.schemeutils:IDispatchers:VERSION@aar'
implementation 'cn.thecover.lib.schemeutils:constant:VERSION@jar'
implementation 'cn.thecover.lib.schemeutils:lib-processor:VERSION'
kapt 'cn.thecover.lib.schemeutils:lib-processor:VERSION'
implementation 'cn.thecover.lib.schemeutils:lib-annotation:VERSION@jar'
`
依赖

## 2。在application oncreate的时候调用SUInit.init(this);

## 3。在app的mainfest中添加
`
<activity
    android:name="cn.thecover.lib.schemeutils.SchemeDispatcherActivity"
    android:exported="true"
    android:theme="@style/TestTheme">
    <intent-filter>
        <!-- action.VIEW和category.DEFAULT必须设置 -->
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <!-- 如果需要浏览器支持打开，则category.BROWSABLE -->
        <category android:name="android.intent.category.BROWSABLE" />
            <!-- schema的协议类型：随便设置，只要按照统一规则，前后端一致就行 -->
            <data android:scheme="xxx" />
    </intent-filter>
</activity>
`
## 4。使用demo:
`
@Module("xxx")
class MyTestDispatcher {
    @Action("xxx")
    fun testInvoke(context: Context?, params: String?) {
        Log.e("MyTestDispatcher", "testInvoke${params}")
    }
}
`

## 5。注意：@Module中的value具有唯一性，只能在一个代码文件中定义




