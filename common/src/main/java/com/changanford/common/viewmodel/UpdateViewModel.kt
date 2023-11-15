package com.changanford.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.UpdateInfo
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import kotlinx.coroutines.launch

class UpdateViewModel : ViewModel() {
    var _updateInfo = MutableLiveData<UpdateInfo?>()

    fun getUpdateInfo() {
        viewModelScope.launch {
            val request = fetchRequest {
                var body = HashMap<String, Any>()
                body["type"] = 0
                var rkey = getRandomKey()
                apiService.getUpdateInfo(body.header(rkey), body.body(rkey))
            }
            if (request.code == 0) {
                _updateInfo.postValue(request.data)
            } else {
                _updateInfo.postValue(null)
            }
        }
    }
}