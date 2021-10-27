package com.changanford.home.news.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.NewsListMainBean
import com.changanford.home.bean.SpecialListMainBean


class FindNewsListViewModel : BaseViewModel() {
    val specialListLiveData = MutableLiveData<UpdateUiState<SpecialListMainBean>>() // 专题列表 轮播图。

    val newsListLiveData = MutableLiveData<UpdateUiState<NewsListMainBean>>() // 专题列表 轮播图。
    var pageNo: Int = 1
    val followLiveData = MutableLiveData<UpdateUiState<Any>>() // 关注否?。
    /**
     *  专题列表顶部
     * */
    fun getSpecialList() {
        launch(block = {
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = 1
            requestBody["pageSize"] = 5
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSpecialList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<SpecialListMainBean>(it, true, "")
                    specialListLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<SpecialListMainBean>(false, "")
                    specialListLiveData.postValue(updateUiState)
                }
        })
    }

    /**
     *  新闻列表
     * */

    fun getNewsList(isLoadMore: Boolean) {
        if (isLoadMore) {
            pageNo += 1
        }else{
            pageNo = 1
        }
        launch(block = {
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = pageNo
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getFindNews(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<NewsListMainBean>(it, true, isLoadMore, "")
                    newsListLiveData.value = updateUiState

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<NewsListMainBean>(false, "", isLoadMore)
                    newsListLiveData.value = updateUiState
                }
        })
    }

    fun followOrCancelUser(followId:String,type:Int){
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"]=type
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    followLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, it)
                    followLiveData.postValue(updateUiState)
                }
        })
    }

}