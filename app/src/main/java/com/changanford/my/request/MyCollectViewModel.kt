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


class MyCollectViewModel : BaseViewModel() {

    //圈子列表
    val circlesListData=MutableLiveData<UpdateUiState<NewCircleDataBean>>()
    val  myTopicsLiveData=MutableLiveData<UpdateUiState<ListMainBean<Topic>>>()
    val  myLikedPostsLiveData= MutableLiveData<UpdateUiState<PostBean>>()

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
                val updateUiState = UpdateUiState<NewCircleDataBean>(it, true, "")
                circlesListData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<NewCircleDataBean>( false, it)
                circlesListData.postValue(updateUiState)
            }
        }
    }

    val  postBeanLiveData= MutableLiveData<UpdateUiState<PostBean>>()
    /**
     * 我收藏的帖子
     */
    fun queryMineCollectPost() {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] = 3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =""
                }
                val rKey = getRandomKey()
                apiService.queryMineCollectInfo(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<PostBean>(it, true, "")
                postBeanLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<PostBean>( false, it)
                postBeanLiveData.postValue(updateUiState)
            }
        }
    }
    val  accLiveData= MutableLiveData<UpdateUiState<AccBean>>()

    /**
     * 我收藏的活动
     */
    fun queryMineCollectAc() {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] = 3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =""
                }
                val rKey = getRandomKey()
                apiService.queryMineCollectAc(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<AccBean>(it, true, "")
                accLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<AccBean>( false, it)
                accLiveData.postValue(updateUiState)
            }
        }




    }
    val  shopBeanLiveData= MutableLiveData<UpdateUiState<ShopBean>>()
    /**
     * 我的收藏 商品
     */
    fun queryShopCollect() {
            viewModelScope.launch {
                fetchRequest {
                    val paramMaps = HashMap<String, Any>()
                    paramMaps["pageNo"] = 1
                    paramMaps["pageSize"] = 3
                    paramMaps["queryParams"] = HashMap<String, Any>().also {
                        it["searchKeys"] =""
                    }
                    val rKey = getRandomKey()
                    apiService.queryShopCollect(paramMaps.header(rKey), paramMaps.body(rKey))
                }.onSuccess { // 成功
                    val updateUiState = UpdateUiState<ShopBean>(it, true, "")
                    shopBeanLiveData.postValue(updateUiState)
                }.onWithMsgFailure { // 失败
                    val updateUiState = UpdateUiState<ShopBean>( false, it)
                    shopBeanLiveData.postValue(updateUiState)
                }
            }
        }


    /**
     * 我收藏的 资讯 1
     */

    val  infoBeanLiveData= MutableLiveData<UpdateUiState<InfoBean>>()
    fun queryMineCollectInfo() {
        viewModelScope.launch {
            fetchRequest {
                val paramMaps = HashMap<String, Any>()
                paramMaps["pageNo"] = 1
                paramMaps["pageSize"] = 3
                paramMaps["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =""
                }
                val rKey = getRandomKey()
                apiService.queryMineCollectList(paramMaps.header(rKey), paramMaps.body(rKey))
            }.onSuccess { // 成功
                val updateUiState = UpdateUiState<InfoBean>(it, true, "")
                infoBeanLiveData.postValue(updateUiState)
            }.onWithMsgFailure { // 失败
                val updateUiState = UpdateUiState<InfoBean>( false, it)
                infoBeanLiveData.postValue(updateUiState)
            }
        }
    }
}