package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.HotPicBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class HotTopicViewModel : BaseViewModel() {

    var page = 1

    val hotTopicBean = MutableLiveData<HotPicBean>()

    fun getData() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getSugesstionTopics(body.header(rKey), body.body(rKey))
                .onSuccess {
                    hotTopicBean.value = it
                }
                .onFailure { }
        })
    }

}