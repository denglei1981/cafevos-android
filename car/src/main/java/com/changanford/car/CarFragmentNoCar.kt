package com.changanford.car

import com.changanford.car.adapter.CarAuthAdapter
import com.changanford.car.adapter.CarRecommendAdapter
import com.changanford.car.adapter.CarTopBannerAdapter
import com.changanford.car.databinding.CarFragmentNocarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.logE


class CarFragmentNoCar : BaseFragment<CarFragmentNocarBinding, CarViewModel>() {
    var carTopBanner = CarTopBannerAdapter()
    var carRecommendAdapter = CarRecommendAdapter()
    private var carAuthAdapter = CarAuthAdapter()
    var topBannerList = ArrayList<String>()

    override fun initView() {

        topBannerList.add("uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png")
        topBannerList.add("uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png")
        topBannerList.add("uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png")

        binding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            setIndicatorView(binding.drIndicator)
            setOnPageClickListener {
                //todo
            }
            setIndicatorView(binding.drIndicator)
        }.create(topBannerList)
        binding.drIndicator
            .setIndicatorGap(20)
            .setIndicatorDrawable(R.drawable.indicator_unchecked,R.drawable.indicator_checked)

        binding.carTopViewPager.isSaveEnabled = false
        binding.carAuthrec.isSaveEnabled = false
        binding.carRecommendLayout.carRecommendRec.isSaveEnabled = false
        binding.carAuthrec.adapter = carAuthAdapter
        binding.carRecommendLayout.carRecommendRec.adapter = carRecommendAdapter

        binding.carNoauthLayout.button.setOnClickListener {
            JumpUtils.instans?.jump(17, "")
        }
    }

    override fun initData() {
        viewModel.getTopAds()
        viewModel._ads.observe(this, {
            "中间页广告数量${it.size}".logE()
        })
        carAuthAdapter.data = mutableListOf<Int>(4)
    }
}