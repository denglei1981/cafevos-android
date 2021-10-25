package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMainBean
import com.changanford.common.MyApp
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.net.*
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/27
 *Purpose
 */
class CircleViewModel : PostRoomViewModel() {

    val circleBean = MutableLiveData<CircleMainBean>()

    fun communityIndex(lng: Double? = null, lat: Double? = null) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            lng?.let { body["lng"] = lng }
            lat?.let { body["lat"] = lat }

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .communityIndex(body.header(rKey), body.body(rKey)).onSuccess {
                    circleBean.value = it
                }

        }, error = {
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
            it.message?.toast()
        })
    }
}