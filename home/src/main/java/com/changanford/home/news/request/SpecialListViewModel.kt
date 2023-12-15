package com.changanford.home.news.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.SpecialListMainBean

/**
 *  专题列表
 * */
class SpecialListViewModel : BaseViewModel() {
    val specialListLiveData = MutableLiveData<UpdateUiState<SpecialListMainBean>>() // 专题列表 轮播图。


    var pageNo: Int = 1

    /**
     *  专题列表顶部
     * */
    fun getSpecialList(isLoadMore: Boolean) {
        if (!isLoadMore) {
            pageNo = 1
        }
        launch(false,{
            val requestBody = HashMap<String, Any>()
            requestBody["pageNo"] = pageNo
            requestBody["pageSize"] =  PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getSpecialList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<SpecialListMainBean>(it, true, isLoadMore,"")
                    specialListLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<SpecialListMainBean>(false, "",isLoadMore)
                    specialListLiveData.postValue(updateUiState)
                }
        })
    }



}