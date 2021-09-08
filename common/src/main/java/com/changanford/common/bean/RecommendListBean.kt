package com.changanford.common.bean

import java.util.*

/**
 * Created by Kevin on 2018/8/7.
 * 推荐列表
 */
class RecommendListBean(
    val dataList: ArrayList<RecommendData>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class HomeExtendBean(
    val viewType: String?,
    val isStarRole: String,
    val isCircler: String,//是否圈主
    val jumpDataType: Int,
    val jumpDataValue: String
)
data class RecommendData(
    val artCollectCount: Double,
    var artCommentCount: Int,
    val artContent: String,
    val artCreateTime: Any,
    val artId: String,
    val artIsSpecialTopic: Int,
    val artKeyword: Any,
    var artLikesCount: Int,
    val artPics: String,
    val artPublishTime: Long,
    val artShareCount: Double,
    val artSpecialTopicId: String,
    val artSpecialTopicTitle: String,
    val specialTopics: ArrayList<SpecialListBean>?,
    val artSummary: String,
    val artTitle: String,
    val artType: Int,
    val artVideoTime: String,
    val artVideoUrl: Any,
    val artViewsCount: Int,
    val artViewsCountBase: Double,
    val artViewsCount_mul: Double,
    var isLike: Int,
    val postsCircleId: Double,
    val postsCollectCount: Double,
    var postsCommentCount: Int,
    val postsContent: String,
    val postsCreateTime: String,
    val postsId: String,
    val postsIsCheck: Double,
    val postsIsGood: Double,
    val postsIsHot: Double,
    val postsIsPrivate: Double,
    val postsIsPublish: Double,
    val postsIsTop: Double,
    val postsKeywords: Any,
    var postsLikesCount: Int,
    val postsPics: String,
    val postsPlate: Double,
    val postsPublishTime: Long,
    val postsShareCount: Double,
    val postsTitle: String,
    val postsTopicId: Double,
    val postsType: Int,
    val postsVideoTime: String,
    val postsVideoUrl: Any,
    val postsViewsCount: Int,
    val rtype: Int,
    val authors: HomeAuthorsBean?,
    val timeStr: String,
    val city: String,
    val artPicCount: Int,
    val postsTopicName: String,
    val title: String,
    val pic: String
)
/**
 * @Author: hpb
 * @Date: 2020/5/18
 * @Des:
 */
data class HomeAuthorsBean(
    val authorId: String,
    val avatar: String,
    var isFollow: Int,
    val memberIcon: String,
    val memberId: String,
    val memberName: String,
    val nickname: String,
    val imags: List<LabelBean>,
    val userId: String,
    val headFrameImage: String
)
class LabelBean(
    var img: String,
    var jumpDataType: Int,
    var jumpDataValue: String
)

/**
 * @Author: hpSpecialListBeanb
 * @Date: 2020/5/21
 * @Des:
 */
data class SpecialListBean(
    val artId: String,
    val avatar: Any,
    val catId: Int,
    val collectCount: Int,
    val commentCount: Int,
    val content: String,
    val createTime: String,
    val isFollow: Int,
    val isLike: Int,
    val isRecommend: Int,
    val isSpecialTopic: Int,
    val keyword: Any,
    val likesCount: Int,
    val likesCountBase: Int,
    val likesCountMul: Int,
    val memberIcon: Any,
    val memberId: Any,
    val memberName: Any,
    val nickname: Any,
    val pics: String,
    val publishTime: String,
    val shareCount: Int,
    val sortOrder: Int,
    val specialTopicId: String,
    val specialTopicTitle: Any,
    val status: Int,
    val summary: String,
    val timeStr: String,
    val title: String,
    val totalCount: Int,
    val type: Int,
    val updateTime: String,
    val userId: Int,
    val videoTime: Any,
    val videoUrl: Any,
    val viewsCount: Int,
    val viewsCountBase: Int,
    val viewsCountMul: Int
)