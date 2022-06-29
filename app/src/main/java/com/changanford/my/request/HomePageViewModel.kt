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


class HomePageViewModel : BaseViewModel() {

    //圈子列表
    val circlesListData=MutableLiveData<UpdateUiState<ListMainBean<NewCircleBean>>>()
    val  myTopicsLiveData=MutableLiveData<UpdateUiState<ListMainBean<Topic>>>()
    val  myLikedPostsLiveData= MutableLiveData<UpdateUiState<ListMainBean<PostDataBean>>>()

    fun getMyCircles(userId: String) {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] = 3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myCircles(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<ListMainBean<NewCircleBean>>(it, true, "")
                circlesListData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<ListMainBean<NewCircleBean>>( false, it)
                circlesListData.postValue(updateUiState)
            }
        }
    }
    fun getMyTopics(userId: String) {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] = 3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myTopics(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<ListMainBean<Topic>>(it, true, "")
                myTopicsLiveData.postValue(updateUiState)

            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<ListMainBean<Topic>>( true, it)
                myTopicsLiveData.postValue(updateUiState)
            }
        }
    }
    fun getMyLikedPosts(userId: String) {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] =3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["userId"] =userId.toLong()
                }
                val rKey = getRandomKey()
                apiService.myLikedPosts(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<ListMainBean<PostDataBean>>(it, true, "")
                myLikedPostsLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<ListMainBean<PostDataBean>>( false, it)
                myLikedPostsLiveData.postValue(updateUiState)
            }
        }
    }

//    fun  getAllHomePageData(userId: String){
//        viewModelScope.launch {
//            fetchRequest {
//                val paramMaps = HashMap<String, Any>()
//                paramMaps["pageNo"] = 1
//                paramMaps["pageSize"] = 3
//                paramMaps["queryParams"] = HashMap<String, Any>().also {
//                    it["userId"] =userId.toLong()
//                }
//                val rKey = getRandomKey()
//                var datas=apiService.myCircles(paramMaps.header(rKey), paramMaps.body(rKey)).data
//            }
//        }
//    }
}