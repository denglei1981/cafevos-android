package com.changanford.shop.ui.goods

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.wutil.ViewPage2AdapterAct
import com.changanford.shop.R
import com.changanford.shop.databinding.ActRecommendBinding
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.net.URLDecoder

/**
 * @Author : wenke
 * @Time : 2022/3/18
 * @Description : 商品推荐
 */
@Route(path = ARouterShopPath.RecommendActivity)
class RecommendActivity : BaseActivity<ActRecommendBinding, GoodsViewModel>() {
    companion object {
        fun start(kindName: String? = "") {
            val bundle = Bundle()
            bundle.putString("value", kindName)
            RouterManger.startARouter(ARouterShopPath.RecommendActivity, bundle)
        }
    }

    private val fragments = ArrayList<Fragment>()

    override fun onResume() {
        super.onResume()
        updateMainGio("推荐榜单页", "推荐榜单页")
    }

    override fun initView() {
        binding.topBar.setActivity(this)
        WCommonUtil.setTabSelectStyle(
            this,
            binding.tabLayout,
            18f,
            Typeface.DEFAULT,
            R.color.color_1700f4,
            true
        )
    }

    override fun initData() {
        val defaultKindName = intent.getStringExtra("value")
        viewModel.typesBean.observe(this) {
            bindTab(it)
            val index = if (!TextUtils.isEmpty(defaultKindName)) it.indexOfFirst { item ->
                item.kindName == URLDecoder.decode(
                    defaultKindName,
                    "UTF-8"
                )
            } else 0
            if (index > 0) binding.viewPager2.currentItem = index
        }
        viewModel.getRecommendTypes()
    }

    private fun bindTab(tabs: MutableList<GoodsTypesItemBean>) {
        fragments.clear()
        binding.tabLayout.removeAllTabs()
        for (it in tabs) {
            fragments.add(RecommendFragment.newInstance(it.kindId))
        }
        binding.viewPager2.apply {
            offscreenPageLimit = 4
            adapter = ViewPage2AdapterAct(this@RecommendActivity, fragments)
            isSaveEnabled = false
            TabLayoutMediator(binding.tabLayout, this) { tab, tabPosition ->
                tab.text = tabs[tabPosition].kindName
            }.attach()
        }
    }
}