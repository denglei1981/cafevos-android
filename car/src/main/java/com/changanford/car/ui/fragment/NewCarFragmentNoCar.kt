package com.changanford.car.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.changanford.car.CarAuthLayout
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarIconAdapter
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.CarServiceAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.car.ui.compose.AfterSalesService
import com.changanford.car.ui.compose.LookingDealers
import com.changanford.car.ui.compose.OwnerCertification
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.location.LocationUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import java.util.*


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
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
                tvCarMoreName.setOnClickListener {
                    viewModel.carMoreInfoBean.value?.carModelMoreJump?.apply {
                        JumpUtils.instans?.jump(this)
                    }
                }
            }
        }
        initBanner()
        initLocation()
    }
    override fun initData() {
        viewModel.topBannerBean.observe(this,{
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
                    if(oldScrollY>=maxSlideY){
                        stopLoop()
                    }
                }
            }
        })
        viewModel.carAuthBean.observe(this,{
            viewModel.getMyCarModelList()
        })
        viewModel.carMoreInfoBean.observe(this,{
            carIconAdapter.setList(it?.carModels)
        })
        viewModel.carInfoBean.observe(this,{
            bindingCompose()
        })
        //经销商
        viewModel.dealersBean.observe(this,{
            bindingCompose()
        })
        viewModel.getTopBanner()
        viewModel.getAuthCarInfo()
        viewModel.getMoreCar()
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
            //赏车之旅
            find { it.modelCode=="cars" }?.apply {
                if(isVisible(carModelCode)){
//                    carIconAdapter.setList(icons)
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
                        if(isVisible(carModelCode))LookingDealers(modelName,viewModel.dealersBean.value)
                    }
                    //车主认证
                    find { it.modelCode=="car_auth" }?.apply {
                        if(isVisible(carModelCode)){
                            val carAuthBean=viewModel.carAuthBean.value
                            val carList=carAuthBean?.carList
                            val findModelCode=carList?.find { it.modelCode==carModelCode }//查指定车型是否有认证 null 则未认证
                            //authStatus >> 审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:已解绑
                            if(findModelCode!=null&&findModelCode.authStatus==3){//已认证
                                CarAuthLayout(findModelCode)
                            }else OwnerCertification(this,isUse(carModelCode),carAuthBean,findModelCode)
//                            if(carList==null||carList.size<1||findModelCode==null){//未认证
//                                OwnerCertification(this,isUse(carModelCode),carAuthBean)
//                            }

                        }
                    }
                }
            }
        }
    }
    private fun initLocation(){
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        LocationUtils.circleLocation(object : BDAbstractLocationListener() {
                            override fun onReceiveLocation(location: BDLocation) {
                                val latitude = location.latitude //获取纬度信息
                                val longitude = location.longitude //获取经度信息
                                viewModel.getRecentlyDealers(longitude,latitude)
                            }
                        })
                    }
                    override fun onPermissionDenied(permission: Permission) {

                    }
                })
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