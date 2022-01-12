package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.SectionEntity
import com.changanford.common.bean.NewCirceTagBean

/**
 * @Author: hpb
 * @Date: 2020/5/13
 * @Des:
 */
data class ChoseCircleBean(
    val checkStatus: Int,
    val circleId: String,
    val createTime: Any,
    val description: String,
    val hotIcon: String,
    var isApply: Int,
    val isHot: Int,
    val isRecommend: Int,
    val lastPostsTime: Any,
    val name: String,
    val nameColor: String,
    val pic: String,
    val postsCount: Int,
    val userCount: Int,
    val userId: Int,
    var isJoin:String?=null,//是否加入圈子 YES NO
    val tags:List<NewCirceTagBean>?,//标签
) : SectionEntity {
    override val isHeader: Boolean
        get() = false
}

data class ChoseCircleHeadBean(val title: String) : SectionEntity {
    override val isHeader: Boolean
        get() = true
}