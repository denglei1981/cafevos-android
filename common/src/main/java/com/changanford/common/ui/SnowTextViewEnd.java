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

public class SnowTextViewEnd extends androidx.appcompat.widget.AppCompatTextView {

    public SnowTextViewEnd(Context context) {
        this(context, null);
    }

    public SnowTextViewEnd(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnowTextViewEnd(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setT(getText().toString());
    }

    //统一设置**
    public void setT(String content) {
        SpannableString spannableString = new SpannableString(content + "* ");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#cc3333"));
        spannableString.setSpan(colorSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }

}
