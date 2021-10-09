package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2021/9/28
 *Purpose
 */
data class CircleDetailBean(
    val circleId: Int = 0,
    val createTime: Long = 0,
    val description: String = "",
    val hotIcon: Any? = Any(),
    var isApply: Int = 0,
    val isOwner: Int = 0,
    val name: String = "",
    val nameColor: Any? = Any(),
    val pic: String = "",
    val postsCount: Int = 0,
    val shareBeanVO: CircleShareBean? ,
    val userCount: Int = 0,
    val userId: Int = 0,
    val users: ArrayList<User> = arrayListOf()
)

data class ShareBeanVO(
    val bizId: Int = 0,
    val shareDesc: String = "",
    val shareImg: String = "",
    val shareTitle: String = "",
    val shareUrl: String = "",
    val type: Int = 0,
    val wxminiprogramCode: Any? = Any()
)

data class User(
    val avatar: String = "",
    val isFollow: Int = 0,
    val memberIcon: Any? = Any(),
    val memberId: Any? = Any(),
    val memberName: Any? = Any(),
    val nickname: String = "",
    val userId: Int = 0
)