package com.changanford.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.ui.viewpager.BannerFragment
import com.changanford.common.ui.viewpager.bindAdapter
import com.changanford.home.acts.fragment.ActsListFragment
import com.changanford.home.databinding.FragmentFirstBinding
import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.shot.fragment.BigShotFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : BaseFragment<FragmentFirstBinding, HomeViewModule>() {

    var pagerAdapter:HomeViewPagerAdapter?=null

    var fragmentList:ArrayList<Fragment> = arrayListOf()

    var titleList= mutableListOf<String>()

    override fun initView() {
        //Tab+Fragment


        fragmentList.add(RecommendFragment.newInstance())
        fragmentList.add(NewsListFragment.newInstance())
        fragmentList.add(ActsListFragment.newInstance())
        fragmentList.add(BigShotFragment.newInstance())

        titleList.add(getString(R.string.recommend))
        titleList.add(getString(R.string.acts))
        titleList.add(getString(R.string.news))
        titleList.add(getString(R.string.big_shot))

        pagerAdapter=HomeViewPagerAdapter(this,fragmentList)
        binding.homeViewpager.adapter = pagerAdapter;

        binding.homeViewpager.isSaveEnabled = false
        TabLayoutMediator(binding.hometab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]
        }.attach()
    }

    override fun initData() {
        //获取banner数据
        lifecycleScope.launch {
            var data = viewModel.getAdList("recommend_banner")
            data?.let { initBanner(it) }
        }
    }

    private suspend fun initBanner(data: List<AdBean>) {
        //banner的使用
        binding.homeBanner?.isSaveEnabled = false
        binding.homeBanner?.bindAdapter(this@HomeFragment, data.size) {
            val homeBannerItem = HomeBannerItem()
            val bundle = Bundle()
            bundle.putSerializable("data", data[it])
            homeBannerItem.arguments = bundle
            homeBannerItem
        }
    }




}