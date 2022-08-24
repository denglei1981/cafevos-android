package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleActivityBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose
 */
class CircleActivityListViewModel : BaseViewModel() {

    val circleActivityBean = MutableLiveData<CircleActivityBean>()

    fun circleActivity(page: Int, circleId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["circleId"] = circleId
            }

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .circleActivity(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleActivityBean.value = it
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

}