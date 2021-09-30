package com.changanford.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class EvosToolBar extends Toolbar {
    public EvosToolBar(@NonNull Context context) {
        super(context);
    }

    public EvosToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EvosToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
