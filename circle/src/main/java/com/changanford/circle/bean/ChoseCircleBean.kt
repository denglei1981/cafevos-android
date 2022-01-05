package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.SectionEntity

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
    val tags:List<String>?,//标签
) : SectionEntity {
    override val isHeader: Boolean
        get() = false
}

data class ChoseCircleHeadBean(val title: String) : SectionEntity {
    override val isHeader: Boolean
        get() = true
}