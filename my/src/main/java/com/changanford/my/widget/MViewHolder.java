package com.changanford.my.widget;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.my.R;

import cn.we.swipe.helper.WeSwipeHelper;

/**
 * 文件名：MViewHolder
 * 创建者: zcy
 * 创建日期：2021/10/28 15:10
 * 描述: TODO
 * 修改描述：TODO
 */
public class MViewHolder extends BaseViewHolder implements WeSwipeHelper.SwipeLayoutTypeCallBack {

    public TextView slide;
    public LinearLayout llContent;
    public AppCompatTextView title;
    public AppCompatTextView time;
    public CheckBox checkBox;

    public MViewHolder(@NonNull View view) {
        super(view);
        slide = view.findViewById(R.id.item_slide);
        llContent = view.findViewById(R.id.ll_content);
        title = view.findViewById(R.id.item_title);
        time = view.findViewById(R.id.item_time);
        checkBox = view.findViewById(R.id.checkbox);
    }

    @Override
    public float getSwipeWidth() {
        return slide.getWidth();
    }

    @Override
    public View needSwipeLayout() {
        return llContent;
    }

    @Override
    public View onScreenView() {
        return llContent;
    }
}
