package com.changanford.my.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.changanford.my.R;
import com.changanford.my.utils.DensityUtil;


/**
 * 创建信息： zcy on 2018/11/13.
 * 描    述：
 * 更新说明：
 */
public class MineView extends LinearLayout {

    public Context context;

    private boolean isShowBottomLine = true;//是否显示底部的线
    private boolean isShowLeftIcon = true;//是否显示left图片
    private boolean isShowRightArrow = true;//是否显示right图片

    private ImageView leftIcon;//left图片
    private TextView leftTitle;//
    private int leftTextColor;//左边字体颜色


    private ImageView rightArrow;//right图片
    private TextView rightTitle;//right标题
    private ImageView msg;//红点
    private int rightTextColor;//右边字体颜色

    private RelativeLayout layout;

    private View bottomLine;// 线
    private int lienLeftRightMargin;

    private int lineSize;// 下划线高度

    private int lineColor;// 下划线颜色

    public MineView(Context context) {
        this(context, null);
    }

    public MineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MineView);

        isShowBottomLine = ta.getBoolean(R.styleable.MineView_show_bottom_line, true);//得到是否显示底部下划线属性
        isShowLeftIcon = ta.getBoolean(R.styleable.MineView_show_left_icon, true);//得到是否显示左侧图标属性标识
        isShowRightArrow = ta.getBoolean(R.styleable.MineView_show_right_arrow, true);//得到是否显示右侧图标属性标识

        leftIcon.setImageDrawable(ta.getDrawable(R.styleable.MineView_left_icon));//设置左侧图标
        rightArrow.setImageDrawable(ta.getDrawable(R.styleable.MineView_right_icon));//设置右侧图标
        leftTitle.setText(ta.getString(R.styleable.MineView_left_text));//设置左侧标题文字
        leftIcon.setVisibility(isShowLeftIcon ? View.VISIBLE : View.GONE);//设置左侧箭头图标是否显示

        rightTitle.setText(ta.getString(R.styleable.MineView_right_text));//设置右侧文字描述
        bottomLine.setVisibility(isShowBottomLine ? View.VISIBLE : View.INVISIBLE);//设置底部图标是否显示
        rightArrow.setVisibility(isShowRightArrow ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示

        lineColor =
                ta.getColor(R.styleable.MineView_bottom_line_color, Color.parseColor("#f2f2f2"));//下划线颜色
        bottomLine.setBackgroundColor(lineColor);

        leftTextColor = ta.getColor(R.styleable.MineView_left_text_color, Color.parseColor("#000000"));
        leftTitle.setTextColor(leftTextColor);

        rightTextColor = ta.getColor(R.styleable.MineView_right_text_color, Color.parseColor("#333333"));
        rightTitle.setTextColor(rightTextColor);


        lineSize = ta.getInteger(R.styleable.MineView_bottom_line_size, 1);// 线的高度 默认1
        lienLeftRightMargin = ta.getInteger(R.styleable.MineView_line_left_right_margin, 1);
        LayoutParams params =
                new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, lineSize));
        params.leftMargin = DensityUtil.dip2px(context, lienLeftRightMargin);
        params.rightMargin = DensityUtil.dip2px(context, lienLeftRightMargin);
        bottomLine.setLayoutParams(params);

        int minHeight = ta.getInt(R.styleable.MineView_min_height, 44);
        //要显示左边图标时 最小高度为48
        if (isShowLeftIcon && minHeight < 44) {
            layout.setMinimumHeight(DensityUtil.dip2px(context, 44));
        } else {
            layout.setMinimumHeight(DensityUtil.dip2px(context, minHeight));
        }

        ta.recycle();//回收
        setGravity(Gravity.CENTER_VERTICAL);
    }

    // 初始化layout
    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout._view_mine_item, null);
        leftIcon = view.findViewById(R.id.left_icon);
        leftTitle = view.findViewById(R.id.left_text);
        rightArrow = view.findViewById(R.id.right_icon);
        rightTitle = view.findViewById(R.id.right_text);
        msg = view.findViewById(R.id.have_message);
        bottomLine = view.findViewById(R.id.line);
        layout = view.findViewById(R.id.layout);
        addView(view);
    }

    //设置左侧图标
    public void setLeftIcon(int value) {
        Drawable drawable = getResources().getDrawable(value);
        leftIcon.setImageDrawable(drawable);
    }

    //设置左侧标题文字
    public void setLeftTitle(String value) {
        leftTitle.setText(value);
    }

    //设置右侧描述文字
    public void setRightDesc(String value) {
        rightTitle.setText(value);
    }

    public void setMSGVisible(boolean visible) {
        if (visible) {
            msg.setVisibility(View.VISIBLE);
        } else {
            msg.setVisibility(View.GONE);
        }
    }

    public TextView getRightTitle() {
        return rightTitle;
    }

    //设置右侧描述文字
    public String getRightDesc() {
        return rightTitle.getText().toString();
    }

    //设置右侧箭头
    public void setShowRightArrow(boolean value) {
        rightArrow.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }

    //设置是否显示下画线
    public void setShowBottomLine(boolean value) {
        bottomLine.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }
}
