package com.changanford.home.search.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.bean.SearchShopBean

class PolySearchShopResultViewModel : BaseViewModel() {




    val followLiveData = MutableLiveData<UpdateUiState<Any>>() // 关注否?。

    val newsListLiveData = MutableLiveData<UpdateUiState<ListMainBean<SearchShopBean>>>() // 专题列表 轮播图。


    /**
     *   搜索具体内容
     * */
    var pageNo = 1
    fun getSearchContent(skwKeyword: String, isLoadMore: Boolean) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            if (isLoadMore) {
                pageNo += 1
            } else {
                pageNo = 1
            }
            requestBody["pageNo"] = pageNo
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            var hashMap = HashMap<String, Any>()
            hashMap["skwKeyword"] = skwKeyword
            hashMap["skwType"] = SearchTypeConstant.SEARCH_ACTION_SHOP
            requestBody["queryParams"] = hashMap
            val rkey = getRandomKey()

            ApiClient.createApi<HomeNetWork>()
                .getSearchShopList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState =
                        UpdateUiState<ListMainBean<SearchShopBean>>(it, true, isLoadMore, "")
                    newsListLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<ListMainBean<SearchShopBean>>(false, it)
                    newsListLiveData.postValue(updateUiState)
                }


        })
    }


    fun followOrCancelUser(followId: String, type: Int) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
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