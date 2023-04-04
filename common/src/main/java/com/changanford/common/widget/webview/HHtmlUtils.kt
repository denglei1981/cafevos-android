package com.changanford.common.widget.webview

import android.webkit.WebView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


/**
 * @Author: hpb
 * @Date: 2020/6/6
 * @Des:
 */
object HHtmlUtils {

    /**
     * 加载html标签
     *
     * @param bodyHTML
     * @return
     */
    fun getHtmlData(bodyHTML: String, spuSource: String = "0"): String? {
        val doc: Document = Jsoup.parse(bodyHTML)
        val elements: Elements = doc.getElementsByTag("img")
        for (element in elements) {
            element.attr("width", "100%").attr("height", "auto")
        }

        val head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\"> " +
                "</head>"
        return "<html>$head<body>$doc</body></html>"

//        val head = "<head>" +
//                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
//                "<style>img{max-width: 100%; width:auto; height:auto!important;} table{max-width:100%;height:auto;}</style>" +
//                "</head>"
//        val headJD = "<head>" +
//                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.44,minimum-scale=0.25,maximum-scale=2, user-scalable=no\"> " +
//                "<style>img{max-width: 100%; width:auto; height:auto!important;} table{max-width:100%;height:auto;}</style>" +
//                "</head>"
//        return if (spuSource == "1") "<html>$headJD<body>$bodyHTML</body></html>" else "<html>$head<body>$bodyHTML</body></html>"
    }

    fun getBoundingClientRect(view: WebView?) {
        view?.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)")
    }
}