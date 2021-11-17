package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.CarItemBean
import com.changanford.common.net.*
import com.changanford.common.util.AuthCarStatus
import kotlinx.coroutines.launch

/**
 *  文件名：CarViewModel
 *  创建者: zcy
 *  创建日期：2021/9/15 9:22
 *  描述: TODO
 *  修改描述：TODO
 */
class CarViewModel : ViewModel() {


    suspend fun getAuthStatus(status: AuthCarStatus) {
        var car = fetchRequest {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            apiService.getAuthStatus(body.header(rkey), body.body(rkey))
        }
    }

    var carAuth: MutableLiveData<ArrayList<CarItemBean>> = MutableLiveData()

    fun queryAuthCarAndIncallList(status: AuthCarStatus) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.queryAuthCarAndIncallList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                carAuth.postValue(it)
            }.onFailure {
                carAuth.postValue(null)
            }
        }
    }
}