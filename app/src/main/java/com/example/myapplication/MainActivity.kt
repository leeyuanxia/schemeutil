package com.example.myapplication


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.thecover.lib.schemeutils.demo.R
import cn.thecover.lib.schemeutils.utils.SchemaUtil

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        var  mWebView: WebView? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById(R.id.webView)
        mWebView?.apply {
            val mWebSettings = mWebView.settings;
            mWebView.loadUrl("file:///android_asset/test.html");
            //启用JavaScript。
            mWebSettings.javaScriptEnabled = true;
            mWebSettings.useWideViewPort = true;
            mWebSettings.javaScriptCanOpenWindowsAutomatically = true;
            webViewClient = object: WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return if (url.contains("test://")) {
                        SchemaUtil.invokeSchemeFromInsideWebView(applicationContext, Uri.parse(url), this@apply)
                        true
                    } else {
                        mWebView.loadUrl(url);
                        false;
                    }
                }
            }
        }

    }
}
