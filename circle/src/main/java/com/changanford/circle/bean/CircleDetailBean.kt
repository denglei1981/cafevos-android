package com.changanford.circle.bean

import com.changanford.common.bean.AdBean
import com.changanford.common.bean.CircleShareBean
import com.changanford.common.bean.NewCirceTagBean

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
    val addrDesc: String = "",
    val nameColor: Any? = Any(),
    val permissions: ArrayList<CirclePermissionsData>? = ArrayList(),
    val pic: String = "",
    val bgImg: String? = null,
    val postsCount: Int = 0,
    //仅车主可加圈0是 1否
    val onlyAuthJoin: Int = 0,
    //圈子认证 0 未认证 1已认证
    val manualAuth: Int = 0,
    //圈子认证V标
    val manualAuthImg: String? = null,
    val isViewApplyMan: Int = 0,
    val shareBeanVO: CircleShareBean?,
    val ads: ArrayList<AdBean>,
    val userCount: Int = 0,
    val userId: Int = 0,
    val wonderfulControls: ArrayList<WonderfulControlsBean>?,
    val circleTopics: ArrayList<CircleTopicsBean>?,
    val circleNotices: ArrayList<CircleNoticesBean>?,
    val users: ArrayList<User> = arrayListOf(),
    val tags: ArrayList<NewCirceTagBean>? = null,
)

data class CircleTopicsBean(
    val topicId: String,
    val name: String,
    val pic: String,
    val description: String,
)

data class WonderfulControlsBean(
    val title: String,
    val coverImg: String,
    val jumpDto: JumpDto,
    val jumpType: Int,
    val jumpValue: String,
    val wonderfulId: String,
    val wonderfulType: String,
    val circleId: String,
    val beginTime: Long,
    val endTime: Long,
    val activityTag: String,
)

data class CircleNoticesBean(
    val top: String,
    val noticeId: String,
    val noticeName: String,
    val detailHtml: String
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


data class CirclePermissionsData(
    val createBy: String = "",
    val createTime: String = "",
    val cssClass: String? = "",
    val default: Boolean = false,
    val dictCode: Int = 0,
    val dictLabel: String = "",
    val dictSort: Int = 0,
    val dictType: String = "",
    val dictValue: String = "",
    val isDefault: String = "",
    val listClass: String? = "",
    val params: Params = Params(),
    val remark: String = "",
    val searchValue: Any? = Any(),
    val status: String = "",
    val updateBy: Any? = Any(),
    val updateTime: Any? = Any()
)
