package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CarMoreInfoBean
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2024/1/3
 *Purpose
 */
class ChooseCarViewModel : BaseViewModel() {

    //更多车型
    val carListBean = MutableLiveData<ArrayList<SpecialCarListBean>>()

    fun getMoreCar(){
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["type"] = "2"
            val rkey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getCarModelList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    carListBean.value = it
                }.onWithMsgFailure {

                }
        })
    }
}