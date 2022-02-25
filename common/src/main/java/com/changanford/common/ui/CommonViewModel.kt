package com.changanford.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.JumpDataBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class CommonViewModel : ViewModel() {

    fun scanRequest(result: String, callBack: ScanCallBack) {
        viewModelScope.launch {
            var request = fetchRequest() {
                val map = HashMap<String, Any>()
                map["code"] = result
                var rkey = getRandomKey()
                apiService.scan(map.header(rkey), map.body(rkey))
            }
            if (request.code == 0) {
                callBack.success(request)
            }
        }

    }
}

interface ScanCallBack {
    fun success(s: CommonResponse<JumpDataBean>?)
}