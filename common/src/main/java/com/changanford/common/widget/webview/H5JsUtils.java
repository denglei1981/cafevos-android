package com.changanford.common.widget.webview;

import android.webkit.WebView;

/**
 * Created by Administrator on 2019/1/18.
 */

public class H5JsUtils {

    public static String JS_ = "(function () {\n" +
            "        var imgs = document.getElementsByTagName(\"img\");\n" +
            "        var currentImgs = [];\n" +
            "        var imgSrcs = [];\n" +
            "        for(const img of imgs){\n" +
            "          imgSrcs.push(img.src);\n" +
            "          if (img.parentNode && img.parentNode.nodeName === \"A\") continue;\n" +
            "          currentImgs.push(img);\n" +
            "        }\n" +
            "        currentImgs.forEach(function (img) {\n" +
            "          img.onclick = function () {\n" +
            "            window.imagelistener.openImage(this.src, imgSrcs);\n" +
            "          };\n" +
            "        });\n" +
            "      })();";


    public static String agent2JS_ = "(function(){\n" +
            "     var objs = document.getElementsByTagName(\"img\"); \n" +
            "     var array=new Array(); \n" +
            "     var strarray = new Array();\n" +
            "     for(var j=0;j<objs.length;j++){ \n" +
            "            strarray[j]=objs[j].src; \n" +
            "           if(objs[j].parentNode==null||objs[j].parentNode.nodeName!='A'){\n" +
            "              array[j]=objs[j];\n" +
            "\t\t    } \n" +
            "\t }\n" +
            "     for(var i=0;i<array.length;i++)  \n" +
            "     {\n" +
            "          array[i].onclick=function()  {  \n" +
            "               window.OSApp.openImage(this.src,strarray);" +
            "            }  \n" +
            "       }\n" +
            "})(); ";


    public static void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:".concat(JS_));
    }

    public static void addImageClickListener2(com.tencent.smtt.sdk.WebView webView) {
        webView.loadUrl("javascript:".concat(JS_));
    }
}
