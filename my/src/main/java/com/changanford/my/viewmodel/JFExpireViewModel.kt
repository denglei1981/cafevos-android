package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.JFExpireBean
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import kotlinx.coroutines.launch

/**
 * @author: niubobo
 * @date: 2024/8/27
 * @description：
 */
class JFExpireViewModel : BaseViewModel() {

    val jfExpireBean = MutableLiveData<JFExpireBean?>()

    fun getData(page: Int, starTime: String, endTime: String, isShowLoading: Boolean = false) {
        viewModelScope.launch {
            fetchRequest(isShowLoading) {
                val body = HashMap<String, Any>()
                body["startExpireTime"] = starTime
                body["endExpireTime"] = endTime
                body["page"] = page
                body["size"] = 20
                val rKey = getRandomKey()
                apiService.getIntegralExpireDetails(body.header(rKey), body.body(rKey))
            }.onSuccess {
                jfExpireBean.value = it
            }.onFailure {
                jfExpireBean.value = null
            }
        }
    }
}