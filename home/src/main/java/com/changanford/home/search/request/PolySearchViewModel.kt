package com.changanford.home.search.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.SearchKeyBean

/**
 *  专题详情
 * */
class PolySearchViewModel : BaseViewModel() {
    val searchKeyLiveData = MutableLiveData<UpdateUiState<List<SearchKeyBean>>>() // 专题详情

    /**
     *
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


}