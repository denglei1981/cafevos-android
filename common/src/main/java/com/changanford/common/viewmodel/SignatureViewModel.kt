package com.changanford.common.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.STSBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @author: niubobo
 * @date: 2024/12/6
 * @description：
 */
class SignatureViewModel:BaseViewModel() {

    val stsBean = MutableLiveData<STSBean>()

    fun getOSS() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getOSS(body.header(rKey), body.body(rKey))
                .onSuccess {
                    stsBean.value = it
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        }, error = {

        })
    }

}