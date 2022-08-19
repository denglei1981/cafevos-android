package com.changanford.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.FordPhotosBean
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */
class FordAlbumViewModel : BaseViewModel() {

    val photosBean = MutableLiveData<FordPhotosBean?>()

    fun getFordPhotos() {
        viewModelScope.launch {
            val request = fetchRequest {
                val body = HashMap<String, Any>()
                val rkey = getRandomKey()
                apiService.getFordPhotos(body.header(rkey), body.body(rkey))
            }
            if (request.code == 0) {
                photosBean.value = request.data
            } else {
                request.msg.toast()
            }
        }
    }
}