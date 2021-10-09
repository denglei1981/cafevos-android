package com.changanford.home.shot.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.BigShotPostBean
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.bean.ListMainBean
import com.changanford.home.bean.NewsListMainBean


class BigShotListViewModel : BaseViewModel() {
    val bigShotsLiveData = MutableLiveData<UpdateUiState<List<BigShotRecommendBean>>>() // 推荐的大咖

    val bigShotPostLiveData = MutableLiveData<UpdateUiState<ListMainBean<BigShotPostBean>>>() //
    var pageNo: Int = 1

    /**
     *  推荐的大咖
     * */
    fun getRecommendList() {
        launch(false,{
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
        })
    }

    /**
     * 获取 大咖帖子列表
     * */
    fun getBigShotPost(isLoadMore: Boolean) {
        if (!isLoadMore) {
            pageNo = 1
        }
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = 1
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getBigShotPostsList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState =
                        UpdateUiState<ListMainBean<BigShotPostBean>>(it, true, isLoadMore, "")
                    bigShotPostLiveData.value = updateUiState
                    pageNo += 1
                }.onWithMsgFailure {
                    val updateUiState =
                        UpdateUiState<ListMainBean<BigShotPostBean>>(false, "", isLoadMore)
                    bigShotPostLiveData.value = updateUiState
                }
        })
    }

}