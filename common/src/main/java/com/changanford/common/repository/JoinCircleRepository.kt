package com.changanford.common.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.JoinCircleCheckBean
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose
 */
class JoinCircleRepository(var viewModel: ViewModel) {
    var _ads = MutableLiveData<JoinCircleCheckBean>()
    fun checkJoin(circleId: String) {
        viewModel.viewModelScope.launch {
            var body = HashMap<String, Any>()
            body["circleId"] = circleId
            var rkey = getRandomKey()
            fetchRequest {
                apiService.onlyAuthJoinCheck(body.header(rkey), body.body(rkey))
            }.onSuccess {
                _ads.postValue(it)
            }
        }
    }
}