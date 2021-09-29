package com.changanford.home.recommend.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.RecommendListBean
import com.changanford.common.net.*
import com.changanford.home.base.response.UpdateUiState
import kotlinx.coroutines.launch

class RecommendViewModel : ViewModel() {

    var recommendLiveData: MutableLiveData<UpdateUiState<RecommendListBean>> = MutableLiveData()

    var pageNo:Int=1
    fun getRecommend( isLoadMore: Boolean) {

        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = pageNo
                val rKey = getRandomKey()
                apiService.getRecommendList(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                pageNo+=1
                val updateUiState = UpdateUiState<RecommendListBean>(it, true, isLoadMore, "")
                recommendLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<RecommendListBean>(false,it ,isLoadMore)
                recommendLiveData.postValue(updateUiState)
            }
        }
    }


}