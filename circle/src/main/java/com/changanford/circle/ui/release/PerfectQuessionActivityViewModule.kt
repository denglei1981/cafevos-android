package com.changanford.circle.ui.release

import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.LocationDataBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 * 单选多选完善问题
 */
class PerfectQuessionActivityViewModule() : BaseViewModel() {


    fun GetOSS(rpo : (CommonResponse<STSBean>)->Unit) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            rpo(fetchRequest {
                apiService.getOSS(body.header(rkey), body.body(rkey))
            })
        }
    }
    /**
     * 获取省市区ID
     */
    fun getCityDetailBylngAndlat(latY: Double,lngX:Double,responseObserver: (CommonResponse<LocationDataBean>)->Unit) {
        var body = HashMap<String, Any>()
        body["latY"] = latY
        body["lngX"] = lngX
        var rkey = getRandomKey()
        viewModelScope.launch {
            responseObserver(fetchRequest {
                apiService.getCityDetailBylngAndlat(body.header(rkey),body.body(rkey))
            })
        }
    }
}