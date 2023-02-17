package com.changanford.common.util.request

import androidx.lifecycle.LifecycleOwner
import com.changanford.common.basic.BaseApplication
import com.changanford.common.net.*
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toastShow
import com.luck.picture.lib.config.PictureSelectionConfig.listener

/**
 *Author lcw
 *Time on 2023/2/16
 *Purpose
 */
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

fun addRecord(id: String) {
    val activity = BaseApplication.curActivity
    activity?.launchWithCatch {
        val requestBody = HashMap<String, Any>()
        requestBody["id"] = id
        val rkey = getRandomKey()
        ApiClient.createApi<NetWorkApi>()
            .addRecord(requestBody.header(rkey), requestBody.body(rkey))
            .onSuccess {

            }
    }
}


interface GetRequestResult {
    fun success(data: Any)
}