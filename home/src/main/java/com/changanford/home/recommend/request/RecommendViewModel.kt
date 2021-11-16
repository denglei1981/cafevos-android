package com.changanford.home.recommend.request

import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.RecommendListBean
import com.changanford.common.net.*
import com.changanford.common.util.SafeMutableLiveData
import com.changanford.common.utilext.toastShow
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import kotlinx.coroutines.launch

class RecommendViewModel : BaseViewModel() {

    var recommendLiveData: SafeMutableLiveData<UpdateUiState<RecommendListBean>> = SafeMutableLiveData()

    val recommendBannerLiveData : SafeMutableLiveData<UpdateUiState<List<AdBean>>> = SafeMutableLiveData()


    var pageNo: Int = 1
    fun getRecommend(isLoadMore: Boolean) {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                if (!isLoadMore) {
                    pageNo = 1
                }
                paramMaps["pageNo"] = pageNo
                val rKey = getRandomKey()
                apiService.getRecommendList(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<RecommendListBean>(it, true, isLoadMore, "")
                recommendLiveData.postValue(updateUiState)
                pageNo += 1
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<RecommendListBean>(false, it, isLoadMore)
                recommendLiveData.postValue(updateUiState)
            }
        }
    }

    fun getRecommendBanner() {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCode"] = "recommend_banner"
            ApiClient.createApi<HomeNetWork>()
                .getRecommendBanner(body.header(rkey), body.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<List<AdBean>>(it, true, "")
                    recommendBannerLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }
        })
    }

    /**
     * 点击活动统计
     */
    fun AddACTbrid(wonderfulId: Int) {
        launch(false, {
            var body = HashMap<String, Any>()
            body["wonderfulId"] = wonderfulId
            var rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .addactbrid(body.header(rkey), body.body(rkey))
                .onSuccess {
                }.onWithMsgFailure {
                }.onFailure {
                }

        })
    }


}