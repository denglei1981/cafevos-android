package com.changanford.my.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MtViewPagerAdapter(fragment: FragmentActivity, var fragmentList: MutableList<Fragment>) :

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