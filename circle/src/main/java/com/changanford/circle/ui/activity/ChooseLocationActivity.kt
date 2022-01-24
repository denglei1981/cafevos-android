package com.changanford.circle.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.GeoCodeOption
import com.baidu.mapapi.search.geocode.GeoCoder
import com.baidu.mapapi.search.poi.*
import com.changanford.circle.R
import com.changanford.circle.adapter.LocaAdapter
import com.changanford.circle.bean.CityEntity
import com.changanford.circle.databinding.ChooselocationBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import java.util.*
import kotlin.collections.ArrayList
import com.baidu.mapapi.search.poi.PoiCitySearchOption
import com.changanford.common.util.LocationServiceUtil


/**
 * 定位
 */
@Route(path = ARouterCirclePath.ChooseLocationActivity)
class ChooseLocationActivity : BaseActivity<ChooselocationBinding, EmptyViewModel>(),
    OnGetPoiSearchResultListener {
    lateinit var locaAdapter: LocaAdapter
    var lat = 0.0
    var lon = 0.0
    var city = ""
    lateinit var mLocationClient: LocationClient
    fun ismLocationClientInitialzed() = ::mLocationClient.isInitialized
    lateinit var mPoiSearch: PoiSearch
    var ml: ArrayList<PoiInfo> = ArrayList()

    var locationed = false
    lateinit var poiInfo: PoiInfo
    var isselected = false


    private fun initlocation() {
        //声明LocationClient类
        mLocationClient = LocationClient(this)
        //注册监听函数
        //注册监听函数
        mLocationClient.registerLocationListener(MyLocationListener())
        mPoiSearch = PoiSearch.newInstance()
        mPoiSearch.setOnGetPoiSearchResultListener(this)
        val locationOption = LocationClientOption()
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0)
        //可选，设置是否需要地址信息，默认不需要
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = true
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集;
        //可选，默认false，设置是否收集CRASH信息，默认收集;
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption)
        //开始定位
        //开始定位
        mLocationClient.start()
        //设置地图单击事件监听
    }



    override fun initData() {


        LiveDataBus.get().with(LiveDataBusKey.CREATE_COLSE_LOCATION, Boolean::class.java)
            .observe(this,
                {
                    if (it) {
                        finish()
                    }
                })

        LiveDataBus.get().with(LiveDataBusKey.ColseCHOOSELOCATION, Boolean::class.java)
            .observe(this,
                {
                    if (it) {
                        finish()
                    }
                })
        binding.etsearch.setOnClickListener {
            city.let {
                var bundle = Bundle()
                bundle.putDouble("Lat", lat)
                bundle.putDouble("Lon", lon)
                bundle.putString("city", city)
                val intent = Intent(this, SearchLocActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                overridePendingTransition(0, 0);
            }
        }
        binding.tvBuxs.setOnClickListener {
//            binding.ivselect.visibility = View.VISIBLE
//            locaAdapter.setSelectID(-1)
//            locaAdapter.notifyDataSetChanged()
//            isselected = true
            LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
                        .postValue(binding.tvBuxs.text.toString())
            finish()
        }
        binding.title.barTvOther.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, CreateLocationActivity::class.java)
            startActivity(intent)
//            if (isselected) {
//                if (locaAdapter.id == -1) {
//                    LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
//                        .postValue(binding.tvBuxs.text.toString())
//                } else {
//                    LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(poiInfo)
//                }
//                finish()
//            } else {
//                "请选择地址".toast()
//            }
        }
        binding.title.barImgBack.setOnClickListener {
            finish()
        }
        binding.tvLocation.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, ChoiceAllCityActivity::class.java)
            startActivityForResult(intent, 121312)
//            registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult()
//            ) {
//                val data = it.data
//                val resultCode = it.resultCode
//                data?.getSerializableExtra("city").toString().toast()
//            }.launch(Intent(this,ChoiceAllCityActivity::class.java))


        }
    }

    fun choiceOver(){
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(poiInfo)
//        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(poiInfo)
//        LiveDataBus.get().with(LiveDataBusKey.ColseCHOOSELOCATION, Boolean::class.java)
//            .postValue(true)
        finish()
    }
    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    inner class MyLocationListener : BDLocationListener {
        override fun onReceiveLocation(location: BDLocation) {

            //获取定位结果
            location.time //获取定位时间
            location.locationID //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.locType //获取定位类型
            location.latitude //获取纬度信息
            location.longitude //获取经度信息
            location.radius //获取定位精准度
            location.addrStr //获取地址信息
            location.country //获取国家信息
            location.countryCode //获取国家码
            location.city //获取城市信息
            location.cityCode //获取城市码
            location.district //获取区县信息
            location.street //获取街道信息
            location.streetNumber //获取街道码
            location.locationDescribe //获取当前位置描述信息
            location.poiList //获取当前位置周边POI信息
            location.buildingID //室内精准定位下，获取楼宇ID
            location.buildingName //室内精准定位下，获取楼宇名称
            location.floor //室内精准定位下，获取当前位置所处的楼层信息
            val js = JSONObject()
            js["latitude"] = location.latitude
            js["longitude"] = location.longitude
            js["address"] = location.address
            js["adCode"] = location.adCode
            js["cityCode"] = location.cityCode
            js["city"] = location.city
            js["countryCode"] = location.countryCode
            js["country"] = location.country
            js["cityCode"] = location.cityCode
            js["district"] = location.district
            js["province"] = location.province
            //经纬度
            lat = location.latitude
            lon = location.longitude
            city = location.city
            binding.tvLocation.text = city
            poi()
        }
    }


    fun poi() {
        if (!locationed) {
            mPoiSearch.searchNearby(
                PoiNearbySearchOption()
                    .location(LatLng(lat, lon))
                    .radius(1000)
                    .keyword("公司")
                    .pageNum(0)
                    .pageCapacity(99)
            )
        }
        locationed = true
    }


    // 正地理编码
    fun rightPoi(cityName: String) {

        mPoiSearch.searchInCity(
            PoiCitySearchOption()
                .city(cityName) //必填
                .keyword("公司") //必填
                .pageCapacity(99)
                .pageNum(0)
        )
        city=cityName

    }

    fun hideInput() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ismLocationClientInitialzed()) {
            mLocationClient.stop()
        }
    }

    override fun onGetPoiResult(poiResult: PoiResult?) {
        if (poiResult?.error == SearchResult.ERRORNO.NO_ERROR) {
            ml.clear()
            ml.addAll(poiResult.allPoi)
            locaAdapter.setList(ml)
            locaAdapter.notifyDataSetChanged()
        }
    }

    override fun onGetPoiDetailResult(p0: PoiDetailResult?) {
    }

    override fun onGetPoiDetailResult(p0: PoiDetailSearchResult?) {
    }

    override fun onGetPoiIndoorResult(p0: PoiIndoorResult?) {
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0);
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "添加位置"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "创建地址"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.circle_00095))
        binding.title.barTvOther.textSize = 14f
//        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)

        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        initlocation()
                    }

                    override fun onPermissionDenied(permission: Permission) {
                        AlertDialog(this@ChooseLocationActivity).builder()
                            .setTitle("提示")
                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
                            .setNegativeButton("取消") { finish() }.setPositiveButton(
                                "确定"
                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
                    }
                })
        locaAdapter = LocaAdapter()
        binding.locrec.layoutManager = LinearLayoutManager(this)
        binding.locrec.adapter = locaAdapter

        locaAdapter.setOnItemClickListener { adapter, view, position ->
//            LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(ml[position])
//            locaAdapter.setSelectID(position)
//            locaAdapter.notifyDataSetChanged()
//            binding.ivselect.visibility = View.GONE
//
//            isselected = true

            poiInfo = ml[position]
            choiceOver()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 121312) {
                val dataExtra = data?.getSerializableExtra("city")
                if(dataExtra!=null){
                    val cityEntity = data?.getSerializableExtra("city") as CityEntity
                    binding.tvLocation.text = cityEntity.name
                    rightPoi(cityName = cityEntity.name)
                }
        }
    }
}