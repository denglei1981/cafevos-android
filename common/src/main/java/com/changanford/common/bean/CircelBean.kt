package com.changanford.common.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 *  文件名：CircelBean
 *  创建者: zcy
 *  创建日期：2021/9/26 19:37
 *  描述: TODO
 *  修改描述：TODO
 */
data class CircleListBean(
    val dataList: List<CircleItemBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class CircleMangerBean(
    val circles: List<CircleItemBean>?,
    val type: Int,
    val typeStr: String,
    val isAudit: String,
)


data class CircleItemBean(
    override var itemType: Int,
    val circleId: Int,
    val description: String,
    val userId: Int,
    val name: String,
    val nameColor: String?,
    val hotIcon: String,
    val isHot: String,//是否热门 1 是 0 不是
    val isRecommend: String,//是否推荐 1是 0 不是
    val pic: String,
    val checkStatus: String,//状态 1 审核通过 2 待审核  3认证失败
    val checkPassTime: String,
    val createTime: String,
    val userCount: Int,
    val postsCount: String,
    val applyerCount: Int, //申请人数
    val checkNoReason: String //审核不通过原因
) : MultiItemEntity


data class CircleMemberBean(
    val dataList: List<CircleMemberData>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class CircleMemberData(
    override var itemType: Int,
    val avatar: String,
    val circleId: String,
    val createTime: String,
    val memberIcon: String,
    val memberId: Int,
    val memberName: String,
    val nickname: String,
    val status: String,
    val userId: String,
    val name: String,
    val pic: String,
    val checkStatus: Int,
    val description: String,
    val postsCount: Int,
    val userCount: Int
) : MultiItemEntity


data class CircleTagBean(
    val refuse: List<Refuse>?
)

data class Refuse(
    val type: String
)

data class CircleUserBean(
    val userApplyCount: Int,
    val userCount: Int
)