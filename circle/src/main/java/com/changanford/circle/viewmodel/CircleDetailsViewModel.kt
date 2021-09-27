package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMainBottomBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleDetailsViewModel : BaseViewModel() {
    val tabList = arrayListOf("推荐", "最新", "精华")

    val circleBean = MutableLiveData<CircleMainBottomBean>()

    fun getData() {
        launch {
            val body = MyApp.mContext.createHashMap()
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = 4
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleBean.value = it
                }
                .onFailure { }
        }
    }
}