package com.changanford.circle.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.changanford.circle.R;


public class CreateLocationTextView extends ConstraintLayout {
    private String titleText,hintText;

    private TextView tvLocation;

    private EditText etInput;



    public CreateLocationTextView(Context context) {
        this(context, null);
    }

    public CreateLocationTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public CreateLocationTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //加载视图的布局
        LayoutInflater.from(context).inflate(R.layout.layout_create_location, this, true);

        //加载自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CreateLocationText);
        titleText = a.getString(R.styleable.CreateLocationText_locationTexts);

        hintText=a.getString(R.styleable.CreateLocationText_hintText);
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
        tvLocation = findViewById(R.id.tv_location_name);

         etInput=findViewById(R.id.et_txt);
        //将从资源文件中加载的属性设置给子控件
        if (!TextUtils.isEmpty(titleText))
            setPageTitleText(titleText);
        if(!TextUtils.isEmpty(hintText)){
            setEtInputHint(hintText);
        }

    }


    /**
     * 设置标题文字
     *
     * @param text
     */
    public void setPageTitleText(String text) {
        tvLocation.setText(text);
    }

    public void setEtInputHint(String hintText){
        etInput.setHint(hintText);
    }



}
