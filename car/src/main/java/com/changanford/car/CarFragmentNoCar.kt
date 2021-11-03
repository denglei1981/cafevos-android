package com.changanford.car

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.changanford.car.adapter.CarAuthAdapter
import com.changanford.car.adapter.CarRecommendAdapter
import com.changanford.car.adapter.CarTopBannerAdapter
import com.changanford.car.databinding.CarFragmentNocarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xiaomi.push.it


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
                JumpUtils.instans?.jump(topBannerList[it].jumpDataType,topBannerList[it].jumpDataValue)
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
        binding.refreshLayout.setOnRefreshListener {
            initData()
            it.finishRefresh()
        }
        binding.carNoauthLayout.root.isVisible = false
        binding.carAuthrec.isVisible = false
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
            setVPHeight()
            binding.carTopViewPager.isVisible = true
            topBannerList.clear()
            topBannerList.addAll(it)
            binding.carTopViewPager.create(topBannerList)
        })
        observeData()
    }

    private fun observeData() {
        viewModel._middleInfo.observe(this, { it ->
            if (it?.carModels.isNullOrEmpty()) {
                binding.carRecommendLayout.root.isVisible = false
                setMinBottom(0)
            } else {
                binding.carRecommendLayout.root.isVisible = true
                setMinBottom(50)
                if (it.carModels.size==1){
                    binding.carRecommendLayout.carRecommendRec.isVisible = false
                    binding.carRecommendLayout.carRecommend1.isVisible = true
                    binding.carRecommendLayout.cr1img.load(it.carModels[0].carModelPic)
                    binding.carRecommendLayout.cr1txt.text = it.carModels[0].spuName
                    binding.carRecommendLayout.cr1img.setOnClickListener {v-> JumpUtils.instans?.jump(it.carModels[0].jumpDataType,it.carModels[0].jumpDataValue) }
                }else {
                    binding.carRecommendLayout.carRecommendRec.isVisible = true
                    binding.carRecommendLayout.carRecommend1.isVisible = false
                    carRecommendAdapter.data.clear()
                    carRecommendAdapter.data.addAll(it.carModels)
                    carRecommendAdapter.notifyDataSetChanged()
                }
            }
            if (it?.carModelMoreJump == null){
                binding.carRecommendLayout.imageView2.isVisible = false
            }else{
                binding.carRecommendLayout.imageView2.isVisible = true
                binding.carRecommendLayout.imageView2.setOnClickListener {v->
                    JumpUtils.instans?.jump(it?.carModelMoreJump)
                }
            }

            it?.carInfos?.let { cars ->
                carAuthAdapter.data.clear()
                carAuthAdapter.data.addAll(cars)
            }
        })
    }
    fun setVPHeight(){
        var params = binding.carTopViewPager.layoutParams
        var service = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var outMetrics = DisplayMetrics()
        service?.defaultDisplay.getRealMetrics(outMetrics)
        params.height =  outMetrics.heightPixels-DeviceUtils.getNavigationBarHeight()
        binding.carTopViewPager.layoutParams = params
    }
    fun setMinBottom(height:Int){
        var params = binding.scrollLayout.layoutParams as SmartRefreshLayout.LayoutParams
        params.bottomMargin = height
        binding.scrollLayout.layoutParams = params
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}