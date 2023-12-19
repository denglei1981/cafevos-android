package com.changanford.home.widget;

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

import com.changanford.home.R;
import com.changanford.home.util.AnimScaleInUtil;


public class DrawVBTextView extends ConstraintLayout {
    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    private TextView tvNum;
    private ImageView ivThumb;
    int resourceId = -1;

    public DrawVBTextView(Context context) {
        this(context, null);
    }

    public DrawVBTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public DrawVBTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //加载视图的布局
        LayoutInflater.from(context).inflate(R.layout.layout_draw_vb, this, true);

        //加载自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopBottomText);
        titleText = a.getString(R.styleable.TopBottomText_titleText);
        titleTextColor = a.getColor(R.styleable.TopBottomText_titleTextColor, Color.GRAY);
        titleTextSize = a.getDimension(R.styleable.TopBottomText_titleTextSize, 20f);
        resourceId = a.getResourceId(R.styleable.TopBottomText_image, R.drawable.icon_home_look_count);

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
        ivThumb = findViewById(R.id.iv_thumbs);

        //将从资源文件中加载的属性设置给子控件
        if (!TextUtils.isEmpty(titleText))
            setPageTitleText(titleText);
        setThumb(resourceId, false);
        tvNum.setTextColor(titleTextColor);

    }


    /**
     * 设置标题文字
     *
     * @param text
     */
    public void setPageTitleText(String text) {
        tvNum.setText(text);

    }

    public void setThumb(int resourceId, boolean isNeedAnim) {
        ivThumb.setImageResource(resourceId);
        if (isNeedAnim) {
            AnimScaleInUtil.INSTANCE.animScaleIn(ivThumb);
        }

    }

    public ImageView getImageview(){
        return ivThumb;
    }

}
