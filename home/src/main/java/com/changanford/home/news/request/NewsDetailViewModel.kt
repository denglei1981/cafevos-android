package com.changanford.home.news.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.news.data.NewsDetailData

/**
 *  资讯详情viewmodel
 * */
class NewsDetailViewModel : BaseViewModel() {
     val newsDetailLiveData = MutableLiveData<UpdateUiState<NewsDetailData>>() // 详情
    /**
     *  资讯详情。
     * */
    fun getNewsDetail(artId:String) {

        launch(false,{
            val requestBody = HashMap<String, Any>()
            requestBody["artId"]=artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getArticleDetails(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<NewsDetailData>(it, true,"")
                    newsDetailLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<NewsDetailData>(false, "")
                    newsDetailLiveData.postValue(updateUiState)
                }
        })
    }



}