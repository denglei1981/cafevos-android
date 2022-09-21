package com.changanford.common.bean

/**
 *  文件名：MessageBean
 *  创建者: zcy
 *  创建日期：2020/5/22 19:55
 *  描述: TODO
 *  修改描述：TODO
 */

data class MessageBean(
    val iconId: Int,
    val title: String,
    val des: String,
    val messageStatus: Int = 0,
    val messageNum:Int = 0
)

//消息类型 1 系统消息， 2 互动消息，3 交易消息
data class MessageQueryParams(val messageType: Int)

data class MessageStatusBean(
    val hudongStatus: Int,
    val hudongTitle: Any,
    val systemMessageStatus: Int,
    val systemMessageTitle: Any,
    val tradeStatus: Int,
    val tradeTitle: Any,
    //新增
    val unReadSystemMessageNum:Int,//0,
    val unReadHudongNum:Int,//0,
    val unReadTradeNum:Int,//0
)


data class MessageListBean(
    val dataList: List<MessageItemData>?,
    val extend: MessageExtend,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class MessageItemData(
    val jumpDataType: Int,
    val jumpDataValue: String,
    val messageContent: String,
    val messageTitle: String,
    val sendTime: Long,
    var status: Int,//消息状态 0 未读取， 1 已读取
    val userMessageId: Int
)

class MessageExtend(
)