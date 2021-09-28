package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.SugesstionTopicDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class TopicDetailsViewModel : BaseViewModel() {

    val tabList = arrayListOf("推荐", "最新", "精华")

    val topPicDetailsTopBean = MutableLiveData<SugesstionTopicDetailBean>()

    fun getData(topicId: String) {
        launch {
            val body = MyApp.mContext.createHashMap()
            body["topicId"] = topicId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getSugesstionTopicDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    topPicDetailsTopBean.value = it
                }
                .onFailure { }
        }
    }
}