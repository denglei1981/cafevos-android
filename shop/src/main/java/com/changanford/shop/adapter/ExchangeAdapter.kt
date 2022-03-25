package com.changanford.shop.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.changanford.shop.R
import com.changanford.shop.base.BaseAdapter
import com.changanford.shop.databinding.ItemShopExchangeFooterBinding
import com.changanford.shop.ui.goods.ExchangeListFragment
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeAdapter
 */
class ExchangeAdapter(val context: Context, private val fragmentActivity: FragmentActivity):BaseAdapter<String>(context, Pair(R.layout.item_shop_exchange_header, 0), Pair(R.layout.item_shop_exchange_footer, 1)) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
            }
            1 -> initFooterView(vdBinding as ItemShopExchangeFooterBinding)
        }
    }
    override fun getItemViewType(position: Int): Int { return position }

    private fun initFooterView(binding:ItemShopExchangeFooterBinding){
        val tabTitles = arrayOf("Tab0", "Tab1", "Tab2")
        val fragments= arrayListOf<Fragment>(
            ExchangeListFragment.newInstance(""),
            ExchangeListFragment.newInstance(""),
            ExchangeListFragment.newInstance(""))
        binding.viewpager.adapter= ViewPage2Adapter(fragmentActivity,fragments)
//        binding.viewpager.isSaveEnabled=false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabTitles[tabPosition]
        }.attach()
    }

}