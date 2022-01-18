package com.changanford.circle.viewmodel

import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

class SearchLocViewModel: BaseViewModel() {


    //  搜索用户 自己创建的位置
    fun getSearchUserLocation(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().searchTopics(body.header(rKey), body.body(rKey))
                .onSuccess {

                }
                .onFailure { }
        })
    }

}