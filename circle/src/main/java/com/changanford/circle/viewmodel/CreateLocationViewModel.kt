package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.search.poi.PoiSearch
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.HotPicBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.baidu.mapapi.search.core.RouteNode.location
import com.changanford.common.bean.CreateLocation
import com.changanford.common.bean.LocationLotLon
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast


class CreateLocationViewModel : BaseViewModel() {

    val provincesListLiveData = MutableLiveData<ArrayList<ProvinceEntity>>()
    fun getData() {
        val provinces = ArrayList<ProvinceEntity>()
        launch(true, block = {
            val body = HashMap<String, String>()
            body["district"] = "true"
            val rkey = getRandomKey()
            ApiClient.apiService.getAllCity(body.header(rkey), body.body(rkey)).onSuccess {
                provinces.clear()
                it?.forEach { p ->
                    val province = ProvinceEntity(p.province.regionId, p.province.regionName)
                    val citys = ArrayList<CityEntity>()
                    p.citys.forEach { c ->
                        val city = CityEntity(c.city.regionId, c.city.regionName)
                        val countys = ArrayList<CountyEntity>()
                        c.district.forEach { d ->
                            val county = CountyEntity(d.regionId, d.regionName)
                            countys.add(county)
                        }
                        city.countyList = countys
                        citys.add(city)
                    }
                    province.cityList = citys
                    provinces.add(province)
                    provincesListLiveData.postValue(provinces)
                }
            }

        })
    }
    val createLocationLiveData = MutableLiveData<CreateLocation>()
    fun addPostData(
        addrName: String,
        province: String,
        city: String,
        district: String,
        address: String,
        lat: Double,
        lon: Double
    ) {
        launch(true, block = {
//            val body = HashMap<String, Any>()
            val param = HashMap<String, Any>()
            param["addrName"] = addrName
            param["province"] = province
            param["city"] = city
            param["district"] = district
            param["lat"] = lat
            param["lon"] = lon
            param["address"] = address
//            body["postsAddress"] = param
            val rkey = getRandomKey()
            ApiClient.apiService.poastsAddressAdd(param.header(rkey), param.body(rkey)).onSuccess {
                val createLocation =
                    CreateLocation(city.plus("·").plus(addrName), province, lat = lat, lon = lon)
                createLocationLiveData.postValue(createLocation)
                LiveDataBus.get().with(LiveDataBusKey.CREATE_LOCATION,CreateLocation::class.java).postValue(createLocation)
            }

        })
    }

    var mLocationClient: LocationClient? = null
    val locationLotLonLiveData = MutableLiveData<LocationLotLon>()
    fun getLocation() {
        //声明LocationClient类
        mLocationClient = LocationClient(MyApp.mContext)
        //注册监听函数
        //注册监听函数
        mLocationClient?.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation) {
                //获取纬度信息
                //获取纬度信息
                val latitude: Double = location.latitude
                //获取经度信息
                //获取经度信息
                val longitude: Double = location.longitude
                val locationLotLon = LocationLotLon(latitude, longitude)
                locationLotLonLiveData.postValue(locationLotLon)
            }
        })
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
        mLocationClient?.locOption = locationOption
        //开始定位
        //开始定位
        mLocationClient?.start()
        //设置地图单击事件监听
    }


}