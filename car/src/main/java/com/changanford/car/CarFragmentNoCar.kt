package com.changanford.car

import androidx.core.view.isVisible
import com.changanford.car.adapter.CarAuthAdapter
import com.changanford.car.adapter.CarRecommendAdapter
import com.changanford.car.adapter.CarTopBannerAdapter
import com.changanford.car.databinding.CarFragmentNocarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.logE


class CarFragmentNoCar : BaseFragment<CarFragmentNocarBinding, CarViewModel>() {
    var carTopBanner = CarTopBannerAdapter()
    var carRecommendAdapter = CarRecommendAdapter()
    private var carAuthAdapter = CarAuthAdapter()
    var topBannerList = ArrayList<AdBean>()

    override fun initView() {
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
        }
        binding.drIndicator
            .setIndicatorGap(20)
            .setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)

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
        viewModel.getMyCar()
        viewModel._ads.observe(this, {
            "中间页广告数量${it.size}".logE()
            if (it==null|| it.size==0){
                binding.carTopViewPager.isVisible = false
                return@observe
            }
            binding.carTopViewPager.isVisible = false
            topBannerList.clear()
            topBannerList.addAll(it)
            binding.carTopViewPager.create(topBannerList)
        })
        observeData()
    }

    private fun observeData() {
        viewModel._middleInfo.observe(this, { it ->
            if (it.carModels == null) {
                binding.carRecommendLayout.root.isVisible = false
            } else {
                binding.carRecommendLayout.root.isVisible = true
                carRecommendAdapter.data.clear()
                carRecommendAdapter.data.add(it.carModels)
            }

            it.carInfos?.let { cars ->
                carAuthAdapter.data.clear()
                carAuthAdapter.data.add(cars)
            }
        })
    }
}