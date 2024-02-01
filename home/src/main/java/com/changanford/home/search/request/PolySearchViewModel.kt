package com.changanford.home.search.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.HotPicBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.room.SearchRecordDatabase
import com.changanford.common.util.room.SearchRecordEntity
import com.changanford.common.utilext.createHashMap
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.bean.SearchShopBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * */
class PolySearchViewModel : BaseViewModel() {
    val searchKeyLiveData = MutableLiveData<UpdateUiState<List<SearchKeyBean>>>()

    val searchHistoryLiveData = MutableLiveData<UpdateUiState<List<SearchKeyBean>>>() // 搜索历史。

    val searchAutoLiveData = MutableLiveData<UpdateUiState<List<SearchKeyBean>>>() // 搜索关键词。

    val searchKolingLiveData = MutableLiveData<UpdateUiState<ListMainBean<SearchShopBean>>>()

    val hotTopicBean = MutableLiveData<HotPicBean>()

    /**
     *  获取搜索关键字
     * */
    fun getSearchKeyList() {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .searchHots(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(it, true, "")
                    searchKeyLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(false, "")
                    searchKeyLiveData.postValue(updateUiState)
                }
        })
    }

    /**
     *  搜索口令
     * */
    fun getSearchContent(skwKeyword: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = 1
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val hashMap = HashMap<String, Any>()
            hashMap["skwKeyword"] = skwKeyword
            requestBody["queryParams"] = hashMap
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSearchShopList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState =
                        UpdateUiState<ListMainBean<SearchShopBean>>(it, true, false, "")
                    searchKolingLiveData.postValue(updateUiState)
                }.onWithMsgFailure {

                }
        })
    }

    /**
     *   搜索历史
     * */
    fun getSearchHistoryList() {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .searchHots(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(it, true, "")
                    searchHistoryLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(false, "")
                    searchHistoryLiveData.postValue(updateUiState)
                }
        })
    }

    fun getSearchAc(keyword: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["keyword"] = keyword
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .searchAc(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(it, true, "")
                    searchAutoLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<List<SearchKeyBean>>(false, "")
                    searchAutoLiveData.postValue(updateUiState)
                }
        })
    }

    fun getTopic() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = 1
            body["pageSize"] = 6

            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getSugesstionTopics(body.header(rKey), body.body(rKey))
                .onSuccess {
                    hotTopicBean.value = it
                }
                .onFailure { }
        })
    }

    fun deleteRecord(context: Context, keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .delete(keyword)
        }
    }

    fun insertRecord(context: Context, keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .insert(SearchRecordEntity(keyword))
        }
    }

    fun clearRecord(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .clearAll()
        }
    }

}