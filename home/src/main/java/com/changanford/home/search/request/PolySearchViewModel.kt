package com.changanford.home.search.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.bean.SearchShopBean
import com.changanford.home.room.SearchRecordDatabase
import com.changanford.home.room.SearchRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * */
class PolySearchViewModel : BaseViewModel() {
    val searchKeyLiveData = MutableLiveData<UpdateUiState<List<SearchKeyBean>>>()

    val searchHistoryLiveData= MutableLiveData<UpdateUiState<List<SearchKeyBean>>>() // 搜索历史。

    val searchAutoLiveData =MutableLiveData<UpdateUiState<List<SearchKeyBean>>>() // 搜索关键词。

    val searchKolingLiveData =MutableLiveData<UpdateUiState<ListMainBean<SearchShopBean>>>()

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
    fun  getSearchContent(skwKeyword:String){
        launch(false,{
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"]=1
            requestBody["pageSize"]= PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val hashMap = HashMap<String, Any>()
            hashMap["skwKeyword"]=skwKeyword
            requestBody["queryParams"]=hashMap
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
    fun  getSearchHistoryList(){
        launch(false,{
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

    fun getSearchAc(keyword: String){
        launch(false,{
            val requestBody = HashMap<String, Any>()
            requestBody["keyword"]=keyword
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
    fun deleteRecord(context:Context,keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .delete(keyword)
        }
    }
    fun insertRecord(context:Context,keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .insert(SearchRecordEntity(keyword))
        }
    }

    fun clearRecord(context:Context) {
        viewModelScope.launch(Dispatchers.IO) {
            SearchRecordDatabase.getInstance(context).getSearchRecordDao()
                .clearAll()
        }
    }


}