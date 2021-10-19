package com.changanford.common.bean

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/20 09:32
 * @Description: 　分享实体，现在简单分享连接
 * *********************************************************************************
 */
class ShareBean(
    val targetUrl: String,
    val imageUrl: String,
    val title: String,
    val content: String,
    val type: String,//业务类型 1 资讯 2 帖子 3 活动 4 用户  5 专题 6 商品  7 圈子 8 话题
    val bizId: String,//业务ID
    val isimg:String = "0"//是否为分享纯图片（1 是 0 否）
)

data class TaskShareBean(
    val bizId: String,
    val shareDesc: String,
    val shareImg: String,
    val shareTitle: String,
    val shareUrl: String,
    val type: String,
    val wxminiprogramCode: String
)