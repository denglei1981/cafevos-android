package com.changanford.common.wutil;

import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.Locale;

public class MyTagHandler implements Html.TagHandler {
    private Activity context;
    private ArrayList<String> imgUrls;

    public MyTagHandler(Activity context) {
        this.context = context;
        imgUrls = new ArrayList<>();
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // 处理标签<img>
        if ("img".equals(tag.toLowerCase(Locale.getDefault()))) {
            // 获取长度
            int len = output.length();
            // 获取图片地址
            ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
            String imgURL = images[0].getSource();
            // 记录所有图片地址
            imgUrls.add(imgURL);
            // 记录是第几张图片
            int position = imgUrls.size()-1;
            // 使图片可点击并监听点击事件
            output.setSpan(new ClickableImage(context, position), len - 1, len,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class ClickableImage extends ClickableSpan {
        private Activity context;
        private int position;

        public ClickableImage(Activity context, int position) {
            this.context = context;
            this.position = position;
        }

        @Override
        public void onClick(View widget) {
//           //查看大图 imgUrls position
        }
    }
}
