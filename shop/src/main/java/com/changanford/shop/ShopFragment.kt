package com.changanford.shop
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.exchange.ExchangeListFragment
import com.changanford.shop.utils.WCommonUtil
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopLayoutBinding, ShopViewModel>() {
    override fun initView() {
        val tabTitles= arrayListOf<String>()
        val fragments= arrayListOf<Fragment>()
        for(i in 0..20){
            tabTitles.add("Tab$i")
            fragments.add(ExchangeListFragment.newInstance())
        }
        binding.viewpager.adapter= ViewPage2Adapter(requireActivity(),fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabTitles[tabPosition]
        }.attach()
        WCommonUtil.setTabSelectStyle(requireContext(),binding.tabLayout,16f, Typeface.DEFAULT_BOLD,R.color.color_33)
    }
    override fun initData() {

    }
}

