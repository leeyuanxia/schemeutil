package com.thecover.lib.schemeutils.idispatchers


import android.util.Log
import android.webkit.WebView
import java.lang.ref.WeakReference

/**
 * @author:likewen
 * @date:2023/2/9
 *
 */
class Callback(private val callback: String, webView: WebView) {
    private val webViewWeakRe = WeakReference<WebView>(webView)

    /**
     * @param result 传给h5的返回值
     * 如果需要返回数据给H5，调用这个方法
     */
    fun call(result: String?) {
        webViewWeakRe.get()?.evaluateJavascript("${callback}(\"${result}\")", null)
    }
}
