package cn.thecover.lib.schemeutils.utils

import android.content.Context
import android.net.Uri
import cn.thecover.lib.schemeutils.SUInit

/**
 * @author:likewen
 * @date:2022/9/22
 * desc: schema util 调用入口 ，注意外部使用和内部使用的调用
 */
object SchemaUtil {
    /**
     * app外部调用schema使用
     */
    fun invokeSchemeFromInside(context: Context?,uri: Uri) {
        var schema = uri.scheme
        val host = uri.host
        val auth = uri.pathSegments[0]
        if (SUInit.insideInvokePermissionControl(auth)) {
            return
        }
        val params = uri.getQueryParameter("params")
        SUInit.invokeSchema(context, host , auth, if (!params.isNullOrEmpty()) params else "")
    }
    /**
     * app内部调用schema使用
     */
    fun invokeSchemeFromOutside(context: Context?,uri: Uri) {
        var schema = uri.scheme
        val host = uri.host
        val auth = uri.pathSegments[0]
        if (SUInit.outsideInvokePermissionControl(auth)) {
            return
        }
        val params = uri.getQueryParameter("params")
        SUInit.invokeSchema(context, host , auth, if (!params.isNullOrEmpty()) params else "")
    }
}
