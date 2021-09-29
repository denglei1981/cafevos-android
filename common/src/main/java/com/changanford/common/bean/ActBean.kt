package com.changanford.common.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 *  文件名：ActBean
 *  创建者: zcy
 *  创建日期：2021/9/29 9:44
 *  描述: TODO
 *  修改描述：TODO
 */
data class InfoBean(
    val dataList: List<InfoData>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class InfoData(
    val artId: Int,
    val authors: Authors?,
    val catId: Int,
    val collectCount: Int,
    var commentCount: Long,
    val content: Any,
    val createTime: Long,
    val isDeleted: Int,
    var isLike: Int,
    val isRecommend: Int,
    val isSpecialTopic: Int,
    val keyword: String,
    var likesCount: Long,
    val likesCountBase: Int,
    val likesCountMul: Double,
    val pics: String,
    val publishTime: Long,
    val shareCount: Int,
    val sortOrder: Int,
    val specialTopicId: Any,
    val specialTopicTitle: Any,
    val status: Int,
    val summary: String,
    val timeStr: String,
    val title: String,
    val type: Int,
    val updateTime: Any,
    val userId: Int,
    val videoTime: String,
    val videoUrl: String,
    val viewsCount: Long,
    val viewsCountBase: Int,
    val viewsCountMul: Double,
    val jumpVal: String,
    val jumpType: Int
)


data class Authors(
    val authorId: Int,
    val avatar: String,
    val isFollow: Int,
    val memberIcon: String,
    val memberId: Any,
    val memberName: Any,
    val nickname: String,
    val headFrameImage: String,
    val imags: ArrayList<LabelBean>? //用户图标列表
)

data class PostBean(
    val dataList: List<PostDataBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class PostDataBean(
    val authorBaseVo: Authors?,
    val circleId: Int,
    val collectCount: Int,
    var commentCount: Int,
    val content: String,
    val createTime: Long,
    val isCheck: Int,
    val isDeleted: Int,
    val isGood: Int,//是否加精 1：加精，2：不加精, 3: 加精审核中
    val isHot: Int,
    var isLike: Int,
    val isPrivate: Int,
    val isPublish: String,
    val isRecommend: Int,
    val isTop: Int,
    val keywords: String,
    var likesCount: Long,
    val likesCountBase: Int,
    val likesCountMul: Double,
    val picCount: Int,
    val pics: String,
    val plate: Int,
    val postsId: Int,
    val publishTime: Long,
    val shareCount: Int,
    val sortOrder: Int,
    val status: Int,
    val title: String,
    val topicId: Int,
    val type: Int,
    val updateTime: Long,
    val userId: Int,
    val videoTime: String,
    val videoUrl: String,
    val viewsCount: Int,
    val viewsCountBase: Int,
    val viewsCountMul: Double,
    val rejectReason: String,
    var itemViewHeight: Int,
    var itemViewWidth: Int
)

data class AcBean(var title: String, var iconUrl: String, var type: Int) : MultiItemEntity {
    override val itemType: Int
        get() = type
}

data class AccBean(
    val dataList: List<AcDataBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class AcDataBean(
    val activityJoinCount: Int,
    val activityTotalCount: Int,
    val beginTime: String,
    val cityName: String,
    val coverImg: String,
    val deadLineTime: Long,
    val endTime: Long,
    val jumpType: Int,
    val jumpVal: String,
    val official: Int,
    val provinceName: String,
    val reason: String,
    val status: Int,
    val title: String,
    val townName: String,
    val userId: Int,
    val wonderfulId: Int,
    val wonderfulType: Int,
    var serverTime: Long,
    val createTime: Long
)