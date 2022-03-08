package com.changanford.car.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.control.CarControl
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.LocationServiceUtil


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val carTopBanner by lazy {NewCarTopBannerAdapter(requireActivity())}
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderCarBinding>(LayoutInflater.from(requireContext()), R.layout.header_car, null, false) }
    private var oldScrollY=0
    private val maxSlideY=500//最大滚动距离
    private val carControl by lazy { CarControl(requireActivity(),this,viewModel,mAdapter,headerBinding) }
    @SuppressLint("NewApi")
    override fun initView() {
        binding.apply {
            srl.setOnRefreshListener {
                getData()
                it.finishRefresh()
            }
            recyclerView.adapter=mAdapter
            recyclerView.addOnScrollListener(onScrollListener)
            mAdapter.addHeaderView(headerBinding.root)
            headerBinding.apply {
                btnSubmit.setOnClickListener { //立即订购
                    WBuriedUtil.clickCarOrder(topBannerList[carTopViewPager.currentItem].carModelName)
                }
            }
        }
        initObserve()
        initBanner()
    }
    override fun initData() {}
    private fun getData(){
        viewModel.getTopBanner()
//        viewModel.getAuthCarInfo()
    }
    private fun initObserve(){
        viewModel.topBannerBean.observe(this) {
            it?.apply {
                if (size == 0) {
                    headerBinding.carTopViewPager.isVisible = false
                    return@observe
                }
                headerBinding.carTopViewPager.isVisible = true
                topBannerList.clear()
                topBannerList.addAll(this)
                headerBinding.carTopViewPager.apply {
                    create(topBannerList)
                    if (oldScrollY >= maxSlideY) {
                        stopLoop()
                    }
                }
            }
            viewModel.getAuthCarInfo()
        }
        viewModel.carAuthBean.observe(this) {
            viewModel.getMyCarModelList()
        }
        viewModel.carInfoBean.observe(this) {
            initLocation()
            bindingCompose()
        }
        //经销商
        viewModel.dealersBean.observe(this) {
            bindingCompose()
        }
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
                        carControl.carModelCode=carModelCode
                        carTopBanner.pauseVideo(mainImg)
                        if(mainIsVideo==1){//是视频
                            carTopBanner.startPlayVideo(mainImg)
                        }else carTopBanner.releaseVideo()
                        bindingCompose()
                    }
                    headerBinding.carTopViewPager.apply {
                        if(oldScrollY>=maxSlideY){
                            stopLoop()
                        }
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
            for ((index,item) in withIndex()){
                bindView(index,item.modelCode,item)
            }
        }
    }
    private fun bindView(index:Int,modelCode:String,dataBean: NewCarInfoBean?){
        when(modelCode){
            //推荐
            "cars"->carControl.setFooterRecommended(dataBean,index)
            //购车
            "buy_service"->carControl.setFooterBuy(dataBean,index)
            //车主认证
            "car_auth"->carControl.setFooterCertification(dataBean,index)
            //售后
            "after-sales"->carControl.setFooterOwner(dataBean,index)
            //经销商
            "dealers"->carControl.setFooterDealers(dataBean,index)
        }
    }
    private fun initLocation(){
        carControl.locationType=if(!LocationServiceUtil.isLocServiceEnable(requireContext()))1 else if(!isGetLocation())2 else 0
        carControl.startLocation()
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden)getData()
    }
    override fun onStart() {
        super.onStart()
        if(carControl.locationType==1||carControl.locationType==3)initLocation()
    }
    override fun onResume() {
        super.onResume()
        getData()
         carControl.mMapView?.onResume()
        if(oldScrollY<maxSlideY&&topBannerList.size>0){
            val position=headerBinding.carTopViewPager.currentItem
            carTopBanner.resumeVideo(topBannerList[position].mainImg)
            headerBinding.carTopViewPager.startLoop()
        }
    }
    override fun onPause() {
        super.onPause()
        if(topBannerList.size>0){
            val position=headerBinding.carTopViewPager.currentItem
            carTopBanner.pauseVideo(topBannerList[position].mainImg)
        }
        carControl.mMapView?.onPause()
        headerBinding.carTopViewPager.stopLoop()
    }

    override fun onDestroy() {
        carControl.mLocationClient?.stop()
        carControl.mBaiduMap?.isMyLocationEnabled = false
        carControl.mMapView?.onDestroy()
        carControl.mLocationClient=null
        super.onDestroy()
    }
    @TargetApi(23)
    private fun isGetLocation(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 定位精确位置
            activity?.apply {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)return false
            }
            return true
        }
        return false
    }
}