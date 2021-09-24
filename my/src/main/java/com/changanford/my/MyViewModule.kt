package com.changanford.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.MenuBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class MyViewModule : ViewModel() {

    var menuBean = MutableLiveData<MenuBean>()
    fun getMenuList() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryMenuList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                menuBean.postValue(it)
            }
        }

    }
}