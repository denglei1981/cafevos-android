package com.changanford.common.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.ui.viewpager.ViewPagerHelper
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/8 17:29
 * @Description: 　
 * *********************************************************************************
 */

/**
 * 普通的ViewPager2直接设置adapter
 * @param fragment this
 * @param dataSize 需要建多少Fragment
 * @param createF 每个position对应的Fragment
 */
class MyViewPagerAdapterForActivity(
    fragment: FragmentActivity,
    val dataSize: Int,
    val createF: (Int) -> Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =
        dataSize

    override fun createFragment(position: Int): Fragment {
        return createF(position)
    }
}


/**
 * 普通的ViewPager2直接设置adapter
 * @param fragment this
 * @param dataSize 需要建多少Fragment
 * @param createF 每个position对应的Fragment
 */
class MyViewPagerAdapterForFragment(
    fragment: Fragment,
    val dataSize: Int,
    val createF: (Int) -> Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =
        dataSize

    override fun createFragment(position: Int): Fragment {
        return createF(position)
    }
}
