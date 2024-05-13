package com.changanford.common.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * Created by Administrator on 2018/5/31.
 */

public class SnowTextView extends androidx.appcompat.widget.AppCompatTextView {

    public SnowTextView(Context context) {
        this(context, null);
    }

    public SnowTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setT(getText().toString());
    }

    //统一设置**
    public void setT(String content) {
        SpannableString spannableString = new SpannableString("* " + content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1500F6"));
        spannableString.setSpan(colorSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }

}
