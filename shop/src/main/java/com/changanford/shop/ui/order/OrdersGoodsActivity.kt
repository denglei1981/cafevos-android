package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.R
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.databinding.ActGoodsOrderBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author : wenke
 * @Time : 2021/9/24
 * @Description : 商品订单
 */
@Route(path = ARouterShopPath.OrderGoodsActivity)
class OrdersGoodsActivity:BaseActivity<ActGoodsOrderBinding, OrderViewModel>() {
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, OrdersGoodsActivity::class.java))
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
        initTab()
    }

    override fun initData() {

    }
    private fun initTab(){
        val tabTitles= arrayListOf(getString(R.string.str_all),getString(R.string.str_toBePaid),getString(R.string.str_toSendGoods),getString(R.string.str_forGoods),getString(R.string.str_toEvaluate))
        val fragments= arrayListOf<Fragment>()
        for(i in tabTitles.withIndex()){
            fragments.add(OrdersGoodsFragment.newInstance("$i"))
        }
        binding.viewPager2.adapter= ViewPage2Adapter(this,fragments)
        binding.viewPager2.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, tabPosition ->
            tab.text = tabTitles[tabPosition]
        }.attach()
    }
}