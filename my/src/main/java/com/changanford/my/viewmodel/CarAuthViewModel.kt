package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.CarAUthResultBean
import com.changanford.common.bean.CarItemBean
import com.changanford.common.bean.OcrBean
import com.changanford.common.bean.OcrRequestBean
import com.changanford.common.net.*
import com.changanford.common.util.AuthCarStatus
import com.changanford.common.util.room.UserDatabase
import kotlinx.coroutines.launch

/**
 *  文件名：CarAuthViewModel
 *  创建者: zcy
 *  创建日期：2021/9/28 9:15
 *  描述: TODO
 *  修改描述：TODO
 */
class CarAuthViewModel : ViewModel() {

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }


    /**
     * OCR
     *  HTTP_URL(1, "网络地址"),     BASE64(2, "BASE64");
     *   VIN(1, "车架号"),     ID_CARD(2, "身份证"),     DRIVER_LICENCE(3, "驾驶证"),     WALK_LICENCE(4, "行驶证");
     */
    fun ocr(ocrBean: OcrRequestBean?, result: (CommonResponse<OcrBean>) -> Unit) {
        ocrBean?.let {
            viewModelScope.launch {
                result(fetchRequest {
                    var body = HashMap<String, Any>()
                    body["imgExt"] = ocrBean.imgExt
                    body["imgType"] = ocrBean.imgType
                    body["ocrSceneType"] = ocrBean.ocrSceneType
                    var rkey = getRandomKey()
                    apiService.ocr(body.header(rkey), body.body(rkey))
                })
            }
        }
    }

    /**
     * 提交车主认证
     */
    fun submitCarAuth(
        body: HashMap<String, Any>,
        result: (CommonResponse<CarAUthResultBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(
                fetchRequest {
                    var rkey = getRandomKey()
                    apiService.submitCarAuth(body.header(rkey), body.body(rkey))
                }
            )
        }
    }

    var carAuth: MutableLiveData<ArrayList<CarItemBean>> = MutableLiveData()

    fun queryAuthCarAndIncallList(status: AuthCarStatus) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.queryAuthCarList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                carAuth.postValue(it?.carList)
            }.onFailure {
                carAuth.postValue(null)
            }
        }
    }

    fun queryAuthCarDetail(vin: String, result: (CommonResponse<CarItemBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["vin"] = vin
                var rkey = getRandomKey()
                apiService.queryAuthDetail(body.header(rkey), body.body(rkey))
            })
        }
    }
}