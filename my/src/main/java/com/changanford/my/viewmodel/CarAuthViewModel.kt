package com.changanford.my.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.util.AuthCarStatus
import com.changanford.common.util.MConstant
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.xiaomi.push.it
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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


    fun cmcImageUpload(ossUrl: String, type: Int, result: (CommonResponse<CmcUrl>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["fileName"] = System.currentTimeMillis().toString().plus(".jpg")
                body["type"] = type
                body["ossUrl"] = ossUrl
                body["watermark"] = "仅用于长安福特私域平台车主认证使用"
                body["needWatermark"] = true
                val rkey = getRandomKey()
                apiService.cmcImageUpload(body.header(rkey), body.body(rkey))
            })
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

    fun queryAuthCarDetail(
        vin: String,
        authId: String,
        result: (CommonResponse<CarItemBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["vin"] = vin
                body["authId"] = authId
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
        authId: String,
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
                    body["authId"] = authId
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

    var smsSuccess: MutableLiveData<Boolean> = MutableLiveData()

    fun smsCacSmsCode(mobile: String) {
        if (mobile?.isNullOrEmpty() == true) {
            toastShow("请输入手机号")
            return
        }
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["phone"] = mobile
                var rkey = getRandomKey()
                apiService.sendCacSmsCode(body.header(rkey), body.body(rkey))
            }.onSuccess {
                smsSuccess.postValue(true)
            }.onWithMsgFailure {
                it?.let {
                    toastShow(it)
                }
            }
        }
    }

    /**
     * 获取验证码倒计时
     */
    var subscribe: Disposable? = null

    var smsGetHint: String by mutableStateOf("获取验证码")
    var btnGetSmsIsEnabled: Boolean by mutableStateOf(true)

    fun smsCountDownTimer() {
        var time: Long = 60
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.rxjava3.core.Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    subscribe = d
                    btnGetSmsIsEnabled = false
                }

                override fun onNext(t: Long) {
                    if (t < 59) {
                        time -= 1
                        smsGetHint = "${time}s"
                    } else {
                        onComplete()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                    smsGetHint = "获取验证码"
                    btnGetSmsIsEnabled = true
                    subscribe?.dispose()
                }
            })
    }


    fun setDefalutCar(carSalesInfoId: String, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest(true) {
                val body = HashMap<String, Any>()
//                body["vin"] = vin
                body["carSalesInfoId"] = carSalesInfoId
                val rKey = getRandomKey()
                apiService.setDefaultCar(body.header(rKey), body.body(rKey))

            })
        }
    }

    fun deleteCar(

        vin: String,
        phone: String? = "",
        smsCode: String? = "",
        id: String,
        result: (CommonResponse<String>) -> Unit
    ) {
        viewModelScope.launch {
            result(
                fetchRequest {
                    val body = HashMap<String, Any>()
//                    body["phone"] = phone ?: ""
//                    body["smsCode"] = smsCode ?: ""
                    body["id"] = id
                    body["vin"] = vin
                    val rKey = getRandomKey()
                    apiService.deleteCar(body.header(rKey), body.body(rKey))
                }
            )
        }
    }

    val waitCarLiveData = MutableLiveData<MutableList<BindCarBean>>()
    fun isWaitBindingCar() {
        if (MConstant.token.isEmpty()) return

        viewModelScope.launch {
            fetchRequest {

                val body = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.waitBindCarList(body.header(randomKey), body.body(randomKey))
                    .onSuccess {
                        waitCarLiveData.postValue(it)
                    }
            }


        }


    }


    val confirmCarLiveData = MutableLiveData<String>()
    var vinStr: String = ""
    fun confirmBindCar(isConfirm: Int, vin: String) {
        vinStr = vin
        if (MConstant.token.isEmpty()) return
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                body["isConfirm"] = isConfirm
                body["vin"] = vin
                val randomKey = getRandomKey()
                apiService.confirmBindCar(body.header(randomKey), body.body(randomKey))
                    .onSuccess {
                        confirmCarLiveData.postValue(it)
                    }
            }
        }

    }

    val waitReceiveListLiveData = MutableLiveData<ArrayList<WaitReceiveBean>>()

    /**
     * 待领取交车礼积分列表
     */
    fun waitReceiveList() {
        //交车礼改为不弹窗，2022-0922
        /*viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                val randomKey = getRandomKey()
                body["popup"] = "YES"
                apiService.waitReceiveList(body.header(randomKey), body.body(randomKey))
                    .onSuccess {
                        if (it != null && it.size > 0) {
                            waitReceiveListLiveData.postValue(it)
                        }
                    }.onWithMsgFailure {
                        it?.toast()
                    }
            }
        }*/
    }
}