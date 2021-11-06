package com.changanford.home.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.data.TwoAdData

class HomeV2ViewModel : BaseViewModel() {
    val twoBannerLiveData = MutableLiveData<UpdateUiState<TwoAdData>>() //

//    val permsLiveData= MutableLiveData<List<String>>()

    //app_index_background 背景长图。
    //app_index_banner
    //app_index_topic
    //app_index_ads
    fun getTwoBanner() {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCodes"] = "app_index_background,app_index_banner,app_index_topic,app_index_ads"
            ApiClient.createApi<HomeNetWork>()
                .getTwoBanner(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<TwoAdData>(it, true, "")
                    twoBannerLiveData.postValue(updateUiState)
                }.onWithMsgFailure {

                }.onFailure {
//                    val updateUiState = UpdateUiState<String>(false, it)
//                    bannerLiveData.postValue(updateUiState)
                }
        })
    }
//    fun getIndexPerms(){
//        launch(false, {
//            val body = HashMap<String, Any>()
//            val rkey = getRandomKey()
//            ApiClient.createApi<HomeNetWork>()
//                .getIndexPerms(body.header(rkey), body.body(rkey))
//                .onSuccess {
//                    permsLiveData.postValue(it)
//                }.onWithMsgFailure {
//
//                }.onFailure {
////                    val updateUiState = UpdateUiState<String>(false, it)
////                    bannerLiveData.postValue(updateUiState)
//                }
//        })
//    }

}