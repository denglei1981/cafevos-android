package com.changanford.home.news.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.CommentListBean
import com.changanford.home.bean.ListMainBean
import com.changanford.home.news.data.NewsDetailData

/**
 *  资讯详情viewmodel
 * */
class NewsDetailViewModel : BaseViewModel() {
    val newsDetailLiveData = MutableLiveData<UpdateUiState<NewsDetailData>>() // 详情

    val commentsLiveData = MutableLiveData<UpdateUiState<ListMainBean<CommentListBean>>>() //

    val commentSateLiveData = MutableLiveData<UpdateUiState<Any>>() // 评论状态。


    var pageNo:Int =1
    /**
     *  资讯详情。
     * */
    fun getNewsDetail(artId: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getArticleDetails(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<NewsDetailData>(it, true, "")
                    newsDetailLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<NewsDetailData>(false, "")
                    newsDetailLiveData.postValue(updateUiState)
                }
        })
    }
    /**
     *  获取资讯评论
     * */
    fun getNewsCommentList(bizId: String,isLoadMore:Boolean) {
        if(!isLoadMore){
            pageNo=1
        }else{
            pageNo += 1
        }
        launch(false, block = {
            val requestBody = HashMap<String, Any>()



            requestBody["bizId"] = bizId
            requestBody["type"] = 1//类型 1 资讯 2 帖子


            requestBody["queryParams"]="{}"



            requestBody["pageNo"]=pageNo
            requestBody["pageSize"]=PageConstant.DEFAULT_PAGE_SIZE_THIRTY


            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getCommentList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<ListMainBean<CommentListBean>>(it, true, isLoadMore,"")
                    commentsLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<ListMainBean<CommentListBean>>(false, "",isLoadMore)
                    commentsLiveData.postValue(updateUiState)
                }

        })
    }
    fun addNewsComment(bizId:String,content:String,pid:String="",phoneModel:String=""){
        launch(true, {
            val requestBody = HashMap<String, Any>()
            requestBody["bizId"] = bizId
            requestBody["content"]=content
            if(!TextUtils.isEmpty(phoneModel)){
                requestBody["phoneModel"]=phoneModel
            }
            if(!TextUtils.isEmpty(pid)){
                requestBody["pid"]=pid
            }
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .addCommentNews(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    commentSateLiveData.postValue(updateUiState)

                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, "")
                    commentSateLiveData.postValue(updateUiState)
                }
        })
    }


}