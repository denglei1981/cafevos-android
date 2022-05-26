package com.changanford.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.changanford.common.R;
import com.changanford.common.util.AnimScaleInUtil;


public class DrawDoubleTextView extends ConstraintLayout {
    private String titleText,descTxt;
    private int titleTextColor;
    private float titleTextSize;
    private TextView tvNum;
    private TextView tvDesc;
    int resourceId = -1;

    public DrawDoubleTextView(Context context) {
        this(context, null);
    }

    public DrawDoubleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public DrawDoubleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //加载视图的布局
        LayoutInflater.from(context).inflate(R.layout.layout_draw_double_text, this, true);

        //加载自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawDoubleText);
        titleText = a.getString(R.styleable.DrawDoubleText_titleText1);
        titleTextColor = a.getColor(R.styleable.DrawDoubleText_titleTextColor1, Color.RED);
        titleTextSize = a.getDimension(R.styleable.DrawDoubleText_titleTextSize1, 20f);


        descTxt =  a.getString(R.styleable.DrawDoubleText_descText);

        //回收资源，这一句必须调用
        a.recycle();
    }

    /**
     * 此方法会在所有的控件都从xml文件中加载完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取子控件
        tvNum = findViewById(R.id.tv_count);
        tvDesc = findViewById(R.id.tv_desc);

        //将从资源文件中加载的属性设置给子控件
        if (!TextUtils.isEmpty(titleText))
            setPageTitleText(titleText);
        setThumb(descTxt);
    }


    /**
     * 设置标题文字
     *
     * @param text
     */
    public void setPageTitleText(String text) {
        tvNum.setText(text);

    }

    public void setThumb(String str) {
        tvDesc.setText(str);


    }


}
