package com.changanford.common.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.AdBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 * 广告接口封装
 * 广告接口使用位置多，简化代码
 */
class AdsRepository(var viewModel: ViewModel) {
    var _ads: MutableLiveData<ArrayList<AdBean>> = MutableLiveData<ArrayList<AdBean>>()
    fun getAds(code: String) {
        viewModel.viewModelScope.launch {
            var body = HashMap<String, Any>()
            body["posCode"] = code
            var rkey = getRandomKey()
            fetchRequest {
                apiService.getHeadBanner(body.header(rkey), body.body(rkey))
            }.onSuccess {
                _ads.postValue(it)
            }
        }
    }
}