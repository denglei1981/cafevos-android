package com.changanford.circle.viewmodel.circle

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.MyJoinCircleBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithAllSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @author: niubobo
 * @date: 2024/6/4
 * @description：
 */
class HoleCircleViewModel : BaseViewModel() {

    val myJoinCircleBean = MutableLiveData<ArrayList<MyJoinCircleBean>?>()

    fun getCircleHomeData() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getMyJoinCarCircle(body.header(rKey), body.body(rKey))
                .onSuccess {
                    myJoinCircleBean.value = it
                }.onWithMsgFailure {
                    it?.toast()
                    myJoinCircleBean.value = null
                }
        })
    }

    fun getCircleHomeData(circleId: String, block: () -> Unit) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .moveCircleHandler(body.header(rKey), body.body(rKey))
                .onWithAllSuccess {
                    it.msg.toast()
                  block.invoke()
                }.onWithMsgFailure {
                    it?.toast()
                }
        })
    }

}