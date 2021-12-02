package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChoseCircleBean
import com.changanford.circle.bean.CircleTypesBean
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListViewModel : BaseViewModel() {

    //    val tabList = arrayListOf("全部圈子", "地域圈子", "兴趣圈子")
    val typesBean = MutableLiveData<ArrayList<CircleTypesBean>>()

    val circleListBean = MutableLiveData<HomeDataListBean<ChoseCircleBean>>()

    fun getTypes() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .circleTypes(body.header(rKey), body.body(rKey))
                .onSuccess {
                    typesBean.value = it
                }
        })
    }

    fun getData(type: Int, lng: String, lat: String, page: Int, isRegion: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                if (isRegion == "YES") {
                    if (lng.isNotEmpty()) {
                        it["lng"] = lng
                    }
                    if (lat.isNotEmpty()) {
                        it["lat"] = lat
                    }
                }
                it["type"] = type
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getAllTypeCircles(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleListBean.value = it
                }
        }, error = {
            it.message?.toast()
        })
    }
}