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
    /**
     * 我收藏的帖子
     */
    fun queryMineCollectPost(pageNo: Int, searchKeys:String,result: (CommonResponse<PostBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =searchKeys
                }
                var rkey = getRandomKey()
                apiService.queryMineCollectInfo(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我收藏的活动
     */
    fun queryMineCollectAc(pageNo: Int,searchKeys:String, result: (CommonResponse<AccBean>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =searchKeys

                }
                var rkey = getRandomKey()
                apiService.queryMineCollectAc(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 我的收藏 商品
     */
    fun queryShopCollect(pageNo: Int, searchKeys:String,result: (CommonResponse<ShopBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = HashMap<String, Any>().also {
                    it["searchKeys"] =searchKeys

                }
                var rkey = getRandomKey()
                apiService.queryShopCollect(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 我收藏的 资讯 1
     */
    fun queryMineCollectInfo(pageNo: Int,searchKeys:String, result: (CommonResponse<InfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = HashMap<String, Any>().also {

                    it["searchKeys"] =searchKeys

                }
                var rkey = getRandomKey()
                apiService.queryMineCollectList(body.header(rkey), body.body(rkey))
            })
        }
    }
}