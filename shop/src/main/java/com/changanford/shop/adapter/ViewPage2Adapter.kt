package com.changanford.shop.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @Author : wenke
 * @Time : 2021/9/8
 * @Description : FragmentAdapter
 */
class ViewPage2Adapter(fragmentActivity: FragmentActivity, private val fragments:List<Fragment>): FragmentStateAdapter (fragmentActivity){
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}