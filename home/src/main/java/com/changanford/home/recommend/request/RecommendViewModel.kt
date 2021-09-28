package com.changanford.home.recommend.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.RecommendListBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class RecommendViewModel : ViewModel() {

    var  recommendLiveData: MutableLiveData<RecommendListBean> = MutableLiveData()


    fun getRecommend(pageNo:Int,pageSize:Int, isLoadMore: Boolean ){
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = pageNo
                val rKey = getRandomKey()
                apiService.getRecommendList(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                recommendLiveData.postValue(it)
            }.onFailure { // 失败

            }
        }
    }



}