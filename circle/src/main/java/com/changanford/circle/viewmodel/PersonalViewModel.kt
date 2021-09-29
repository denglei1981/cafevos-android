package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class PersonalViewModel : BaseViewModel() {

    val personalBean = MutableLiveData<HomeDataListBean<CircleMemberBean>>()

    fun getData(circleId: String, page: Int) {
        launch {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, String>().also {
                it["circleId"] = circleId
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCircleUsers(body.header(rKey), body.body(rKey))
                .onSuccess {
                    personalBean.value = it
                }
                .onFailure { }
        }
    }

}