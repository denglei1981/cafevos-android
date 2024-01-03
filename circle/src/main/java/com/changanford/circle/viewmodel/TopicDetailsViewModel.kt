package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.SugesstionTopicDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.SpecialCarListBean
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
    val carListBean = MutableLiveData<ArrayList<SpecialCarListBean>>()

    fun getData(topicId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["topicId"] = topicId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getSugesstionTopicDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    if (it?.isBuyCarDiary == 1) {
                        getCarModelList()
                    }
                    topPicDetailsTopBean.value = it
                }
                .onFailure { }
        })
    }

    private fun getCarModelList() {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["type"] = "2"
            val rkey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getCarModelList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    carListBean.value = it
                }.onWithMsgFailure {

                }
        })
    }
}