package com.changanford.circle.viewmodel

import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.net.ApiClient.circleService
import com.changanford.common.utilext.createHashMap

class PostGraphicViewModel : BaseViewModel() {

    fun data() {
        launch(true) {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            circleService.sendFordSmsCode(body.header(rKey), body.body(rKey))
                .onSuccess { }
                .onFailure { }
        }
    }

}