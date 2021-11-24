package com.changanford.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 文件名：MyEditText
 * 创建者: zcy
 * 创建日期：2020/6/2 18:23
 * 描述: TODO
 * 修改描述：TODO
 */
public class PassInputEditText extends AppCompatEditText {

    private long lastTime = 0;

    public PassInputEditText(Context context) {
        super(context);
    }

    public PassInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PassInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        this.setSelection(this.getText().length());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime < 500) {
                    lastTime = currentTime;
                    return true;
                } else {
                    lastTime = currentTime;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
