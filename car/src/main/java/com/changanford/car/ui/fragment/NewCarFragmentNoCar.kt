package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
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
    private var carModelCode:String=""
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
//                bindingCompose()
            }
        }
        initBanner()
    }
    override fun initData() {
        viewModel.getTopBanner()
        viewModel.getMyCarModelList()
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
        viewModel.carInfoBean.observe(this,{
            bindingCompose()
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
                        this@NewCarFragmentNoCar.carModelCode=carModelCode
                        bindingCompose()
//                        viewModel.getMyCarModelList()
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

    private fun bindingCompose(){
        viewModel.carInfoBean.value?.apply {
            //赏车之旅
            find { it.modelCode=="cars" }?.apply {
                if(isVisible(carModelCode)){
                    carIconAdapter.setList(icons)
                    headerBinding.apply {
                        tvCarMoreName.text=modelName
                        tvCarMoreName.visibility= View.VISIBLE
                        rvCar.visibility=View.VISIBLE
                    }
                }else{
                    headerBinding.tvCarMoreName.visibility= View.GONE
                    headerBinding.rvCar.visibility=View.GONE
                }
            }
            //购车服务
            find { it.modelCode=="buy_service" }?.apply {
                if(isVisible(carModelCode)){
                    serviceAdapter.setList(icons)
                    headerBinding.apply {
                        tvService.text=modelName
                        tvService.visibility=View.VISIBLE
                        rvCarService.visibility=View.VISIBLE
                    }
                }else{
                    headerBinding.apply {
                        tvService.visibility=View.GONE
                        rvCarService.visibility=View.GONE
                    }
                }

            }
            headerBinding.composeView.setContent {
                Column(modifier = Modifier.fillMaxWidth()) {
                    //售后服务
                    find { it.modelCode=="after-sales" }?.apply {
                        if(isVisible(carModelCode))AfterSalesService(this)
                    }
                    //寻找经销商
                    find { it.modelCode=="dealers" }?.apply {
                        if(isVisible(carModelCode))LookingDealers(this)
                    }
                    //车主认证
                    find { it.modelCode=="car_auth" }?.apply {
                        if(isVisible(carModelCode))OwnerCertification(this,isUse(carModelCode))
                    }
                }
            }
        }
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