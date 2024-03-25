package com.changanford.common.util

import android.content.Context
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.widget.ReplyDialog

/**
 * @author: niubobo
 * @date: 2024/3/25
 * @description：
 */
object CommentUtils {

    var commentContent=""

    fun showReplyDialog(
        context: Context,
        bizId: String?,
        groupId: String?,
        pid: String?,
        type: Int,
        nickName: String,
        block: () -> Unit
    ) {
        if (MConstant.userId.isNullOrEmpty()){
            startARouter(ARouterMyPath.SignUI)//跳转登录
            return
        }
        ReplyDialog(context, object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                commentContent=content
                when (type) {
                    1 -> {
                        addNewsComment(bizId, groupId, pid, content, block)
                    }

                    2 -> {
                        addPostsComment(bizId, groupId, pid, content, block)
                    }
                }
            }
        }, hintText = "回复@$nickName").show()
    }

    private fun addNewsComment(
        bizId: String?,
        groupId: String?,
        pid: String?,
        content: String,
        block: () -> Unit
    ) {
        BaseApplication.curActivity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["bizId"] = bizId ?: ""
            body["pid"] = pid ?: ""
            body["groupId"] = groupId ?: ""
            body["content"] = content
            body["phoneModel"] = DeviceUtils.getDeviceModel()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .addCommentNews(body.header(rKey), body.body(rKey)).onSuccess {
                    block.invoke()
                }.onWithMsgFailure { it?.toast() }
        }
    }

    private fun addPostsComment(
        bizId: String?,
        groupId: String?,
        pid: String?,
        content: String,
        block: () -> Unit
    ) {
        BaseApplication.curActivity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["bizId"] = bizId ?: ""
            body["pid"] = pid ?: ""
            body["groupId"] = groupId ?: ""
            body["content"] = content
            body["phoneModel"] = DeviceUtils.getDeviceModel()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .addPostsComment(body.header(rKey), body.body(rKey)).onSuccess {
                    block.invoke()
                }.onWithMsgFailure { it?.toast() }
        }
    }
}