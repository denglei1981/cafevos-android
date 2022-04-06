package com.changanford.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.changanford.common.R;


/**
 *
 *  自定义控件，  3头部。
 * */

public class LayoutNameMoneyLayout extends ConstraintLayout {

    TextView tvOne,tvSecond ,tvName;

    String nameStr, OneStr,secondStr;
    int nameColor, oneColor,secondColor;
    float nameSize,oneSize,secondSize;

    public LayoutNameMoneyLayout(@NonNull Context context) {
        this(context,null);
    }

    public LayoutNameMoneyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LayoutNameMoneyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //加载视图的布局
        LayoutInflater.from(context).inflate(R.layout.layout_name_money, this, true);

        //加载自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LayoutNameMoneyLayout);

        if(a!=null){
            nameStr = a.getString(R.styleable.LayoutNameMoneyLayout_nameStr);
            nameColor = a.getColor(R.styleable.LayoutNameMoneyLayout_nameTextColor, ContextCompat.getColor(context,R.color.gray_999999));
            nameSize = a.getDimension(R.styleable.LayoutNameMoneyLayout_nameTextSize, 20f);
            OneStr = a.getString(R.styleable.LayoutNameMoneyLayout_oneStr);
            oneColor = a.getColor(R.styleable.LayoutNameMoneyLayout_oneTextColor, ContextCompat.getColor(context,R.color.gray_999999));
            oneSize = a.getDimension(R.styleable.LayoutNameMoneyLayout_oneTextSize, 20f);


            secondStr = a.getString(R.styleable.LayoutNameMoneyLayout_secondStr);
            secondColor = a.getColor(R.styleable.LayoutNameMoneyLayout_secondTextColor, ContextCompat.getColor(context,R.color.gray_999999));
            secondSize = a.getDimension(R.styleable.LayoutNameMoneyLayout_secondTextSize, 20f);
            //回收资源，这一句必须调用
            a.recycle();
        }


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvOne= findViewById(R.id.tv_earn_money);
        tvSecond= findViewById(R.id.tv_earn_today);
        tvName =findViewById(R.id.tv_user_name);

        if(!TextUtils.isEmpty(OneStr)){
            setOneText(OneStr);
        }
        setOneColor(oneColor);
        if(!TextUtils.isEmpty(nameStr)){
            setNameText(nameStr);
        }
        setNameColor(nameColor);
        if(!TextUtils.isEmpty(secondStr)){
            setSecondText(secondStr);
        }
        setSecondColor(secondColor);


    }
    public void setSecondText(String desc){
        tvSecond.setText(desc);
    }
    public void setOneText(String desc){
        tvOne.setVisibility(View.VISIBLE);
        tvOne.setText(desc);
    }
    public void setOneColor(int color){
        tvOne.setTextColor(color);
    }


    public void setNameText(String desc){
        tvName.setText(desc);
    }
    public void setNameColor(int color){
        tvName.setTextColor(color);
    }


    public void setSecondColor(int color){
        tvSecond.setTextColor(color);
    }
}
