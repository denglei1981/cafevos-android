package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarIconAdapter
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.CarServiceAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.control.AnimationControl
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.car.ui.compose.AfterSalesService
import com.changanford.car.ui.compose.LookingDealers
import com.changanford.car.ui.compose.OwnerCertification
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import java.util.*


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val animationControl by lazy { AnimationControl() }
    private val carTopBanner by lazy {NewCarTopBannerAdapter()}
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderCarBinding>(LayoutInflater.from(requireContext()), R.layout.header_car, null, false) }
    private var oldScrollY=0
    private val maxSlideY=500//最大滚动距离
    private val serviceAdapter by lazy { CarServiceAdapter() }
    private val carIconAdapter by lazy { CarIconAdapter() }
    @SuppressLint("NewApi")
    override fun initView() {
        binding.apply {
            srl.setOnRefreshListener {
                initData()
                it.finishRefresh()
            }
            recyclerView.adapter=mAdapter
            recyclerView.addOnScrollListener(onScrollListener)
            mAdapter.addHeaderView(headerBinding.root)
            headerBinding.apply {
                rvCarService.adapter=serviceAdapter
                rvCar.adapter=carIconAdapter
                bindingService()
                bindingCompose()
            }
        }
        initBanner()
    }
    override fun initData() {
        viewModel.getTopBanner()
        viewModel.topBannerBean.observe(this,{
            it?.apply {
                if (size == 0) {
                    headerBinding.carTopViewPager.isVisible = false
                    return@observe
                }
                headerBinding.carTopViewPager.isVisible = true
                topBannerList.clear()
                topBannerList.addAll(this)
                headerBinding.carTopViewPager.create(topBannerList)
            }
        })
        viewModel.carInfoBean.observe(this,{carInfo->
            carInfo?.apply {
                headerBinding.composeView.setContent {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        //赏车之旅
                        find { it.modelCode=="cars" }?.apply {
                            headerBinding.tvCarMoreName.text=modelName
                            carIconAdapter.setList(icons)
                        }
                        //购车服务
                        find { it.modelCode=="buy_service" }?.apply {
                            headerBinding.tvService.text=modelName
                            serviceAdapter.setList(icons)
                        }
                        //售后服务
                        AfterSalesService(find { it.modelCode=="after-sales" })
                        //寻找经销商
                        LookingDealers(find { it.modelCode=="dealers" })
                        //车主认证
                        OwnerCertification()
                    }
                }
            }
        })
    }
    private fun initBanner(){
        headerBinding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            setIndicatorView(headerBinding.drIndicator)
            setOnPageClickListener { _, position ->
            if (!FastClickUtils.isFastClick()) {
                    JumpUtils.instans?.jump(topBannerList[position].mainJumpType, topBannerList[position].mainJumpVal)
                }
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    topBannerList[position].apply {
                        viewModel.getMyCarModelList(carModelCode)
//                        headerBinding.imgTop.load(topImg)
//                        headerBinding.imgBottom.load(bottomImg)
//                        animationControl.startAnimation(headerBinding.imgTop,topAni)
//                        animationControl.startAnimation(headerBinding.imgBottom,topAni)
                    }
                }
            })
            setIndicatorView(headerBinding.drIndicator)
        }
        headerBinding.drIndicator.setIndicatorGap(20).setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)
        headerBinding.carTopViewPager.isSaveEnabled = false
    }
    /**
     * 服务模块
    * */
    private fun bindingService(){
        val dataList = arrayListOf<NewCarTagBean>()
        for (i in 0..3){
            dataList.add(NewCarTagBean(tagName = "Tag$i"))
        }
        serviceAdapter.setList(dataList)
    }
    private fun bindingCompose(){
        val dataList = arrayListOf<NewCarTagBean>()
        for (i in 0..10){
            dataList.add(NewCarTagBean(tagName = "Tag$i"))
        }
//        headerBinding.composeView.setContent {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                //售后服务
//                AfterSalesService(dataList)
//                //寻找经销商
//                LookingDealers()
//                //车主认证
//                OwnerCertification()
//            }
//        }
    }

    /**
     * RecyclerView 滚动监听 主要用于控制banner是否自动播放
    * */
    private val onScrollListener=object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== RecyclerView.SCROLL_STATE_IDLE){
                headerBinding.carTopViewPager.apply {
                    if(oldScrollY<maxSlideY){
                        startLoop()
                    }else{
                        stopLoop()
                    }
                }
            }
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            oldScrollY+=dy
        }
    }
    override fun onResume() {
        super.onResume()
        initData()
        if(oldScrollY<maxSlideY){
            headerBinding.carTopViewPager.startLoop()
        }
    }
    override fun onPause() {
        super.onPause()
        headerBinding.carTopViewPager.stopLoop()
    }
}