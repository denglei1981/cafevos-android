package com.changanford.common.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.ui.viewpager.BannerHelper
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/7 14:37
 * @Description: 实现无限循环Banner效果
 * *********************************************************************************
 */


private class MyCircleBannerAdapterForActivity(
    fragment: FragmentActivity,
    private val dataSize: Int,
    val createF: (Int) -> Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =
        dataSize + 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                createF(dataSize - 1)
            }
            dataSize + 1 -> {
                createF(0)
            }
            else -> createF(position - 1)
        }
    }
}

private class MyCircleBannerAdapterForFragment(
    fragment: Fragment,
    private val dataSize: Int,
    val createF: (Int) -> Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =
        dataSize + 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            dataSize + 1 -> {
                createF(0)
            }
            0 -> {
                createF(dataSize - 1)
            }
            else -> createF(position - 1)
        }
    }
}

/**
 * 设置无限循环关系
 * 在列表的前后各加一个替代元素：
 * 原：  0 1 2 3
 * 实：3 0 1 2 3 0
 * 当滑动到第一个或最后一个，分别和倒数第二和第二个交换
 */
private class MyPageChangeCallback(private val dataSize: Int, private val viewPager2: ViewPager2) :
    ViewPager2.OnPageChangeCallback() {
    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager2.SCROLL_STATE_IDLE) {
            when (viewPager2.currentItem) {
                dataSize + 1 -> {
                    doChange(1, false)
                }
                0 -> {
                    doChange(dataSize, false)
                }
            }
        }
    }

    private fun doChange(i: Int, scroll: Boolean = true) {
        viewPager2.setCurrentItem(i, scroll)
    }
}

suspend fun ViewPager2.bindAdapter(
    fragment: Fragment,
    dataSize: Int, createF: (Int) -> Fragment
) {
    //判断空
    if (dataSize <= 0) return
    adapter = MyCircleBannerAdapterForFragment(fragment, dataSize) {
        createF(it)
    }

    registerOnPageChangeCallback(
        MyPageChangeCallback(
            dataSize,
            this
        )
    )
    //设置当前为第一条
    currentItem = 1
    //设置循环播放
    while (true) {
        delay(3000)
        withContext(Dispatchers.Main) {
            if (scrollState == ViewPager2.SCROLL_STATE_IDLE) {
                currentItem += 1
            }
        }
    }
}

fun ViewPager2.bindAdapter(
    fragment: FragmentActivity,
    dataSize: Int, createF: (Int) -> Fragment
) {
    this.adapter = MyCircleBannerAdapterForActivity(fragment, dataSize) {
        createF(it)
    }
    this.registerOnPageChangeCallback(
        MyPageChangeCallback(
            dataSize,
            this
        )
    )
    this.currentItem = 1
}