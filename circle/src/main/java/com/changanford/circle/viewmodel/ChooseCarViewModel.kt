package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CarMoreInfoBean
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
    val carMoreInfoBean=MutableLiveData<CarMoreInfoBean?>()

    fun getMoreCar(){
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.getMoreCareInfo(hashMap.header(randomKey),hashMap.body(randomKey))
            }.onSuccess {
                carMoreInfoBean.postValue(it)
            }.onWithMsgFailure {
                carMoreInfoBean.postValue(null)
                it?.toast()
            }
        }
    }
}