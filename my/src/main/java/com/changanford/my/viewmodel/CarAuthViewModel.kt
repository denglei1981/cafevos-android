package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.util.AuthCarStatus
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.toastShow
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

    var carAuth: MutableLiveData<CarAuthBean> = MutableLiveData()

    fun queryAuthCarAndIncallList(status: AuthCarStatus) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.queryAuthCarList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                carAuth.postValue(it)
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

    /****------------------****/
    fun getSmsCode(mobile: String?, result: (CommonResponse<String>) -> Unit) {
        if (mobile?.isNullOrEmpty() == true) {
            toastShow("未获取到手机号")
            return
        }
        viewModelScope.launch {
            result(fetchRequest(showLoading = true) {
                var body = HashMap<String, Any>()
                body["phone"] = mobile
                var rKey = getRandomKey()
                apiService.sendCacSmsCode(body.header(rKey), body.body(rKey))
            })
        }
    }


    fun changePhoneBind(
        vin: String,
        oldPhone: String? = "",
        smsCode: String? = "",
        result: (CommonResponse<String>) -> Unit
    ) {
        viewModelScope.launch {
            result(
                fetchRequest {
                    var body = HashMap<String, Any>()
                    body["oldPhone"] = oldPhone ?: ""
                    body["smsCode"] = smsCode ?: ""
                    body["vin"] = vin
                    var rKey = getRandomKey()
                    if (oldPhone.isNullOrEmpty() || smsCode.isNullOrEmpty()) {
                        apiService.changePhoneBind(body.header(rKey), body.body(rKey))
                    } else {
                        apiService.changeOldPhoneBind(body.header(rKey), body.body(rKey))
                    }
                }
            )
        }
    }

    /**
     * 车主权益
     */
    fun carAuthQY(result: (CommonResponse<CarAuthQYBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = java.util.HashMap<String, Any>()
                body["configKey"] = "car_auth_con"
                body["obj"] = true
                var rkey = getRandomKey()
                apiService.carAuthQY(body.header(rkey), body.body(rkey))
            })
        }
    }
}