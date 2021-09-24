package com.changanford.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.ui.viewpager.BannerFragment
import com.changanford.common.ui.viewpager.bindAdapter
import com.changanford.home.databinding.FragmentFirstBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : BaseFragment<FragmentFirstBinding, HomeViewModule>() {


    override fun initView() {
        //Tab+Fragment
        binding.homeViewpager?.adapter = HomeViewPagerAdapter(this)
        binding.homeViewpager.isSaveEnabled = false
        TabLayoutMediator(binding.hometab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = "Fragment $i"
            if (i == 1) {
                val badge = tab.orCreateBadge
                badge.number = 22
                badge.isVisible = true
            }
        }.attach()
        binding.homeViewpager.setOnClickListener {
//            NavController.
        }
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

    inner class HomeViewPagerAdapter(fragmentActivity: Fragment) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return BannerFragment()
        }
    }


}