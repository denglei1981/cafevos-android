package com.changanford.my.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.changanford.common.util.JumpUtils;
import com.changanford.common.utilext.GlideUtils;
import com.changanford.my.R;

import java.util.Objects;


public class FlyCirclePost extends ConstraintLayout {
    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    public  TextView tvNum;
    public ImageView ivThumb;
    int resourceId = -1;

    public FlyCirclePost(Context context) {
        this(context, null);
    }

    public FlyCirclePost(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public FlyCirclePost(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //加载视图的布局
        LayoutInflater.from(context).inflate(R.layout.item_fl_circle_post, this, true);

        //获取子控件
        tvNum = findViewById(R.id.tv_circle_desc);
        ivThumb = findViewById(R.id.iv_circle);




    }

    /**
     * 此方法会在所有的控件都从xml文件中加载完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();



    }


    /**
     * 设置标题文字
     *
     * @param text
     */
    public void setPageTitleText(String text) {
        tvNum.setText(text);
    }

    public void setThumb(String resourceId,int postId) {
        GlideUtils.INSTANCE.loadBD(resourceId,ivThumb);
          this.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                  Objects.requireNonNull(JumpUtils.getInstans()).jump(4,String.valueOf(postId));
              }
          });
    }


}
