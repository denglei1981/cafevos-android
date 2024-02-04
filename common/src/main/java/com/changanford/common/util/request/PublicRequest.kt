package com.changanford.common.util.request

import androidx.lifecycle.LifecycleOwner
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.BizCodeBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.MConstant
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toastShow

/**
 *Author lcw
 *Time on 2023/2/16
 *Purpose
 */
//关注
fun followOrCancelFollow(
    lifecycleOwner: LifecycleOwner,
    followId: String,
    type: Int,
    block: () -> Unit
) {
    lifecycleOwner.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["followId"] = followId
        requestBody["type"] = type
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .cancelFans(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {
                if (type == 1) {
                    toastShow("已关注")
                } else {
                    toastShow("取消关注")
                }
                block.invoke()
            }.onWithMsgFailure {
                if (it != null) {
                    toastShow(it)
                }
            }
    }
}

//获取协议id
fun getBizCode(lifecycleOwner: LifecycleOwner, bizCodes: String, listener: GetRequestResult) {
    lifecycleOwner.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["bizCodes"] = bizCodes
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .bizCode(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {
                it?.ids?.let { it1 -> listener.success(it1) }
            }
    }
}

//获取协议id
fun getUpdateAgree(lifecycleOwner: LifecycleOwner, listener: GetUpdateAgreeResult) {
    val ids = MConstant.agreementPrivacy + "," + MConstant.agreementRegister
    lifecycleOwner.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["bizCodes"] = ids
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .bizCode(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {
                it?.let {
                    listener.success(it)
                }
            }
    }
}

//协议记录提交
fun addRecord(id: String) {
    val activity = BaseApplication.curActivity
    activity?.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["ids"] = id
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .addRecord(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {

            }
    }
}

//资讯点赞
fun actionLike(lifecycleOwner: LifecycleOwner, artId: String, block: () -> Unit) {
    lifecycleOwner.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["artId"] = artId
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .actionLike(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {
                block.invoke()
            }.onWithMsgFailure {
                it?.let { it1 -> toastShow(it1) }
            }
    }
}

//帖子点赞
fun actionLickPost(lifecycleOwner: LifecycleOwner, postId: String, block: () -> Unit){
    lifecycleOwner.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["postsId"] = postId
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .actionLikePost(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {
                block.invoke()
            }.onWithMsgFailure {
                it?.let { it1 -> toastShow(it1) }
            }
    }
}

interface GetRequestResult {
    fun success(data: Any)
}

interface GetUpdateAgreeResult {
    fun success(result: BizCodeBean)
}