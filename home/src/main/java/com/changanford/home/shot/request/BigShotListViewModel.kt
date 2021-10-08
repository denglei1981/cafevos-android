package com.changanford.home.shot.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.bean.NewsListMainBean


class BigShotListViewModel : BaseViewModel() {
    val bigShotsLiveData = MutableLiveData<UpdateUiState<List<BigShotRecommendBean>>>() // 推荐的大咖

    val newsListLiveData = MutableLiveData<UpdateUiState<NewsListMainBean>>() // 专题列表 轮播图。
    var pageNo: Int = 1

    /**
     *  推荐的大咖
     * */
    fun getRecommendList() {
        launch {
            val requestBody = HashMap<String, Any>()
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getRecommendBigShot(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<BigShotRecommendBean>>(it, true, "")
                    bigShotsLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<List<BigShotRecommendBean>>(false, "")
                    bigShotsLiveData.postValue(updateUiState)
                }
        }
    }

    /**
     *  新闻列表
     * */

    fun getNewsList(isLoadMore: Boolean) {
        if (!isLoadMore) {
            pageNo = 1
        }
        launch {
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = 1
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getFindNews(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<NewsListMainBean>(it, true, isLoadMore, "")
                    newsListLiveData.value = updateUiState
                    pageNo += 1
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<NewsListMainBean>(false, "", isLoadMore)
                    newsListLiveData.value = updateUiState
                }
        }
    }

}