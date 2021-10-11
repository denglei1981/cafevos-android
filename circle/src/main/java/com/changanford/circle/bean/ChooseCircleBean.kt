package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.MultiItemEntity


data class ChooseCircleBean(
    var dataList: List<ChooseCircleData> = listOf(),
    var extend: Extend = Extend(),
    var pageNo: Int = 0,
    var pageSize: Int = 0,
    var total: Int = 0,
    var totalPage: Int = 0,
    var title:String
)

data class ChooseCircleData(
    var circleId: Int = 0,
    var createTime: Long = 0,
    var description: String = "",
    var hotIcon: Any? = null,
    var isHot: Int = 0,
    var isRecommend: Int = 0,
    var lastPostsTime: Long = 0,
    var maxUserCount: Int = 0,
    var name: String = "",
    var nameColor: Any? = null,
    var pic: String = "",
    var postsCount: Int = 0,
    var updateTime: Long = 0,
    var userCount: Int = 0,
    var userId: Int = 0,
    private var ItemType:Int =2,
    var title:String
): MultiItemEntity {
    override var itemType: Int = 2
        get() = ItemType
}

class Extend
