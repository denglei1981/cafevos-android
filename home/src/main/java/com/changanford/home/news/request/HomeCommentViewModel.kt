package com.changanford.home.news.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CommentListBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.utilext.createHashMap
import com.changanford.home.PageConstant
import com.changanford.home.api.HomeNetWork
import com.changanford.home.base.response.UpdateUiState
import com.changanford.home.bean.ListMainBean
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.NewsExpandData

/**
 *  资讯详情viewmodel
 * */
class HomeCommentViewModel : BaseViewModel() {
    val newsDetailLiveData = MutableLiveData<UpdateUiState<NewsDetailData>>() // 详情

    val commentsLiveData = MutableLiveData<UpdateUiState<ListMainBean<com.changanford.circle.bean.CommentListBean>>>() //

    val commentSateLiveData = MutableLiveData<UpdateUiState<Any>>() // 评论状态。

    val actionLikeLiveData = MutableLiveData<UpdateUiState<Any>>() // 评论状态。

    val followLiveData = MutableLiveData<UpdateUiState<Any>>() // 关注否?。


    val recommendNewsLiveData = MutableLiveData<UpdateUiState<NewsExpandData>>() //  推荐的 新闻


    var pageNo: Int = 1

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
                    val updateUiState = UpdateUiState<NewsDetailData>(false, it)
                    newsDetailLiveData.postValue(updateUiState)
                }
        })
    }

    /**
     *  获取资讯评论
     * */
    fun getNewsCommentList(bizId: String, isLoadMore: Boolean) {
        if (isLoadMore) {
            pageNo += 1
        } else {
            pageNo = 1
        }
        launch(false, block = {
            val requestBody = HashMap<String, Any>()
            requestBody["queryParams"] = HashMap<String, Any>().also {
                it["bizId"] = bizId
                it["type"] = "1"
            }
            requestBody["pageNo"] = pageNo
            requestBody["pageSize"] = PageConstant.DEFAULT_PAGE_SIZE_THIRTY
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getCommentList(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState =
                        UpdateUiState<ListMainBean<com.changanford.circle.bean.CommentListBean>>(it, true, isLoadMore, "")
                    commentsLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState =
                        UpdateUiState<ListMainBean<com.changanford.circle.bean.CommentListBean>>(false, it, isLoadMore)
                    commentsLiveData.postValue(updateUiState)
                }
        })
    }

    val childCommentListBean = MutableLiveData<ArrayList<CommentListBean>>()

    fun getChildCommentData(bizId: String, groupId: String, type: String) {
        launch(true,block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = "1"
            body["pageSize"] = "3"
            body["queryParams"] = HashMap<String, Any>().also {
                it["bizId"] = bizId
                it["groupId"] = groupId
                it["type"] = type
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getChildCommentList(body.header(rKey), body.body(rKey)).also {
                    if (it.data?.dataList?.size!! > 0) {
                        it.data?.dataList?.removeAt(0)
                        childCommentListBean.value = it.data?.dataList
                    }

                }

        })
    }

    fun addNewsComment(bizId: String, content: String, pid: String = "", phoneModel: String = "") {
        launch(true, {
            val requestBody = HashMap<String, Any>()
            requestBody["bizId"] = bizId
            requestBody["content"] = content
            if (!TextUtils.isEmpty(phoneModel)) {
                requestBody["phoneModel"] = phoneModel
            }
            if (!TextUtils.isEmpty(pid)) {
                requestBody["pid"] = pid
            }
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .addCommentNews(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    commentSateLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, it)
                    commentSateLiveData.postValue(updateUiState)
                }
        })
    }

    fun actionLike(artId: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionLike(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    actionLikeLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, it)
                    actionLikeLiveData.postValue(updateUiState)
                }
        })
    }

    fun followOrCancelUser(followId: String, type: Int) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    followLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, it)
                    followLiveData.postValue(updateUiState)
                }
        })
    }
    val collectLiveData = MutableLiveData<UpdateUiState<Any>>() // 关注否?。
    fun addCollect(collId: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["collectionContentId"] = collId
            requestBody["collectionType"] = 1
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .collectionApi(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<Any>(it, true, "")
                    collectLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                    val updateUiState = UpdateUiState<Any>(false, it)
                    collectLiveData.postValue(updateUiState)
                }
        })
    }


    fun getArtAdditional(artId: String) {
        launch(false, {
            val requestBody = HashMap<String, Any>()
            requestBody["artId"] = artId
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getArtAdditional(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    val updateUiState = UpdateUiState<NewsExpandData>(it, true, "")
                    recommendNewsLiveData.postValue(updateUiState)
                }.onWithMsgFailure {
                }
        })
    }


}