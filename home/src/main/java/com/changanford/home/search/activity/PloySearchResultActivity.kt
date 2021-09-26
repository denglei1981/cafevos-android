package com.changanford.home.search.activity

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.home.R
import com.changanford.home.databinding.ActivityPloySearchResultBinding
import com.changanford.home.search.adapter.SearchResultViewpagerAdapter
import com.changanford.home.search.fragment.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar


@Route(path = ARouterHomePath.PloySearchResultActivity)
class PloySearchResultActivity : BaseActivity<ActivityPloySearchResultBinding, EmptyViewModel>() {

    var pagerAdapter: SearchResultViewpagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()


    override fun initView() {
        ImmersionBar.with(this).fitsSystemWindows(true)
        fragmentList.add(SearchActsFragment.newInstance())
        fragmentList.add(SearchNewsFragment.newInstance())
        fragmentList.add(SearchPostFragment.newInstance())
        fragmentList.add(SearchShopFragment.newInstance())
        fragmentList.add(SearchUserFragment.newInstance())
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_search_shop))
        titleList.add(getString(R.string.home_search_post))
        titleList.add(getString(R.string.home_search_user))
        pagerAdapter = SearchResultViewpagerAdapter(this, fragmentList)
        binding.viewpager.adapter = pagerAdapter

        binding.viewpager.isSaveEnabled = false
        binding.searchTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.searchTab.tabRippleColor = null
//        setAppbarPercent()

        TabLayoutMediator(binding.searchTab, binding.viewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }
        binding.searchTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTab(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        var mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 15f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗

        }
    }

    var itemPunchWhat: Int = 0

    //初始化tab
    private fun initTab() {
        for (i in 0 until binding.searchTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_home, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)

            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 15f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
            //更改选中项样式
            //设置样式
            binding.searchTab.getTabAt(i)?.customView = view
        }
    }

    override fun initData() {

    }
}