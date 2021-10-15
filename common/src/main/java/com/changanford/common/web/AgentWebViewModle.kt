package com.changanford.common.web

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.Base64Utils
import com.huawei.hms.common.ApiException
import com.xiaomi.push.it
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.evos.ui.model.AgentWebViewModle
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/13 15:50
 * @Description: 　
 * *********************************************************************************
 */
class AgentWebViewModle : ViewModel() {
//    var ossRepository = OssRepository()
    var _pic: MutableLiveData<String> = MutableLiveData<String>()
    var _location: MutableLiveData<String> = MutableLiveData<String>()
    var located = false

    /**
     * 上传图片（对外接口）
     */
    fun uploadImg(base64Str: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.getOSS(body.header(rkey),body.body(rkey))
            }.onSuccess { stsBean->
                val dialog = LoadDialog(BaseApplication.INSTANT)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setLoadingText("图片上传中..")
//                dialog.show()
                var imgBytes = Base64Utils.base64ToByteArray(base64Str)
//                viewModelScope.launch(Dispatchers.IO) {
                    imgBytes?.let {
                        stsBean?.let { it1 -> uploadImgs(it, it1, dialog) }
                    }
//                }
            }
        }
    }

    private fun uploadImgs(
        imgBytes: ByteArray,
        stsBean: STSBean,
        dialog: LoadDialog
    ) {
        AliYunOssUploadOrDownFileConfig.getInstance(BaseApplication.INSTANT).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        val path =
            stsBean.tempFilePath.plus(System.currentTimeMillis().toString()).plus(".jpg")
        AliYunOssUploadOrDownFileConfig.getInstance(BaseApplication.INSTANT)
            .uploadFile(true, imgBytes, stsBean.bucketName, path, "", "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(BaseApplication.INSTANT).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                viewModelScope.launch(Dispatchers.Main) {
//                    dialog.dismiss()
                }
//                _pic.postValue(stsBean.cdn.plus(path))
                _pic.postValue(path)
            }

            override fun onUploadFileFailed(errCode: String) {
//                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
            }
        })
    }

    /**
     * 微信
     */
    fun getWxPayInfo() {
//        var body = HashMap<String, Any>()
//        var rKey = getRandomKey()
//        RepositoryManager.obtainService(ApiService::class.java)
//            .getWxPayInfo(
//                getHeader(body, rKey),
//                getRequestBody(body, rKey)
//            )
//            .compose(ResponseTransformer())
//            .subscribe(object : ResponseObserver<BaseBean<WxPayBean>>(this, true) {
//
//                override fun onFail(e: ApiException) {
//                }
//
//                override fun onSuccess(response: BaseBean<WxPayBean>) {
//                    var config = response.data
//                    var map = HashMap<String, Any>()
//                    map["payCode"] = 2
//                    map["param"] = JSON.toJSON(config)
//                    map["callback"] = "callback"
//                    LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_PAY).postValue(map)
//                }
//            })
    }

    /**
     * 支付宝
     */
    fun getAliPayInfo() {
//        var body = HashMap<String, Any>()
//        var rKey = getRandomKey()
//        RepositoryManager.obtainService(ApiService::class.java)
//            .getAliPayInfo(
//                getHeader(body, rKey),
//                getRequestBody(body, rKey)
//            )
//            .compose(ResponseTransformer())
//            .subscribe(object : ResponseObserver<BaseBean<String>>(this, true) {
//
//                override fun onFail(e: ApiException) {
//                }
//
//                override fun onSuccess(response: BaseBean<String>) {
//                    var config = response.data
//                    var map = HashMap<String, Any>()
//                    map["payCode"] = 1
//                    map["param"] = config.substring(1, config.length - 1)
//                    map["callback"] = "callback"
//                    LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_PAY).postValue(map)
//                }
//            })
    }

    /**
     * 初始化定位参数配置
     */
    fun initLocationOption() {
        located = false
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        val locationClient =
            LocationClient(BaseApplication.INSTANT)
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        val myLocationListener = MyLocationListener()
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener)
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = true
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
//        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.locOption = locationOption
        //开始定位
        locationClient.start()
    }

    /**
     * 实现定位回调
     */
    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            val latitude = location.latitude
            //获取经度信息
            val longitude = location.longitude
            //获取定位精度，默认值为0.0f
            val radius = location.radius
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            val coorType = location.coorType
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            val errorCode = location.locType
            val adCode = location.adCode
            val address = location.address.address
            var js = JSONObject()
            js.put("latitude", latitude.toString())
            js.put("longitude", longitude.toString())
            js.put("address", address)
            js.put("adCode", adCode)
            js.put("cityCode", adCode)
            js.put("countryCode", location.countryCode)
            js.put("country", location.country)
            js.put("cityCode", location.cityCode)
            js.put("city", location.city)
            js.put("district", location.district)
            js.put("province",location.province)
            Log.e("LOCATION", "${js.toString()}")
//            setParam(
//                BaseApplication.INSTANT,
//                MConstant.LOCATION_BD,
//                js.toString()
//            )

            if (!located) {
                _location.postValue(js.toString())
                located = true
            }
        }
    }
    fun clearAllPost(){
//        viewModelScope.launch(Dispatchers.IO) {
//            PostDatabase.getInstance(context).getPostDao()
//                .clearAll()
//        }
    }
}