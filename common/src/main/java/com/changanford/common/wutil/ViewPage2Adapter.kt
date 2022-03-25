package com.changanford.common.wutil

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @Author : wenke
 * @Time : 2021/9/8
 * @Description : FragmentAdapter
 */
class ViewPage2AdapterFragment(fragment: Fragment, private val fragments:List<Fragment>): FragmentStateAdapter (fragment){
    override fun getItemCount(): Int {
        return fragments.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
    override fun containsItem(itemId: Long): Boolean {
        return itemId>=0&&itemId<getItemId(itemId.toInt())
    }
}
class ViewPage2AdapterAct(fragmentActivity: FragmentActivity, var fragmentList: MutableList<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
    override fun containsItem(itemId: Long): Boolean {
        return itemId>=0&&itemId<getItemId(itemId.toInt())
    }
}