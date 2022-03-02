package com.changanford.car.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
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
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.LocationServiceUtil
import com.changanford.common.util.location.LocationUtils


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val carTopBanner by lazy {NewCarTopBannerAdapter(requireActivity())}
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderCarBinding>(LayoutInflater.from(requireContext()), R.layout.header_car, null, false) }
    private var oldScrollY=0
    private val maxSlideY=500//最大滚动距离
    private val serviceAdapter by lazy { CarServiceAdapter() }
    private val carIconAdapter by lazy { CarIconAdapter(requireActivity()) }
    private var carModelCode:String=""
    private var longitude:Double?=null//经度
    private var latitude:Double?=null//维度
    private val mMapView by lazy { headerBinding.mapView}
    private val mBaiduMap by lazy { headerBinding.mapView.map }
    private var mLocationClient:LocationClient?=null
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
                btnSubmit.setOnClickListener { //立即订购
                    WBuriedUtil.clickCarOrder(topBannerList[carTopViewPager.currentItem].carModelName)
                }
            }
        }
        initObserve()
        initBanner()
        initMap()
    }
    override fun initData() {
        viewModel.getTopBanner()
        viewModel.getAuthCarInfo()
        viewModel.getMoreCar()
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
        }
        viewModel.carAuthBean.observe(this) {
            viewModel.getMyCarModelList()
        }
        viewModel.carMoreInfoBean.observe(this) {
            carIconAdapter.setList(it?.carModels)
        }
        viewModel.carInfoBean.observe(this) {
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
                        this@NewCarFragmentNoCar.carModelCode=carModelCode
                        carTopBanner.pauseVideo()
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
                    //售后服务
                    find { it.modelCode=="after-sales" }?.apply {
                        if(isVisible(carModelCode))AfterSalesService(this)
                    }
                }
            }
            headerBinding.apply {
                layoutDealers.visibility=View.GONE
                //寻找经销商
                find { it.modelCode=="dealers" }?.apply {
                    if(isVisible(carModelCode)) {
                        layoutDealers.visibility=View.VISIBLE
                        tvDealers.apply {
                            text=modelName
                            setOnClickListener {

                            }
                        }
                        composeViewDealers.setContent {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                LookingDealers(viewModel.dealersBean.value)
                            }
                        }
                    }
                }

            }
        }
    }
    private fun initMap(){
        headerBinding.apply {
            if (LocationServiceUtil.isLocServiceEnable(requireContext())&&isGetLocation()) {
                viewMapBg.setBackgroundResource(R.drawable.bord_f4_5dp)
                tvLocation.visibility=View.GONE
                tvFromYouRecently.visibility=View.VISIBLE
                headerBinding.mapView.showZoomControls(false)
                LocationUtils.circleLocation(myLocationListener)
//                mBaiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
//                mBaiduMap.isTrafficEnabled = true
//                //关闭缩放按钮
//                headerBinding.mapView.showZoomControls(false)
//                // 开启定位图层
//                mBaiduMap.isMyLocationEnabled = true
//                //声明LocationClient类
//                try {
//                    LocationClient.setAgreePrivacy(true)
//                    mLocationClient = LocationClient(requireContext())
//                    val option = LocationClientOption()
//                    option.isOpenGps = true // 打开gps
//                    option.setCoorType("bd09ll") // 设置坐标类型
//                    option.setScanSpan(1000)
//                    mLocationClient?.registerLocationListener(myLocationListener)
//                    mLocationClient?.locOption = option
//                    mLocationClient?.start()//开始定位
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Log.e("wenke","定位：Exception:${e.message}")
//                }
            }else{//服务端自行ip定位
                tvFromYouRecently.visibility=View.GONE
                viewMapBg.setBackgroundResource(R.drawable.shape_40black_5dp)
                tvLocation.visibility=View.VISIBLE
                viewModel.getRecentlyDealers()
            }
        }

    }
    private var isFirstLoc=true
    private val myLocationListener =object :BDAbstractLocationListener(){
        override fun onReceiveLocation(location: BDLocation?) {
            location?.apply {
                latitude = latitude //获取纬度信息
                longitude = longitude //获取经度信息
//                val locData = MyLocationData.Builder()
//                    .accuracy(radius) // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(direction).latitude(latitude)
//                    .longitude(longitude).build()
//                mBaiduMap.setMyLocationData(locData)
                if (isFirstLoc) {
                    isFirstLoc = false
                    val latLng = LatLng(latitude, longitude)
                    val builder = MapStatus.Builder()
                    builder.target(latLng).zoom(20.0f)
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                }
                addMarker(latitude,longitude)
                viewModel.getRecentlyDealers(longitude,latitude)
            }
        }
    }
    private fun addMarker(latitude: Double?,longitude:Double?){
        Log.e("wenke","latitude:$latitude>>>longitude:$longitude")
        if(latitude==null||longitude==null)return
        //定义Maker坐标点
        val point = LatLng(latitude,longitude)
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.fordicon)
        //构建MarkerOption，用于在地图上添加Marker
        val option: OverlayOptions = MarkerOptions()
            .position(point)
            .icon(bitmap)
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option)
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
        mMapView.onResume()
        if(oldScrollY<maxSlideY){
            carTopBanner.resumeVideo()
            headerBinding.carTopViewPager.startLoop()
        }
    }
    override fun onPause() {
        super.onPause()
        carTopBanner.pauseVideo()
        mMapView.onPause();
        headerBinding.carTopViewPager.stopLoop()
    }

    override fun onDestroy() {
        mLocationClient?.apply {
            stop()
            mBaiduMap.isMyLocationEnabled = false
            mMapView.onDestroy();
        }
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