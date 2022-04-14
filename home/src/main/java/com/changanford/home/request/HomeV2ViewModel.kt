package com.changanford.home.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.WResponseBean
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.toast
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.BindCarBean
import com.changanford.home.bean.CouponItem
import com.changanford.home.bean.FBBean
import com.changanford.home.data.TwoAdData

class HomeV2ViewModel : BaseViewModel() {
    val twoBannerLiveData = MutableLiveData<UpdateUiState<TwoAdData>>() //
    val fBBeanLiveData = MutableLiveData<FBBean?>()
    val responseBeanLiveData = MutableLiveData<WResponseBean?>()
//    val permsLiveData= MutableLiveData<List<String>>()

    //app_index_background 背景长图。
    //app_index_banner
    //app_index_topic
    //app_index_ads
    fun getTwoBanner() {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCodes"] = "app_index_background,app_index_banner,app_index_topic,app_index_ads"
            ApiClient.createApi<HomeNetWork>()
                .getTwoBanner(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<TwoAdData>(it, true, "")
                    twoBannerLiveData.postValue(updateUiState)
                }.onWithMsgFailure {

                }.onFailure {
//                    val updateUiState = UpdateUiState<String>(false, it)
//                    bannerLiveData.postValue(updateUiState)
                }
        })
    }
//    fun getIndexPerms(){
//        launch(false, {
//            val body = HashMap<String, Any>()
//            val rkey = getRandomKey()
//            ApiClient.createApi<HomeNetWork>()
//                .getIndexPerms(body.header(rkey), body.body(rkey))
//                .onSuccess {
//                    permsLiveData.postValue(it)
//                }.onWithMsgFailure {
//
//                }.onFailure {
////                    val updateUiState = UpdateUiState<String>(false, it)
////                    bannerLiveData.postValue(updateUiState)
//                }
//        })
//    }
    /**
     * 判断用户是否有可领取的微客服小程序积分
     * */
    fun isGetIntegral() {
        if (MConstant.token.isEmpty()) return
        launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .isGetIntegral(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    fBBeanLiveData.postValue(it)
                }
        })
    }

    /**
     * 领取微客服小程序积分
     * */
    fun doGetIntegral() {
        if (MConstant.token.isEmpty()) {
            startARouter(ARouterMyPath.SignUI)
            return
        }
        launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .doGetIntegral(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    responseBeanLiveData.postValue(WResponseBean(isSuccess = true))
                }.onWithMsgFailure {
                    it?.toast()
                    responseBeanLiveData.postValue(null)
                }
        })
    }


    val confirmCarLiveData = MutableLiveData<String>()
    var vinStr: String = ""
    fun confirmBindCar(isConfirm: Int, vin: String) {
        vinStr = vin
        if (MConstant.token.isEmpty()) return
        launch(false, {
            val body = HashMap<String, Any>()
            body["isConfirm"] = isConfirm
            body["vin"] = vin
            val randomKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .confirmBindCar(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    confirmCarLiveData.postValue(it)
                }
        })
    }

    val receiveListLiveData = MutableLiveData<MutableList<CouponsItemBean>>()
    fun receiveList() {
        launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["popup"]="YES"
            ApiClient.createApi<HomeNetWork>()
                .receiveList(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    if (it != null && it.size > 0) {
                        receiveListLiveData.postValue(it)
                    }
                }.onWithMsgFailure {
                    it?.toast()
                }
        })
    }
    fun receiveCoupon(list:ArrayList<String>) {
        launch(true, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["couponSendIds"]=list
            ApiClient.createApi<HomeNetWork>()
                .receiveList(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    if (it != null && it.size > 0) {
                        receiveListLiveData.postValue(it)
                    }
                }
        })
    }

}