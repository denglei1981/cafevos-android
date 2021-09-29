package com.changanford.common.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 文件名：RoundCheckBox
 * 创建者: zcy
 * 创建日期：2020/5/16 17:28
 * 描述: TODO
 * 修改描述：TODO
 */
public class RoundCheckBox extends AppCompatCheckBox {

    public RoundCheckBox(Context context) {
        this(context, null);
    }

    public RoundCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.radioButtonStyle);
    }

    public RoundCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
