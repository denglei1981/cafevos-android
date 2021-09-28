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

    val listBean = MutableLiveData<CircleMainBottomBean>()

    fun getData(viewType: Int, page: Int) {
        launch {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleBean.value = it
                }
                .onFailure { }
        }
    }

    fun getListData(viewType: Int, topicId: String, page: Int) {
        launch {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
                it["topicId"] = topicId
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    listBean.value = it
                }
                .onFailure { }
        }
    }
}