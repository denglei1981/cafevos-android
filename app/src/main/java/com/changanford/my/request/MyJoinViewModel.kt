package com.changanford.my.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.ListMainBean
import com.changanford.common.bean.NewCircleDataBean
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.Topic
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.home.PageConstant
import com.changanford.home.data.TwoAdData
import kotlinx.coroutines.launch


class MyJoinViewModel : BaseViewModel() {

    //圈子列表
    val circlesListData=MutableLiveData<UpdateUiState<NewCircleDataBean>>()
    val  myTopicsLiveData=MutableLiveData<UpdateUiState<ListMainBean<Topic>>>()
    val  myLikedPostsLiveData= MutableLiveData<UpdateUiState<PostBean>>()
    var pageNo: Int = 1
    fun getMyCircles(userId: String,isLoadMore: Boolean) {
        if (isLoadMore) {
            pageNo += 1
        }else{
            pageNo = 1
        }
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = pageNo
                paramMaps["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myCircles(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<NewCircleDataBean>(it, true, isLoadMore,"")
                circlesListData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<NewCircleDataBean>( false, it,isLoadMore)
                circlesListData.postValue(updateUiState)
            }
        }
    }
    fun getMyTopics(userId: String,isLoadMore: Boolean) {
        if (isLoadMore) {
            pageNo += 1
        }else{
            pageNo = 1
        }
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = pageNo
                paramMaps["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myTopics(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<ListMainBean<Topic>>(it, true, isLoadMore,"")
                myTopicsLiveData.postValue(updateUiState)

            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<ListMainBean<Topic>>( true, it,isLoadMore)
                myTopicsLiveData.postValue(updateUiState)
            }
        }
    }
    fun getMyLikedPosts(userId: String,isLoadMore: Boolean) {
        if (isLoadMore) {
            pageNo += 1
        }else{
            pageNo = 1
        }
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = pageNo
                paramMaps["pageSize"] =PageConstant.DEFAULT_PAGE_SIZE_THIRTY
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myLikedPosts(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<PostBean>(it, true, isLoadMore,"")
                myLikedPostsLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<PostBean>( false, it,isLoadMore)
                myLikedPostsLiveData.postValue(updateUiState)
            }
        }
    }
}