package com.changanford.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhpan.bannerview.BannerViewPager;

/**
 * @author: niubobo
 * @date: 2024/4/16
 * @description：
 */
public class NoInterBanner<T>  extends BannerViewPager<T> {
    public NoInterBanner(Context context) {
        super(context);
    }

    public NoInterBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoInterBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public BannerViewPager<T> disallowParentInterceptDownEvent(boolean disallowParentInterceptDownEvent) {
        return super.disallowParentInterceptDownEvent(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
