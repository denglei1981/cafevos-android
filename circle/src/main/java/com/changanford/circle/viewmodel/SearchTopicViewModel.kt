package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose
 */
class SearchTopicViewModel:BaseViewModel() {

    val topicBean = MutableLiveData<HomeDataListBean<HotPicItemBean>>()

    fun getData(searchKey:String,page:Int ){
        launch {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["searchKey"] = searchKey
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().searchTopics(body.header(rKey), body.body(rKey))
                .onSuccess {
                    topicBean.value = it
                }
                .onFailure { }
        }
    }
}