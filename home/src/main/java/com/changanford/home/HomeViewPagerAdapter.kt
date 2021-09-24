package com.changanford.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(fragment: Fragment, var fragmentList: MutableList<Fragment>) :

    FragmentStateAdapter(fragment) {

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