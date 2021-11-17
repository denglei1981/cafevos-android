package com.changanford.home.search.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean

class PolySearchUserViewModel : BaseViewModel() {


    val searchHistoryLiveData= MutableLiveData<UpdateUiState<ListMainBean<AuthorBaseVo>>>() // 搜索历史。

    val followLiveData = MutableLiveData<UpdateUiState<Any>>() // 关注否?。




    /**
     *   搜索具体内容
     * */
    var pageNo=1
    fun  getSearchContent(skwType:Int,skwKeyword:String,isLoadMore:Boolean){
        launch(false,{
            val requestBody = HashMap<String, Any>()
            if(isLoadMore){
                pageNo+=1
            }else{
                pageNo=1
            }
            requestBody["pageNo"]=pageNo
            requestBody["pageSize"]=PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            var hashMap = HashMap<String, Any>()
            hashMap["skwKeyword"]=skwKeyword
            hashMap["skwType"]=skwType
            requestBody["queryParams"]=hashMap
            val rkey = getRandomKey()

                    ApiClient.createApi<HomeNetWork>()
                        .searchS(requestBody.header(rkey), requestBody.body(rkey))
                        .onSuccess {
                            val updateUiState = UpdateUiState<ListMainBean<AuthorBaseVo>>(it, true, isLoadMore,"")
                            searchHistoryLiveData.postValue(updateUiState)

                        }.onWithMsgFailure {
                            val updateUiState = UpdateUiState<ListMainBean<AuthorBaseVo>>(false, it,isLoadMore)
                            searchHistoryLiveData.postValue(updateUiState)
                        }
        })
    }








}