package com.changanford.shop.ui.goods

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.wutil.ViewPage2AdapterAct
import com.changanford.shop.R
import com.changanford.shop.databinding.ActRecommendBinding
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author : wenke
 * @Time : 2022/3/18
 * @Description : 商品推荐
 */
@Route(path = ARouterShopPath.RecommendActivity)
class RecommendActivity:BaseActivity<ActRecommendBinding,GoodsViewModel>() {
    companion object{
        fun start(kindId:String?="0"){
            val bundle= Bundle()
            bundle.putString("kindId", kindId)
            RouterManger.startARouter(ARouterShopPath.RecommendActivity,bundle)
        }
    }
    private val fragments=ArrayList<Fragment>()
    override fun initView() {
        binding.topBar.setActivity(this)
        WCommonUtil.setTabSelectStyle(this,binding.tabLayout,18f, Typeface.DEFAULT_BOLD,R.color.color_00095B)
    }
    override fun initData() {
        val defaultKindId=intent.getStringExtra("kindId")
        viewModel.typesBean.observe(this){
            bindTab(it)
            val index = it.indexOfFirst { item -> item.kindId == defaultKindId }
            if (index>0) binding.viewPager2.currentItem =index
        }
        viewModel.getRecommendTypes()
    }
    private fun bindTab(tabs:MutableList<GoodsTypesItemBean>){
        fragments.clear()
        binding.tabLayout.removeAllTabs()
        for(it in tabs){
            fragments.add(RecommendFragment.newInstance(it.kindId))
        }
        binding.viewPager2.apply {
            offscreenPageLimit=4
            adapter= ViewPage2AdapterAct(this@RecommendActivity,fragments)
            isSaveEnabled = false
            TabLayoutMediator(binding.tabLayout, this) { tab, tabPosition ->
                tab.text = tabs[tabPosition].kindName
            }.attach()
        }
    }
}