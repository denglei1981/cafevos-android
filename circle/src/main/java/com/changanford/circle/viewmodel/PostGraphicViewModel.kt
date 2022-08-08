package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.baidu.mapapi.search.core.BusInfo
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

class PostGraphicViewModel : BaseViewModel() {

    val postDetailsBean = MutableLiveData<PostsDetailBean>()
    val likePostsBean = MutableLiveData<CommonResponse<Any>>()
    val collectionPostsBean = MutableLiveData<CommonResponse<Any>>()
    val commentLikeBean = MutableLiveData<CommonResponse<Any>>()
    val followBean = MutableLiveData<CommonResponse<Any>>()
    val addCommendBean = MutableLiveData<CommonResponse<Any>>()
    val commendBean = MutableLiveData<HomeDataListBean<CommentListBean>>()

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }

    fun getData(postsId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = postsId
            val rKey = getRandomKey()

            ApiClient.createApi<CircleNetWork>()
                .getPostsDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    postDetailsBean.value = it
                }.onWithMsgFailure {
                    it?.toast()
                    LiveDataBus.get().with(CircleLiveBusKey.CLOSE_POST_DETAILS).postValue(false)
                }
        }, error = {
            it.message?.toast()
        })
    }

    fun getCommendList(bizId: String, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["bizId"] = bizId
                it["type"] = "2"
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getCommentList(body.header(rKey), body.body(rKey))
                .onSuccess {
                    commendBean.value = it
                }
                .onFailure { }
        })
    }

    fun likePosts(postsId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = postsId

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .actionLike(body.header(rKey), body.body(rKey)).also {
                    likePostsBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }

    fun collectionApi(postsId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["collectionContentId"] = postsId
            body["collectionType"] = 2
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .collectionApi(body.header(rKey), body.body(rKey)).also {
                    collectionPostsBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }

    fun commentLike(commentId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["commentId"] = commentId
            body["type"] = 2
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .commentLike(body.header(rKey), body.body(rKey)).also {
                    commentLikeBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }

    fun userFollowOrCancelFollow(followId: String, type: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["followId"] = followId
            // 1 关注 2 取消关注
            body["type"] = type
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .userFollowOrCancelFollow(body.header(rKey), body.body(rKey)).also {
                    followBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }

    fun addPostsComment(
        bizId: String?,
        groupId: String?,
        pid: String?,
        content: String
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["bizId"] = bizId ?: ""
            body["pid"] = pid ?: ""
            body["groupId"] = groupId ?: ""
            body["content"] = content
            body["phoneModel"] = DeviceUtils.getDeviceModel()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .addPostsComment(body.header(rKey), body.body(rKey)).also {
                    addCommendBean.value = it
                }

        }, error = {
            it.message?.toast()
        })
    }

    fun addPostsCommentOut(
        bizId: String?,
        groupId: String?,
        pid: String?,
        content: String
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["bizId"] = bizId ?: ""
            body["pid"] = pid ?: ""
            body["groupId"] = groupId ?: ""
            body["content"] = content
            body["phoneModel"] = DeviceUtils.getDeviceModel()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .addPostsComment(body.header(rKey), body.body(rKey)).also {
                   it.msg.toast()
                    LiveDataBus.get().with(LiveDataBusKey.REFRESH_COMMENT_CIRCLE).postValue(false)
                }

        }, error = {
            it.message?.toast()
        })
    }
}