package com.changanford.circle.viewmodel

import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.location.LocationUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/27
 *Purpose
 */
class CircleViewModel : PostRoomViewModel() {


    fun communityIndex() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["lng"] = LocationUtils.mLongitude.value!!
            body["lat"] = LocationUtils.mLatitude.value!!

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .communityIndex(body.header(rKey), body.body(rKey)).also {

                }

        }, error = {
            it.message?.toast()
        })
    }
}