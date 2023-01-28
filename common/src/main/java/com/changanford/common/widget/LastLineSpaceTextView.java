package com.changanford.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Author lcw
 * Time on 2023/1/28
 * Purpose
 */
public class LastLineSpaceTextView extends AppCompatTextView {
    private Rect mLastLineRect;
    private Rect mLastLineIndexRect;


    public LastLineSpaceTextView(Context context) {
        this(context, null);
    }

    public LastLineSpaceTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public LastLineSpaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLastLineRect = new Rect();
        mLastLineIndexRect = new Rect();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() - calculateSpace());
    }


    private int calculateSpace() {
        int result = 0;
        //界面显示的最后一行的index
        int lastlineIndex = Math.min(getMaxLines(), getLineCount()) - 1;
        //获取实际的最后一行
        int lastLineNomalIndex = getLineCount() - 1;

        if (lastlineIndex >= 0) {
            Layout layout = getLayout();
            int baseline = getLineBounds(lastlineIndex, mLastLineRect);
            getLineBounds(lastLineNomalIndex, mLastLineIndexRect);
            int height = (mLastLineIndexRect.bottom - mLastLineRect.bottom);


            if (getMeasuredHeight() == getLayout().getHeight() - height) {
                if (getLineSpacingExtra() == 0) {
                    result = 0;
                } else {
                    result = mLastLineRect.bottom - (baseline + layout.getPaint().getFontMetricsInt().descent + getPaddingBottom());
                    if (getLineSpacingExtra() > result) {
                        result = 0;
                    } else {
                        result = (int) getLineSpacingExtra();
                    }
                }

            }

        }
        return result;
    }

}
