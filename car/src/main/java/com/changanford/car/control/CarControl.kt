package com.changanford.car.control

import android.Manifest
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ZoomControls
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.baidu.mapapi.utils.DistanceUtil
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarIconAdapter
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.CarServiceAdapter
import com.changanford.car.databinding.*
import com.changanford.car.ui.compose.AfterSalesService
import com.changanford.car.ui.compose.CarAuthLayout
import com.changanford.car.ui.compose.LookingDealers
import com.changanford.car.ui.compose.OwnerCertificationUnauthorized
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.bean.DistanceBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.LocationServiceUtil
import com.changanford.common.util.MConstant
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.wutil.WCommonUtil
import com.changanford.common.wutil.wLogE
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import com.xiaomi.push.it


/**
 * @Author : wenke
 * @Time : 2022/3/7 0007
 * @Description : CarControl
 */
class CarControl(
    val activity: Activity,
    val fragment: Fragment,
    val viewModel: CarViewModel,
    private val mAdapter: CarNotAdapter,
    private val headerBinding: HeaderCarBinding,
    private val binding:FragmentCarBinding
) {
    var carModelCode: String = ""
    private var isFirstLoc = true
    private var latLng: LatLng? = null
    var mLocationClient: LocationClient? = null
    val locationType = MutableLiveData<Int>()// 0 已开启定位和已授权定位权限、1未开启定位、2未授权、3拒绝授权  4附近没有经销商 5已定位成功
    private val carIconAdapter by lazy { CarIconAdapter(activity) }
    private val serviceAdapter by lazy { CarServiceAdapter() }

    //推荐
    private var hRecommendBinding: HeaderCarRecommendedBinding? = null

    //购车服务
    private var hBuyBinding: HeaderCarBuyBinding? = null

    //车主服务
    private var hOwnerBinding: LayoutComposeviewBinding? = null

    //认证
    private var hCertificationBinding: LayoutComposeviewBinding? = null

    //经销商
    private var hDealersBinding: HeaderCarDealersBinding? = null

    private var distanceBeanArr: ArrayList<DistanceBean>? = null

    private var certificationInfoBean: NewCarInfoBean? = null
    var delayMillis: Long? = null//addFooterView延迟添加时间
    var mMapView: MapView? = null
    var mBaiduMap: BaiduMap? = null

    init {
        viewModel.carMoreInfoBean.observe(fragment) {
//            carIconAdapter.setList(it?.carModels?.reversed())
            carIconAdapter.setList(it?.carModels)
        }
        //经销商
        viewModel.dealersBean.observe(fragment) {
            bindDealersData(it)
        }
        locationType.observe(fragment) {
            updateLocationUi()
        }
        //认证信息
        viewModel.carAuthBean.observe(fragment) {
            bindCertification(it)
        }
    }

    /**
     * 推荐
     * */
    fun setFooterRecommended(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        if (hRecommendBinding == null) {
            hRecommendBinding = DataBindingUtil.inflate<HeaderCarRecommendedBinding>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.header_car_recommended,
                null,
                false
            ).apply {
                rvCar.adapter = carIconAdapter
                tvCarMoreName.setOnClickListener {
                    dataBean?.apply {
                        JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                        GIOUtils.homePageClick("推荐车型", 0.toString(), "更多")
                    }
                }
//                addFooterView(root,sort)
            }
        }
        hRecommendBinding?.apply {
            dataBean?.apply {
                addFooterView(root, sort, isUpdateSort)
                if (isVisible(carModelCode)) {
                    root.visibility = View.VISIBLE
                    tvCarMoreName.text = modelName
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }
    }

    /**
     * 车主服务
     * */
    fun setFooterOwner(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        if (hOwnerBinding == null) {
            hOwnerBinding = DataBindingUtil.inflate<LayoutComposeviewBinding>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.layout_composeview,
                null,
                false
            ).apply {
//                addFooterView(root,sort)
            }
        }
        hOwnerBinding?.apply {
            dataBean?.apply {
                addFooterView(root, sort, isUpdateSort)
                if (isVisible(carModelCode)) {
                    root.visibility = View.VISIBLE
                    composeView.setContent {
                        Column {
                            AfterSalesService(this@apply)
                        }
                    }
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }
    }

    /**
     * 认证
     * */
    fun setFooterCertification(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        certificationInfoBean = dataBean
        if (hCertificationBinding == null) {
            hCertificationBinding = DataBindingUtil.inflate<LayoutComposeviewBinding>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.layout_composeview,
                null,
                false
            ).apply {
//                addFooterView(root,sort)
            }
        }
        hCertificationBinding?.apply {
            addFooterView(root, sort, isUpdateSort)
            bindCertification(viewModel.carAuthBean.value)
        }
    }

    /**
     * 绑定认证数据
     * */
    private fun bindCertification(dataBean: CarAuthBean? = null) {
        if (dataBean == null) return
        hCertificationBinding?.apply {
            certificationInfoBean?.apply {
                val carList = dataBean.carList
                //优先获取默认车辆然后获取已认证的车辆
                val authItemData =
                    carList?.find { it.isDefault == 1 } ?: carList?.find { it.authStatus == 3 }
                if (authItemData != null || isVisible(carModelCode)) {
                    root.visibility = View.VISIBLE
//                    //优先查询指定车辆是否有认证没有则取第一辆认证的车辆信息 如果查询结果为 null 则表示该用户是非车主身份
//                    val findModelCode=carList?.find { it.modelCode==carModelCode }?:carList?.get(0)
                    //获取第一辆在审核的车辆信息
                    val auditItemData = carList?.find { it.authStatus < 3 }
                    //authStatus >> 审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:已解绑
                    composeView.setContent {
                        Column {
                            Spacer(modifier = Modifier.height(27.dp))
                            if (authItemData?.authStatus == 3) CarAuthLayout(
                                authItemData,
                                auditItemData
                            )//已认证
                            else OwnerCertificationUnauthorized(
                                this@apply,
                                isUse(carModelCode),
                                dataBean,
                                auditItemData
                            )//未认证或者审核中
                        }
                    }
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }
    }

    /**
     * 认证
     * */
    fun setFooterCertification0(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        if (hCertificationBinding == null) {
            hCertificationBinding = DataBindingUtil.inflate<LayoutComposeviewBinding>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.layout_composeview,
                null,
                false
            ).apply {
//                addFooterView(root,sort)
            }
        }
        hCertificationBinding?.apply {
            dataBean?.apply {
                addFooterView(root, sort, isUpdateSort)
                val carAuthBean = viewModel.carAuthBean.value
                val carList = carAuthBean?.carList
                //优先获取默认车辆然后获取已认证的车辆
                val authItemData =
                    carList?.find { it.isDefault == 1 } ?: carList?.find { it.authStatus == 3 }
                if (authItemData != null || isVisible(carModelCode)) {
                    root.visibility = View.VISIBLE
//                    //优先查询指定车辆是否有认证没有则取第一辆认证的车辆信息 如果查询结果为 null 则表示该用户是非车主身份
//                    val findModelCode=carList?.find { it.modelCode==carModelCode }?:carList?.get(0)
                    //获取第一辆在审核的车辆信息
                    val auditItemData = carList?.find { it.authStatus < 3 }
                    //authStatus >> 审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:已解绑
                    composeView.setContent {
                        Column {
                            Spacer(modifier = Modifier.height(27.dp))
                            if (authItemData?.authStatus == 3) CarAuthLayout(
                                authItemData,
                                auditItemData
                            )//已认证
                            else OwnerCertificationUnauthorized(
                                this@apply,
                                isUse(carModelCode),
                                carAuthBean,
                                auditItemData
                            )//未认证或者审核中
                        }
                    }
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }

    }

    /**
     * 购车
     * */
    fun setFooterBuy(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        if (hBuyBinding == null) {
            hBuyBinding = DataBindingUtil.inflate<HeaderCarBuyBinding?>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.header_car_buy,
                null,
                false
            ).apply {
                rvCarService.adapter = serviceAdapter
//                addFooterView(root,sort)
            }
        }
        hBuyBinding?.apply {
            dataBean?.apply {
                addFooterView(root, sort, isUpdateSort)
                if (isVisible(carModelCode)) {
                    root.visibility = View.VISIBLE
                    if (icons != null) rvCarService.layoutManager =
                        GridLayoutManager(activity, if (icons!!.size > 3) 4 else 3)
                    serviceAdapter.setList(icons)
                    tvService.text = modelName
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }
    }

    /**
     * 经销商
     * */
    fun setFooterDealers(dataBean: NewCarInfoBean?, sort: Int, isUpdateSort: Boolean) {
        if (hDealersBinding == null) {
            hDealersBinding = DataBindingUtil.inflate<HeaderCarDealersBinding>(
                LayoutInflater.from(fragment.requireContext()),
                R.layout.header_car_dealers,
                null,
                false
            ).apply {
                mMapView = headerBinding.mapView
                headerBinding.layoutRoot.removeView(headerBinding.mapView)
                mMapView?.visibility = View.VISIBLE
                mapView.addView(mMapView)
                mBaiduMap = mMapView?.map
                initMap()
                viewMapBg.setOnClickListener {
                    WBuriedUtil.clickCarDealer(viewModel.dealersBean.value?.dealerName)
                    GIOUtils.homePageClick("附近经销商", 1.toString(), "附近经销商")
                    JumpUtils.instans?.jump(1, MConstant.H5_CAR_DEALER)
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
                tvDealers.setOnClickListener {
                    WBuriedUtil.clickCarDealer(viewModel.dealersBean.value?.dealerName)
                    dataBean?.apply {
                        JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                    }
                }
//                addFooterView(root,sort)
            }
        }
        hDealersBinding?.apply {
            dataBean?.apply {
                addFooterView(root, sort, isUpdateSort)
                if (isVisible(carModelCode)) {//经销商可见
                    root.visibility = View.VISIBLE
                    tvDealers.apply {
                        text = modelName
                        setOnClickListener {
                            JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                        }
                    }
                    initLocation()
                } else {
                    root.visibility = View.GONE
//                    mAdapter.removeFooterView(root)
                }
            }
        }
    }

    fun initLocation() {
        val locationTypeValue =
            if (!LocationServiceUtil.isLocServiceEnable(fragment.requireContext())) 1 else if (!WCommonUtil.isGetLocation(
                    fragment.requireActivity()
                )
            ) 2 else 0
        locationType.postValue(locationTypeValue)
        startLocation(locationTypeValue)
    }

    private fun bindDealersData(dataBean: NewCarInfoBean?) {
        if (dataBean == null) locationType.postValue(4)
        else {
            hDealersBinding?.apply {
                dataBean.apply {
                    locationType.postValue(5)
                    val p1 = LatLng(latY?.toDouble()!!, lngX?.toDouble()!!)
                    latLng?.apply { addPolyline(this, p1) }
                    addMarker(p1, dealerName)
                    composeViewDealers.setContent {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LookingDealers(this@apply)
                        }
                    }
                }
            }
        }
    }

    private fun addFooterView(view: View, sort: Int, isUpdateSort: Boolean) {
        if (isUpdateSort) {
            view.visibility = View.VISIBLE
            if (delayMillis != null) {
                Handler(Looper.myLooper()!!).postDelayed({
                    mAdapter.removeFooterView(view)
                    mAdapter.setFooterView(view, sort)
                }, delayMillis!!)
            } else {
                mAdapter.removeFooterView(view)
                mAdapter.setFooterView(view, sort)
            }
//            doAsync {
//                mAdapter.removeFooterView(view)
//                mAdapter.setFooterView(view, sort)
//            }
        }
    }

    private fun getLocationPermissions() {
        val permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val success = {
            locationType.postValue(0)
            startLocation(0)
        }
        val fail = {
            locationType.postValue(3)
            WCommonUtil.setSettingLocation(activity)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//        SoulPermission.getInstance().checkAndRequestPermission(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            object : CheckRequestPermissionListener {
//                override fun onPermissionOk(permission: Permission) {
//                    locationType.postValue(0)
//                    startLocation(0)
//                }
//
//                override fun onPermissionDenied(permission: Permission) {
//                    locationType.postValue(3)
//                    WCommonUtil.setSettingLocation(activity)
//                }
//            })
    }

    private fun updateLocationUi(locationTypeValue: Int? = locationType.value) {
        "更新定位UI:>>>$locationTypeValue".wLogE()
        hDealersBinding?.apply {
            if (0 == locationTypeValue || 5 == locationTypeValue) {
                viewMapBg.setBackgroundResource(R.drawable.bord_f4_5dp)
                tvLocation.visibility = View.GONE
                tvFromYouRecently.visibility = View.VISIBLE
            } else {
                tvFromYouRecently.visibility = View.GONE
                viewMapBg.setBackgroundResource(R.drawable.shape_40black_5dp)
                tvLocation.apply {
                    visibility = View.VISIBLE
                    setText(if (locationTypeValue != 4) R.string.str_pleaseOnYourMobilePhoneFirst else R.string.str_thereIsNoDealerNearby)
                    val drawable = if (locationType.value != 4) ContextCompat.getDrawable(
                        fragment.requireContext(),
                        R.mipmap.ic_location
                    ) else null
                    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        }
    }

    private fun startLocation(locationTypeValue: Int) {
        "开始定位>>$locationTypeValue".wLogE()
        if (locationTypeValue == 0 && mLocationClient == null) {
            mLocationClient = LocationClient(activity).apply {
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

    /**
     * 将地图缩放到最大
     * */
    private fun initMap() {
        distanceBeanArr = ArrayList()
        val distanceArr =
            arrayListOf(200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000)
        for (i in 0 until distanceArr.size) {
            val itemBean = DistanceBean(zoom = (16f - i), distance = distanceArr[i])
            distanceBeanArr?.add(itemBean)
        }
        mMapView?.apply {
            showZoomControls(false)
            showScaleControl(false)
            val child = getChildAt(1)
            if (child != null && (child is ImageView || child is ZoomControls)) {
                child.visibility = View.INVISIBLE// 隐藏logo
            }
            setMapZoom(1f)
        }
    }

    private val myLocationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            location?.apply {
                latLng = LatLng(latitude, longitude)
                if (isFirstLoc) {
                    isFirstLoc = false
                    setMapZoom(15f)//默认缩放15
                }
                addMarker(latLng!!, null)
                viewModel.getRecentlyDealers(longitude, latitude)
            }
        }
    }

    /**
     * 绘制点标记
     * */
    private fun addMarker(latLng: LatLng, dealersName: String?) {
        mLocationClient?.unRegisterLocationListener(myLocationListener)
//        val bitmap = BitmapDescriptorFactory.fromResource(iconId?:R.mipmap.ic_car_current_lacation)
        binding.apply {
            if (dealersName != null) tvLocationTitle.setText(dealersName)
            val bitmap =
                BitmapDescriptorFactory.fromBitmap(WCommonUtil.createBitmapFromView(if (dealersName == null) layoutLocation0 else layoutLocation1))
            val option: OverlayOptions = MarkerOptions()
                .position(latLng)
                .icon(bitmap)
            mBaiduMap?.addOverlay(option)
        }
    }

    /**
     * 绘制折线
     * */
    private fun addPolyline(p1: LatLng, p2: LatLng) {
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
        distanceBeanArr?.apply {
            //计算p1、p2两点之间的直线距离，单位：米
            val distance = DistanceUtil.getDistance(p1, p2)
            val (match, rest) = this.partition { distance < it.distance }
            val zoom = if (rest.isNotEmpty()) rest[rest.size - 1].zoom else match[0].zoom
            setMapZoom(zoom)
        }
    }

    private fun setMapZoom(zoomValue: Float? = 15f) {
        val builder = MapStatus.Builder()
        if (latLng != null) builder.target(latLng).zoom(zoomValue ?: 13f)
        else builder.zoom(zoomValue ?: 13f)
        mBaiduMap?.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }
}