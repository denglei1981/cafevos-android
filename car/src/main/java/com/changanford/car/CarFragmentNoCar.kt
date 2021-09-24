package com.changanford.car

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.changanford.car.adapter.CarAuthAdapter
import com.changanford.car.adapter.CarRecommendAdapter
import com.changanford.car.databinding.CarFragmentNocarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.ui.viewpager.bindAdapter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.logE
import kotlinx.coroutines.launch


class CarFragmentNoCar : BaseFragment<CarFragmentNocarBinding, CarViewModel>() {
    var carRecommendAdapter = CarRecommendAdapter()
    private var carAuthAdapter = CarAuthAdapter()

    override fun initView() {
        lifecycleScope.launch {
            binding.carTopViewPager?.bindAdapter(this@CarFragmentNoCar, 2) {
                val carIntroFragment = CarIntroFragment()
                val bundle = Bundle()
                bundle.putSerializable(
                    "imgUrl",
                    "uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png"
                )
                carIntroFragment.arguments = bundle
                carIntroFragment
            }
        }
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
    }
}