package com.changanford.home.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.request.GetUpdateAgreeResult
import com.changanford.common.utilext.toast
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.FBBean
import com.changanford.home.data.TwoAdData
import kotlinx.coroutines.launch

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

    val receiveListLiveData = MutableLiveData<MutableList<CouponsItemBean>>()

    /**
     * 优惠券弹窗
     */
    fun receiveList() {
        launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["popup"] = "YES"
            ApiClient.createApi<HomeNetWork>()
                .receiveList(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    if (it != null && it.size > 0) {
                        receiveListLiveData.postValue(it)
                    } else waitReceiveList()
                }.onWithMsgFailure {
                    waitReceiveList()
                    it?.toast()
                }
        })
    }

    val waitReceiveListLiveData = MutableLiveData<ArrayList<WaitReceiveBean>>()

    /**
     * 待领取交车礼积分列表
     */
    fun waitReceiveList() {
        //交车礼改为不弹窗，2022-0922
        /*launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["popup"] = "YES"
            ApiClient.createApi<HomeNetWork>()
                .waitReceiveList(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    if (it != null && it.size > 0) {
                        waitReceiveListLiveData.postValue(it)
                    } else {
                        loginGetJump()
                    }
                }.onWithMsgFailure {
                    loginGetJump()
                    it?.toast()
                }
        })*/
    }

    val newEstOneBean = MutableLiveData<NewEstOneBean>()

    fun getNewEstOne() {
        launch(false, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["posCode"] = "index_popover"
            ApiClient.createApi<HomeNetWork>()
                .newEstOne(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    it?.let {
                        newEstOneBean.postValue(it)
                    }
                }
        })
    }

    val updateAgreeBean = MutableLiveData<BizCodeBean>()

    //隐私协议更新弹窗
    fun getUpdateAgree(lifecycleOwner: LifecycleOwner) {
        com.changanford.common.util.request.getUpdateAgree(lifecycleOwner,
            object : GetUpdateAgreeResult {
                override fun success(result: BizCodeBean) {
                    updateAgreeBean.value = result
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

    /**
     * 登录成功获取跳转的jump（主要用于H5用户引流）
     */
    private fun loginGetJump() {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                val body = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.loginJump(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.apply {
                    if (jumpStatus == "0") {
                        changeJumpStatus()
                        JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                    }
                }
            }
        }
    }

    private fun changeJumpStatus() {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                val body = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.changeJumpStatus(body.header(rkey), body.body(rkey))
            }
        }
    }

    fun receiveCoupon(list: ArrayList<String>) {
        launch(true, {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["couponSendIds"] = list
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