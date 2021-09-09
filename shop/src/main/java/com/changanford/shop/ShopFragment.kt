package com.changanford.shop
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.databinding.FragmentShopBinding
import com.changanford.shop.ui.IntegralDetailsActivity
import com.changanford.shop.ui.exchange.ExchangeFragment
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopBinding, ShopViewModel>(), View.OnClickListener {
    private val tabTitles by lazy {arrayOf(getString(R.string.str_pointsFor), getString(R.string.str_earnPoints))}
    private val fragments= arrayListOf<Fragment>(ExchangeFragment.newInstance(),ExchangeFragment.newInstance())
    override fun initView() {
        binding.viewpager.adapter= ViewPage2Adapter(requireActivity(),fragments)
        binding.viewpager.isSaveEnabled=false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabTitles[tabPosition]
        }.attach()
        binding.tvShopIntegral.setOnClickListener(this)
    }
    override fun initData() {

    }
    override fun onClick(v: View?) {
        when(v?.id){
            //积分明细
            R.id.tv_shop_integral->startActivity(Intent(requireContext(),IntegralDetailsActivity::class.java))
        }
    }
}

