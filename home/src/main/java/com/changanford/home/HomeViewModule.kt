package com.changanford.home

import androidx.lifecycle.ViewModel
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header

class HomeViewModule:ViewModel() {
    /**
     * 使用suspend 直接返回
     */
    suspend fun getAdList(type: String) =
        fetchRequest {
            var map = HashMap<String, Any>()
            map["posCode"] = type
            var rkey = getRandomKey()
            apiService.getAdList(map.header(rkey), map.body(rkey))
        }?.data
}