package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.PostBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleDetailsViewModel : BaseViewModel() {

    val tabList = arrayListOf("推荐", "最新", "精华")

    val circleBean = MutableLiveData<PostBean>()

    val listBean = MutableLiveData<PostBean>()

    val circleDetailsBean = MutableLiveData<CircleDetailBean>()

    fun getData(viewType: Int, page: Int) {
        launch(block = {
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
        })
    }

    fun getListData(viewType: Int, topicId: String, circleId: String,page: Int) {
        launch (block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
                if(topicId.isNotEmpty()){
                    it["topicId"] = topicId
                }
                if(circleId.isNotEmpty()){
                    it["circleId"] = circleId
                }
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    listBean.value = it
                }
                .onFailure { }
        })
    }

    fun getCircleDetails(circleId: String) {
        launch(block ={
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().queryCircle(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleDetailsBean.value = it
                }
                .onFailure { }
        } )
    }
}