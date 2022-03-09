package com.changanford.car.control

import android.Manifest
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
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
import com.changanford.car.databinding.*
import com.changanford.car.ui.compose.AfterSalesService
import com.changanford.car.ui.compose.LookingDealers
import com.changanford.car.ui.compose.OwnerCertification
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.LocationServiceUtil
import com.changanford.common.util.MConstant
import com.changanford.common.wutil.WCommonUtil
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener

/**
 * @Author : wenke
 * @Time : 2022/3/7 0007
 * @Description : CarControl
 */
class CarControl(val activity:Activity, val fragment:Fragment, val viewModel: CarViewModel, private val mAdapter: CarNotAdapter,
                 private val headerBinding: HeaderCarBinding) {
    var carModelCode:String=""
    private var isFirstLoc=true
    private var latLng:LatLng?=null
    var mLocationClient:LocationClient?=null
    val locationType= MutableLiveData<Int>()// 0 已开启定位和已授权定位权限、1未开启定位、2未授权、3拒绝授权  4附近没有经销商 5已定位成功
    private val carIconAdapter by lazy { CarIconAdapter(activity) }
    private val serviceAdapter by lazy { CarServiceAdapter() }
    //推荐
    private var hRecommendBinding:HeaderCarRecommendedBinding?=null
    //购车服务
    private var hBuyBinding:HeaderCarBuyBinding?=null
    //车主服务
    private var hOwnerBinding:LayoutComposeviewBinding?=null
    //认证
    private var hCertificationBinding:LayoutComposeviewBinding?=null
    //经销商
    var hDealersBinding:HeaderCarDealersBinding?=null

    var mMapView: MapView?=null
    var mBaiduMap: BaiduMap?=null
    init {
        viewModel.carMoreInfoBean.observe(fragment) {
            carIconAdapter.setList(it?.carModels)
        }
        //经销商
        viewModel.dealersBean.observe(fragment) {
            bindDealersData(it)
        }
        locationType.observe(fragment){
            updateLocationUi()
        }
        viewModel.getMoreCar()
    }
    /**
     * 推荐
    * */
    fun setFooterRecommended(dataBean:NewCarInfoBean?,sort:Int,isUpdateSort:Boolean){
        if(hRecommendBinding==null){
            hRecommendBinding=DataBindingUtil.inflate<HeaderCarRecommendedBinding>(LayoutInflater.from(fragment.requireContext()), R.layout.header_car_recommended, null, false).apply {
                rvCar.adapter = carIconAdapter
                tvCarMoreName.setOnClickListener {
                    viewModel.carMoreInfoBean.value?.carModelMoreJump?.apply {
                        JumpUtils.instans?.jump(this)
                    }
                }
//                addFooterView(root,sort)
            }
        }
        hRecommendBinding?.apply {
            dataBean?.apply {
                if(isVisible(carModelCode)){
                    addFooterView(root,sort,isUpdateSort)
                    root.visibility=View.VISIBLE
                    tvCarMoreName.text=modelName
                }else mAdapter.removeFooterView(root)
            }
        }
    }
    /**
     * 车主服务
     * */
    fun setFooterOwner(dataBean:NewCarInfoBean?,sort:Int,isUpdateSort:Boolean){
        if(hOwnerBinding==null) {
            hOwnerBinding = DataBindingUtil.inflate<LayoutComposeviewBinding>(LayoutInflater.from(fragment.requireContext()),R.layout.layout_composeview,null,false).apply {
//                addFooterView(root,sort)
            }
        }
        hOwnerBinding?.apply {
            dataBean?.apply {
                if(isVisible(carModelCode)){
                    addFooterView(root,sort,isUpdateSort)
                    root.visibility=View.VISIBLE
                    composeView.setContent {
                        Column {
                            AfterSalesService(this@apply)
                        }
                    }
                }else mAdapter.removeFooterView(root)
            }
        }

    }
    /**
     * 认证
     * */
    fun setFooterCertification(dataBean:NewCarInfoBean?,sort:Int,isUpdateSort:Boolean){
        if(hCertificationBinding==null){
            hCertificationBinding=DataBindingUtil.inflate<LayoutComposeviewBinding>(LayoutInflater.
            from(fragment.requireContext()), R.layout.layout_composeview, null, false).apply {
//                addFooterView(root,sort)
            }
        }
        hCertificationBinding?.apply {
            dataBean?.apply {
                if(isVisible(carModelCode)){
                    addFooterView(root,sort,isUpdateSort)
                    root.visibility=View.VISIBLE
                    val carAuthBean=viewModel.carAuthBean.value
                    val carList=carAuthBean?.carList
                    val findModelCode=carList?.find { it.modelCode==carModelCode }//查指定车型是否有认证 null 则未认证
                    //authStatus >> 审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:已解绑
                    composeView.setContent {
                        Column {
                            Spacer(modifier = Modifier.height(27.dp))
                            if(findModelCode!=null&&findModelCode.authStatus==3){//已认证
                                CarAuthLayout(findModelCode)
                            }else OwnerCertification(this@apply,isUse(carModelCode),carAuthBean,findModelCode)
                        }
                    }
                }else mAdapter.removeFooterView(root)
            }
        }

    }
    /**
     * 购车
     * */
    fun setFooterBuy(dataBean:NewCarInfoBean?,sort:Int,isUpdateSort:Boolean){
        if(hBuyBinding==null){
            hBuyBinding=DataBindingUtil.inflate<HeaderCarBuyBinding?>(LayoutInflater.from(fragment.requireContext()), R.layout.header_car_buy, null, false).apply {
                rvCarService.adapter=serviceAdapter
//                addFooterView(root,sort)
            }
        }
        hBuyBinding?.apply {
            dataBean?.apply {
                if(isVisible(carModelCode)){
                    addFooterView(root,sort,isUpdateSort)
                    root.visibility=View.VISIBLE
                    if(icons!=null)rvCarService.layoutManager= GridLayoutManager(activity,if(icons!!.size>3)4 else 3)
                    serviceAdapter.setList(icons)
                    tvService.text=modelName
                }else mAdapter.removeFooterView(root)
            }
        }
    }
    /**
     * 经销商
     * */
    fun setFooterDealers(dataBean:NewCarInfoBean?,sort:Int,isUpdateSort:Boolean){
        if(hDealersBinding==null) {
            hDealersBinding=DataBindingUtil.inflate<HeaderCarDealersBinding>(LayoutInflater.from(fragment.requireContext()), R.layout.header_car_dealers, null, false).apply {
                mMapView=mapView
                mBaiduMap=mapView.map
                viewMapBg.setOnClickListener {
                    JumpUtils.instans?.jump(1,MConstant.H5_CAR_DEALER)
                }
                tvLocation.setOnClickListener {
                    when (locationType.value) {
                        //未开启定位
                        1 -> WCommonUtil.showLocationServicePermission(activity)
                        //未授权-询问授权
                        2 -> getLocationPermissions()
                        //拒绝授权
                        3 -> WCommonUtil.setSettingLocation(activity)
                    }
                }
//                addFooterView(root,sort)
            }
        }
        hDealersBinding?.apply {
            dataBean?.apply {
                if(isVisible(carModelCode)) {//经销商可见
                    addFooterView(root,sort,isUpdateSort)
                    root.visibility=View.VISIBLE
                    tvDealers.apply {
                        text=modelName
                        setOnClickListener {
                            JumpUtils.instans?.jump(jumpDataType,jumpDataValue)
                        }
                    }
                    initLocation()
                }else mAdapter.removeFooterView(root)
            }
        }
    }
    fun initLocation(){
        val locationTypeValue=if(!LocationServiceUtil.isLocServiceEnable(fragment.requireContext()))1 else if(!WCommonUtil.isGetLocation(fragment.requireActivity()))2 else 0
        locationType.postValue(locationTypeValue)
        startLocation(locationTypeValue)
    }
    private fun bindDealersData(dataBean: NewCarInfoBean?){
        if(dataBean==null)locationType.postValue(4)
        else{
            hDealersBinding?.apply {
                dataBean.apply {
                    locationType.postValue(5)
                    val p1 = LatLng(latY?.toDouble()!!, lngX?.toDouble()!!)
                    latLng?.apply { addPolyline(this,p1) }
                    addMarker(p1,dealerName)
                    composeViewDealers.setContent {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LookingDealers(this@apply)
                        }
                    }
                }
            }
        }
    }
    private fun addFooterView(view:View, sort:Int,isUpdateSort:Boolean){
        if(isUpdateSort){
            Handler(Looper.myLooper()!!).postDelayed({
                mAdapter.removeFooterView(view)
                mAdapter.setFooterView(view, sort)
            },1000)
//            doAsync {
//                mAdapter.removeFooterView(view)
//                mAdapter.setFooterView(view, sort)
//            }
        }
    }
    private fun getLocationPermissions(){
        SoulPermission.getInstance().checkAndRequestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            object : CheckRequestPermissionListener {
                override fun onPermissionOk(permission: Permission) {
                    locationType.postValue(0)
                }
                override fun onPermissionDenied(permission: Permission) {
                    locationType.postValue(3)
                    WCommonUtil.setSettingLocation(activity)
                }
            })
    }
    private fun updateLocationUi(locationTypeValue:Int?=locationType.value){
        hDealersBinding?.apply {
            if(0==locationTypeValue||5==locationTypeValue){
                viewMapBg.setBackgroundResource(R.drawable.bord_f4_5dp)
                tvLocation.visibility= View.GONE
                tvFromYouRecently.visibility= View.VISIBLE
            }else{
                tvFromYouRecently.visibility= View.GONE
                viewMapBg.setBackgroundResource(R.drawable.shape_40black_5dp)
                tvLocation.apply {
                    visibility= View.VISIBLE
                    setText(if(locationTypeValue!=4)R.string.str_pleaseOnYourMobilePhoneFirst else R.string.str_thereIsNoDealerNearby)
                    val drawable=if(locationType.value!=4)ContextCompat.getDrawable(fragment.requireContext(),R.mipmap.ic_location) else null
                    setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
                }
            }
        }
    }
    private fun startLocation(locationTypeValue:Int){
        if(locationTypeValue==0&&mLocationClient==null){
            hDealersBinding?.mapView?.showZoomControls(false)
            mLocationClient = LocationClient(activity).apply {
                Log.e("wenke","开始定位")
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
    }
    private val myLocationListener =object : BDAbstractLocationListener(){
        override fun onReceiveLocation(location: BDLocation?) {
            Log.e("wenke","onReceiveLocation:${location?.latitude}")
            location?.apply {
                latLng= LatLng(latitude, longitude)
                if (isFirstLoc) {
                    isFirstLoc = false
                    val builder = MapStatus.Builder()
                    builder.target(latLng).zoom(13.2f)
                    mBaiduMap?.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                }
                addMarker(latLng!!,null)
                viewModel.getRecentlyDealers(longitude,latitude)
            }
        }
    }
    /**
     * 绘制点标记
     * */
    private fun addMarker(latLng:LatLng,dealersName:String?){
//        val bitmap = BitmapDescriptorFactory.fromResource(iconId?:R.mipmap.ic_car_current_lacation)
        headerBinding.apply {
            if(dealersName!=null)tvLocationTitle.setText(dealersName)
            val bitmap = BitmapDescriptorFactory.fromBitmap(WCommonUtil.createBitmapFromView(if(dealersName==null)layoutLocation0 else layoutLocation1))
            val option: OverlayOptions = MarkerOptions()
                .position(latLng)
                .icon(bitmap)
            mBaiduMap?.addOverlay(option)
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
        mBaiduMap?.addOverlay(mOverlayOptions)
    }
}