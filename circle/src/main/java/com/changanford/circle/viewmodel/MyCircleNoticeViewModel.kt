package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleNoticeBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class MyCircleNoticeViewModel:BaseViewModel() {

    val noticeListBean = MutableLiveData<CircleNoticeBean>()

    fun circleMyNotices(page: Int, circleId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["circleId"] = circleId
            }

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .circleMyNotices(body.header(rKey), body.body(rKey))
                .onSuccess {
                    noticeListBean.value = it
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }
}