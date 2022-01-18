package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.SerachUserAddress
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap

class SearchLocViewModel: BaseViewModel() {


    val  searchUserAdressLiveData = MutableLiveData<List<SerachUserAddress>>() // 关注否?。

    //  搜索用户 自己创建的位置
    fun getSearchUserLocation(){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getUserPostAdress(body.header(rKey), body.body(rKey))
                .onSuccess {
                    searchUserAdressLiveData.postValue(it)
                }
                .onFailure {

                }
        })
    }

}