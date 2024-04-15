package com.changanford.common.widget.recy;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: niubobo
 * @date: 2024/4/11
 * @description：
 */
public class CustomRecyclerView  extends RecyclerView {
    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (MeasureSpec.getMode(heightSpec) == MeasureSpec.UNSPECIFIED) {
            int size = MeasureSpec.getSize(heightSpec);
            heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

//    @Override
//    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
//        child.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);
//    }

}
