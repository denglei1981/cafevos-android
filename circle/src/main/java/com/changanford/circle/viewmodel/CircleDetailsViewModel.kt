package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.*
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.PostBean
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleDetailsViewModel : BaseViewModel() {

    val tabList = arrayListOf("推荐", "最新", "精华", "圈主专区")
    val circleType = arrayListOf("4", "2", "3", "5")

    val circleBean = MutableLiveData<PostBean>()

    val recommondBean = MutableLiveData<PostBean>()

    val listBean = MutableLiveData<PostBean>()

    val joinBean = MutableLiveData<CommonResponse<ChoseCircleBean>>()

    val circleDetailsBean = MutableLiveData<CircleDetailBean>()

    val circleRolesBean = MutableLiveData<ArrayList<CircleStarRoleDto>>()

    val applyBean = MutableLiveData<CommonResponse<GetApplyManageBean>>()

    fun getData(viewType: Int, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleBean.value = it
                }
                .onFailure { }
        })
    }


    fun getRecommendPostData(viewType: Int, page: Int) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
                it["type"] = viewType
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getRecommendPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    recommondBean.value = it
                    LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
                }
                .onFailure {
                    LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
                }
        })
    }


    fun getListData(
        viewType: Int,
        topicId: String,
        circleId: String,
        page: Int,
        userId: String? = null
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20

            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
                if (topicId.isNotEmpty()) {
                    it["topicId"] = topicId
                }
                if (circleId.isNotEmpty()) {
                    it["circleId"] = circleId
                }
                if (viewType == 5) {
                    userId?.let { _ ->
                        it["userId"] = userId
                    }
                }
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    listBean.value = it
                }
                .onFailure { }

        })
    }

    fun getCircleDetails(circleId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().queryCircle(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleDetailsBean.value = it
                }
                .onFailure { }
        })
    }

    /**
     *申请加入圈子
     * */
    fun joinCircle(circleId: String, listener: OnPerformListener? = null) {
        if (MConstant.token.isEmpty()) {
            startARouter(ARouterMyPath.SignUI)
            return
        }
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().joinCircle(body.header(rKey), body.body(rKey))
                .also {
                    joinBean.value = it
                    if (it.code == 0) listener?.onFinish((it.data?.isApply) ?: 1)
                    else it.msg.toast()
                }

        }, error = {
            it.message.toString().toast()
        })
    }

    fun getCircleRoles(circleId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCircleRoles(body.header(rKey), body.body(rKey))
                .onSuccess {
                    circleRolesBean.value = it?.circleStarRoleDtos
                }
        }, error = {
            it.message.toString().toast()
        })
    }

    fun applyManagerInfo(circleId: String, circleStarRoleId: String) {
        launch(true, block = {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId
            body["circleStarRoleId"] = circleStarRoleId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .applyManagerInfo(body.header(rKey), body.body(rKey)).also {
                    applyBean.value = it
                }
        })

    }

    val topicBean = MutableLiveData<CircleMainBean>()

    fun communityTopic() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .communityTopic(body.header(rKey), body.body(rKey)).onSuccess {
                    topicBean.value = it
                }

        }, error = {
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
            it.message?.toast()
        })
    }

    val circleAdBean = MutableLiveData<List<AdBean>>()

    fun getRecommendTopic() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["posCode"] = "community_recommend"
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getRecommendTopic(body.header(rKey), body.body(rKey)).onSuccess {
                    circleAdBean.postValue(it)
                }

        }, error = {

        })
    }
}