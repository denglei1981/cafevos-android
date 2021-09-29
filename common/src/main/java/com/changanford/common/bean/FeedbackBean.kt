package com.changanford.common.bean

import java.io.Serializable

/**
 *  文件名：FeedbackBean
 *  创建者: zcy
 *  创建日期：2020/5/16 11:48
 *  描述: TODO
 *  修改描述：TODO
 */

class FeedbackTags : ArrayList<FeedbackTagsItem>()

data class FeedbackTagsItem(
    val tagId: Int,
    val tagName: String
)

data class FeedbackQBean(
    val dataList: List<FeedbackItem>?,
    val extend: ExtendQ,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class FeedbackItem(
    val questionContent: String,
    val questionName: String
)

class ExtendQ(
)


data class FeedbackMineListBean(
    val dataList: List<FeedbackMineListItem>?,
    val extend: ExtendM,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class FeedbackMineListItem(
    val feedbackContent: String,
    val feedbackTime: Long,
    val headImg: String,
    val imgs: String,
    var isReply: Int,
    val memberIcon: String,
    val replyContent: String,
    val replyTime: Long,
    val tagName: String,
    val userName: String,
    var isRead: String,
    val userFeedbackId: String
) : Serializable

class ExtendM(
)

data class FeedbackInfoList(
    val avatar: String,
    val feedbackStatus: Int,
    val item: List<FeedbackInfoItem>,
    val nickname: String,
    val userFeedbackId: Int,
    val closeReason: String //关闭状态
)

data class FeedbackInfoItem(
    val createTime: Long,
    val messageContent: String?,
    val messageImg: String,
    val messageType: Int
)