package com.changanford.my.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.home.PageConstant
import com.changanford.home.data.TwoAdData
import kotlinx.coroutines.launch


class OtherMedalViewModel : BaseViewModel() {

    //圈子列表
    val medalLiveData=MutableLiveData<UpdateUiState<ArrayList<MedalListBeanItem>>>()


    fun queryOtherUserMedal(userId: String) {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                body["userId"] = userId
                val rkey = getRandomKey()
                apiService.queryOtherUserMedal(body.header(rkey), body.body(rkey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<ArrayList<MedalListBeanItem>>(it, true,"")
                medalLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<ArrayList<MedalListBeanItem>>( false, it)
                medalLiveData.postValue(updateUiState)
            }
        }
    }


}