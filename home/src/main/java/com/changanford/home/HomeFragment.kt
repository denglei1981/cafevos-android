package com.changanford.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.acts.fragment.ActsListFragment
import com.changanford.home.databinding.FragmentHomeManagerBinding

import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.shot.fragment.BigShotFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : BaseFragment<FragmentHomeManagerBinding, EmptyViewModel>() {

    var pagerAdapter: HomeViewPagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()

    override fun initView() {
        //Tab+Fragment
        StatusBarUtil.setColor(requireActivity(), Color.WHITE)
        fragmentList.add(RecommendFragment.newInstance())
        fragmentList.add(NewsListFragment.newInstance())
        fragmentList.add(ActsListFragment.newInstance())
        fragmentList.add(BigShotFragment.newInstance())

        titleList.add(getString(R.string.home_recommend))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_big_shot))

        pagerAdapter = HomeViewPagerAdapter(this, fragmentList)
        binding.homeViewpager.adapter = pagerAdapter

        binding.homeViewpager.isSaveEnabled = false

        binding.hometab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.transparent
            )
        )
        binding.hometab.tabRippleColor=null

        TabLayoutMediator(binding.hometab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }
        binding.hometab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        var line = tab.customView?.findViewById<View>(R.id.line)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
            line?.visibility = View.VISIBLE
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 15f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗
            line?.visibility = View.INVISIBLE
        }
    }
    var itemPunchWhat: Int = 0
    //初始化tab
    private fun initTab() {
        for (i in 0 until binding.hometab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_home, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)
            val line = view.findViewById<View>(R.id.line)
            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f
                line.visibility = View.VISIBLE
            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 15f
                mTabText.paint.isFakeBoldText = false// 取消加粗
                line.visibility = View.INVISIBLE
            }
            //更改选中项样式
            //设置样式
            binding.hometab.getTabAt(i)?.customView = view
        }
    }

    override fun initData() {

    }




}