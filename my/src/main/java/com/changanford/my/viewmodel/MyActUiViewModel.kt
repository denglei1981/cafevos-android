package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class MyActUiViewModel : BaseViewModel() {

    var myActPublishState: MutableLiveData<List<String>> = MutableLiveData()
    fun getIndexPerms() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.getIndexPerms(body.header(rkey), body.body(rkey))
            }.onSuccess {
                myActPublishState.postValue(it)
            }
        }
    }
}