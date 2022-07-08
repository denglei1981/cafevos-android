package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.bean.BindingCar

class WaitBindingViewModel: BaseViewModel()  {
    val confirmBindLiveData = MutableLiveData<UpdateUiState<String>>() // 详情

    fun confirmBindCarList(waitAuthCarList: List<BindingCar>) {
        launch(true, {
            val requestBody = HashMap<String, Any>()
            requestBody["waitAuthCarList"] = waitAuthCarList
            val rkey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .confirmBindCarList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<String>(it, true, "")
                    confirmBindLiveData.postValue(updateUiState)
                    LiveDataBus.get().with(LiveDataBusKey.REFRESH_WAIT).postValue(false)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<String>(it, false, "")
                    confirmBindLiveData.postValue(updateUiState)
                }
        })
    }
}