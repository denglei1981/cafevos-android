package com.changanford.home.news.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.SpecialListMainBean
import com.changanford.home.news.data.SpecialDetailData

/**
 *  专题详情
 * */
class SpecialDetailViewModel : BaseViewModel() {
     val specialDetailLiveData = MutableLiveData<UpdateUiState<SpecialDetailData>>() // 专题详情

    /**
     *  专题列表顶部
     * */
    fun getSpecialDetail(specialTopicId:String) {

        launch(false,{
            val requestBody = HashMap<String, Any>()
            requestBody["specialTopicId"]=specialTopicId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSpecialTopicList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<SpecialDetailData>(it, true,"")
                    specialDetailLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<SpecialDetailData>(false, "")
                    specialDetailLiveData.postValue(updateUiState)
                }
        })
    }



}