package com.changanford.common.widget.webview

import android.app.Activity
import android.net.http.SslError
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import com.changanford.common.util.DensityUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toastShow
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.AgentWebUtils
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebViewClient

/**
 * @Author: hpb
 * @Date: 2020/6/6
 * @Des: WebView基础配置
 */
class CustomWebHelper(activity: Activity, private var webView: WebView?, isMargin: Boolean = true) {
    init {
        if (webView != null) {
            if (isMargin && webView?.layoutParams is ViewGroup.MarginLayoutParams) {
                val params = webView?.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = DensityUtils.dip2px(15F)
                params.rightMargin = DensityUtils.dip2px(15F)
            }
            webView?.settings?.let {
                it.javaScriptEnabled = true
                it.javaScriptCanOpenWindowsAutomatically = true
                it.setSupportZoom(true)
                it.builtInZoomControls = false
                it.useWideViewPort = true
                it.loadWithOverviewMode = true
                it.databaseEnabled = true
                it.setAppCacheEnabled(true)
                it.loadsImagesAutomatically = true
                it.setSupportMultipleWindows(false)
                it.blockNetworkImage = false
                it.allowFileAccess = true
                it.domStorageEnabled = true
                it.setNeedInitialFocus(true)
                it.defaultTextEncodingName = "utf-8"
                it.defaultFontSize = 16
                it.minimumFontSize = 12
                val dir = AgentWebConfig.getCachePath(webView!!.context)
                it.setAppCachePath(dir)
                if (Build.VERSION.SDK_INT >= 21) {
                    // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
                    it.allowFileAccessFromFileURLs = false
                    // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
                    it.allowUniversalAccessFromFileURLs = false;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                if (AgentWebUtils.checkNetwork(webView!!.context)) {
                    //根据cache-control获取数据。
                    it.cacheMode = WebSettings.LOAD_DEFAULT;
                } else {
                    //没网，则从本地获取，即离线加载
                    it.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;
                }
            }
            webView?.overScrollMode = WebView.OVER_SCROLL_NEVER
            webView?.setLayerType(View.LAYER_TYPE_NONE, null)
            webView?.isVerticalScrollBarEnabled = false
            webView?.addJavascriptInterface(MJavascriptInterface(activity), "imagelistener")
            webView?.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    // 超链接跳转
                    if (url.contains("jumpDataType=")) {
                        try {
                            val paramStr = url.indexOf("?")
                            val param = url.subSequence(paramStr, url.length)
                            val split = param.split("&")
                            var jumpType = ""
                            var jumpData = ""
                            split.forEach {s->
                                if(s.contains("jumpDataType=")){
                                    jumpType=s.substring(s.indexOf("=")+1,s.length)
                                }
                                if(s.contains("jumpDataValue=")){
                                    jumpData=s.substring(s.indexOf("=")+1,s.length)
                                }
                            }
                            try {
                                JumpUtils.instans?.jump(jumpType.toInt(), jumpData)
                            } catch (e: NumberFormatException) {
                                toastShow("获取链接失败")
                            }
                        } catch (e: StringIndexOutOfBoundsException) {
                            toastShow("获取链接失败")
                        }
                        return true
                    }
                    if (url.startsWith(DefaultWebClient.HTTP_SCHEME) || url.startsWith(DefaultWebClient.HTTPS_SCHEME)) {
                        JumpUtils.instans?.jump(1,url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    H5JsUtils.addImageClickListener(view)
                }

                override fun onReceivedSslError(
                    view: WebView,
                    handler: SslErrorHandler,
                    error: SslError
                ) {
                    super.onReceivedSslError(view, handler, error)
                    handler.proceed() // 接受信任所有网站的证书
                }
            }
        }
    }

    fun loadDataWithBaseURL(htmlData: String) {
        webView?.loadDataWithBaseURL(
            null,
            HHtmlUtils.getHtmlData(htmlData) ?: "",
            "text/html",
            "utf-8",
            null
        )
    }

    fun onResume() {
        webView?.let {
            if (Build.VERSION.SDK_INT >= 21) {
                it.onResume()
            }
            it.resumeTimers()
        }
    }

    fun onPause() {
        webView?.let {
            if (Build.VERSION.SDK_INT >= 21) {
                it.onPause()
            }
            it.pauseTimers()
        }
    }

    fun onDestroy() {
        webView?.let { webView ->
            webView.resumeTimers()
            if (Looper.myLooper() != Looper.getMainLooper()) {
                return
            }
            webView.loadUrl("about:blank")
            webView.stopLoading()
            if (webView.handler != null) {
                webView.handler.removeCallbacksAndMessages(null)
            }
            webView.removeAllViews()
            val mViewGroup = webView.parent as? ViewGroup
            mViewGroup?.removeView(webView)
            webView.webChromeClient = null
//            webView.webViewClient = null
            webView.tag = null
            webView.clearHistory()
            webView.destroy()
        }
        webView = null
    }
}