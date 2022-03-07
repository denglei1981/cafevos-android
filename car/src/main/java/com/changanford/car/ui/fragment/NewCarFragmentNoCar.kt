package com.changanford.car.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
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
import com.changanford.common.wutil.WCommonUtil
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener


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
    private val mMapView by lazy { headerBinding.mapView}
    private val mBaiduMap by lazy { headerBinding.mapView.map }
    private var mLocationClient:LocationClient?=null
    private var isFirstLoc=true
    private var latLng:LatLng?=null
    private var locationType=0// 0 已开启定位和已授权定位权限、1未开启定位、2未授权、3拒绝授权
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
                tvLocation.setOnClickListener {
                    when (locationType) {
                        //未开启定位
                        1 -> WCommonUtil.showLocationServicePermission(requireActivity())
                        //未授权-询问授权
                        2 -> getLocationPermissions()
                        //拒绝授权
                        3 -> WCommonUtil.setSettingLocation(requireContext())
                    }
                }
            }
        }
        initObserve()
        initBanner()
        initLocation()
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
                        if(icons!=null)rvCarService.layoutManager=GridLayoutManager(requireContext(),if(icons!!.size>3)4 else 3)
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
                                JumpUtils.instans?.jump(jumpDataType,jumpDataValue)
                            }
                        }
                        viewModel.dealersBean.value?.apply {
                            val p1 = LatLng(latY?.toDouble()!!, lngX?.toDouble()!!)
//                            addInfoWindow(p1,dealerName?:"")
                            latLng?.apply { addPolyline(this,p1) }
                            addMarker(p1,R.mipmap.ic_car_location,dealerName)
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
    }
    private fun initLocation(){
        locationType =if(!LocationServiceUtil.isLocServiceEnable(requireContext()))1 else if(!isGetLocation())2 else 0
        updateLocationUi()
    }
    private fun getLocationPermissions(){
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
            object : CheckRequestPermissionListener {
                override fun onPermissionOk(permission: Permission) {
                    locationType=0
                    updateLocationUi()
                }
                override fun onPermissionDenied(permission: Permission) {
                    locationType=3
                    updateLocationUi()
                    WCommonUtil.setSettingLocation(requireContext())
                }
            })
    }
    private fun updateLocationUi(){
        headerBinding.apply {
            if(locationType==0){
                viewMapBg.setBackgroundResource(R.drawable.bord_f4_5dp)
                tvLocation.visibility=View.GONE
                tvFromYouRecently.visibility=View.VISIBLE
                headerBinding.mapView.showZoomControls(false)
                startLocation()
                Log.e("wenke","开始定位")
            }else{
                tvFromYouRecently.visibility=View.GONE
                viewMapBg.setBackgroundResource(R.drawable.shape_40black_5dp)
                tvLocation.visibility=View.VISIBLE
//                viewModel.getRecentlyDealers()
            }
        }
    }
    private fun startLocation(){
        mLocationClient = LocationClient(requireContext()).apply {
            //通过LocationClientOption设置LocationClient相关参数
            val option = LocationClientOption()
            option.isOpenGps = true
            option.setCoorType("bd09ll")
            option.setScanSpan(0)
            //设置locationClientOption
            locOption = option
            //注册LocationListener监听器
            registerLocationListener(myLocationListener)
            //开启地图定位图层
            start()
        }
    }
    private val myLocationListener =object :BDAbstractLocationListener(){
        override fun onReceiveLocation(location: BDLocation?) {
            Log.e("wenke","onReceiveLocation:${location?.latitude}")
            location?.apply {
                latLng= LatLng(latitude, longitude)
                if (isFirstLoc) {
                    isFirstLoc = false
                    val builder = MapStatus.Builder()
                    builder.target(latLng).zoom(13.2f)
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                }
                addMarker(latLng!!,R.mipmap.ic_car_location_green,getString(R.string.str_currentPosition))
//                addInfoWindow(latLng!!,getString(R.string.str_currentPosition))
                viewModel.getRecentlyDealers(longitude,latitude)
            }
        }
    }
    /**
     * 绘制折线
     * */
    private fun addPolyline(p1:LatLng,p2:LatLng){
        val points: MutableList<LatLng> = ArrayList()
        points.add(p1)
        points.add(p2)
        //设置折线的属性
        val mOverlayOptions: OverlayOptions = PolylineOptions()
            .width(2)
            .color(-0x00979797)
            .points(points)
            .dottedLine(true) //设置折线显示为虚线
       mBaiduMap.addOverlay(mOverlayOptions)
    }
    /**
     * 绘制点标记
    * */
    private fun addMarker(latLng:LatLng,iconId:Int,dealersName:String?){
//        val bitmap = BitmapDescriptorFactory.fromResource(iconId?:R.mipmap.ic_car_current_lacation)
        headerBinding.apply {
            imgLocationIc.setImageResource(iconId)
            tvLocationTitle.setText(dealersName)
            val bitmap = BitmapDescriptorFactory.fromBitmap(WCommonUtil.createBitmapFromView(layoutLocation))
            val option: OverlayOptions = MarkerOptions()
                .position(latLng)
                .icon(bitmap)
            mBaiduMap.addOverlay(option)
        }
    }
    /**
     * 添加信息窗
     * */
    private fun addInfoWindow(latLng:LatLng,str:String){
        //用来构造InfoWindow的Button
        val view = Button(context).apply {
            setBackgroundResource(R.drawable.shape_car_location_bg)
            setPadding(20,0,20,0)
            maxLines=1
            maxWidth=100
            ellipsize=  TextUtils.TruncateAt.END
            isSingleLine=true
//            setTextSize(TypedValue.COMPLEX_UNIT_SP,13f)
            setTextColor(ContextCompat.getColor(requireContext(),R.color.color_33))
            text = str
        }
        //构造InfoWindow point 描述的位置点  -100 InfoWindow相对于point在y轴的偏移量
        val mInfoWindow = InfoWindow(view, latLng, -20)
        mBaiduMap.showInfoWindow(mInfoWindow)
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

    override fun onStart() {
        super.onStart()
        if(locationType==1||locationType==3)initLocation()
    }
    override fun onResume() {
        super.onResume()
        initData()
        mMapView.onResume()
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
        mMapView.onPause()
        headerBinding.carTopViewPager.stopLoop()
    }

    override fun onDestroy() {
        mLocationClient?.stop()
        mBaiduMap.isMyLocationEnabled = false
        mMapView.onDestroy()
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