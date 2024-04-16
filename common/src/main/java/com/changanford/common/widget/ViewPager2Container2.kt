package com.changanford.common.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.R
import com.zhpan.bannerview.BannerViewPager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class ViewPager2Container2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mViewPager2: ViewPager2? = null
    private var disallowParentInterceptDownEvent = true
    private var startX = 0
    private var startY = 0

    //遍历ViewPager2Container 的所有子 View，如果没有找到 ViewPager2 就抛出异常
    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView is BannerViewPager<*>) {
                mViewPager2 = childView.findViewById(R.id.vp_main)
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    delay(1000)
                    mViewPager2?.isNestedScrollingEnabled=false
                }
            }
            if (childView is ViewPager2) {
                mViewPager2 = childView
                break
            }
        }
        if (mViewPager2 == null) {
            throw IllegalStateException("The root child of ViewPager2Container must contains a ViewPager2")
        }
    }



}