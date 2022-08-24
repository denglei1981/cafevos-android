package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleNoticeBean
import com.changanford.circle.bean.CircleNoticeItem
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
class CircleNoticeViewMode : BaseViewModel() {

    val noticeListBean = MutableLiveData<CircleNoticeBean?>()

    fun createNotice(circleId: String, noticeName: String, detailHtml: String, block: () -> Unit) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            body["noticeName"] = noticeName
            body["detailHtml"] = detailHtml

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .initiateCircleNotice(body.header(rKey), body.body(rKey))
                .also {
                    it.msg.toast()
                    if (it.code == 0) {
                        block.invoke()
                    }
                }
        })
    }

    fun circleNotices(page: Int, circleId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["circleId"] = circleId
            }

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .circleNotices(body.header(rKey), body.body(rKey))
                .onSuccess {
                    noticeListBean.value = it
                }
                .onWithMsgFailure {
                    noticeListBean.value = null
                    it?.toast()
                }
        })
    }

    fun updateCircleNotice(
        noticeId: String,
        noticeName: String,
        detailHtml: String,
        block: () -> Unit
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["noticeId"] = noticeId
            body["noticeName"] = noticeName
            body["detailHtml"] = detailHtml

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .updateCircleNotice(body.header(rKey), body.body(rKey))
                .also {
                    it.msg.toast()
                    if (it.code == 0) {
                        block.invoke()
                    }
                }
        })
    }
}