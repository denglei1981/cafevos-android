package com.changanford.common.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.changanford.common.util.CountUtils
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
    var artCommentCount: Long,
    val artContent: String,
    val artCreateTime: Any,
    val artId: String,
    val artIsSpecialTopic: Int,
    val artKeyword: Any,
    var artLikesCount: Long = 0,
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
    val artViewsCount: Long,
    val artViewsCountBase: Double,
    val artViewsCount_mul: Double,
    var isLike: Int,
    val postsCircleId: Double,
    val postsCollectCount: Double,
    var postsCommentCount: Long,
    val postsContent: String,
    val postsCreateTime: String,
    val postsId: String,
    val postsIsCheck: Double,
    val postsIsGood: Double,
    val postsIsHot: Double,
    val postsIsPrivate: Double,
    val postsIsPublish: Double,
    val postsIsTop: Double,
    val postsKeywords: String,
    var postsLikesCount: Long = 0,
    val postsPics: String,
    val postsPlate: Double,
    val postsPublishTime: Long,
    val postsShareCount: Double,
    val postsTitle: String,
    val postsTopicId: Double,
    val postsType: Int,
    val postsVideoTime: String,
    val postsVideoUrl: Any,
    val postsViewsCount: Long,
    val rtype: Int, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
    val authors: AuthorBaseVo?,
    val timeStr: String,
    val city: String,
    val artPicCount: Int,
    val postsTopicName: String,
    val title: String, // 后台设置的
    val pic: String, // 后台设置的封面。
    var pisList: List<String>? = null,
    val townName: String? = null,
    val beginTime: Long = 0,
    val deadLineTime: Long = 0,
    val endTime: Long = 0,
    val official: Int? = null,
    val wonderfulType: Int? = null,
    val activityJoinCount: String? = null, //参加活动人数
    val coverImg: String? = null,
    val jumpVal: String? = null,
    val serverTime: Long = 0,
    val cityName: String? = null,
    val provinceName: String? = null,
    val recommend: String? = null,
    val hot: String? = null,
    val wonderfulTitle: String = "",
    val wonderfulPic: String = "",
    val timeState: String = "",
    val openTime: String = "",
    val jumpType: String = "",
    val jumpValue: String = "",
    ) : MultiItemEntity {
    private fun getItemTypeLocal(): Int {
        if (rtype == 3) {// 活动
            return 3
        }
        if (!TextUtils.isEmpty(postsPics)) { // 不为空时逗号，分隔。
            pisList = postsPics.split(",")
        } else if (!TextUtils.isEmpty(artPics)) {
            pisList = artPics.split(",")
        }
        if (pisList != null && pisList!!.size > 1) {
            return 2
        }
        return 1
    }

    fun getContent(): String { // 获取内容
        var contentString: String = ""
        when (rtype) {//1 资讯 2 帖子 3 活动
            1 -> {
                contentString = artSummary
            }
            2 -> {
                contentString = postsTopicName
            }
            3 -> {
                contentString = artTitle
            }
        }
        return contentString
    }

    var likeCountResult: String = "" // 喜欢的数量。。
    var likeCount: Long = 0
    fun getLikeCount(): String { // 获取点赞数量
        when (rtype) {
            1 -> {
                likeCount = artLikesCount
            }
            2 -> {
                likeCount = postsLikesCount
            }
            3 -> {
                likeCount = artLikesCount
            }
        }
        if (likeCount == 0L) {
            return "点赞"
        }
        likeCountResult = CountUtils.formatNum(likeCount.toString(), false).toString()
        return likeCountResult
    }

    var commentCommentResult: String = ""
    var commentCount: Long = 0
    fun getCommentCount(): String {
        when (rtype) {
            1 -> {
                commentCount = artCommentCount
            }
            2 -> {
                commentCount = postsCommentCount
            }
            3 -> {
                commentCount = artCommentCount
            }
        }
        if (commentCount == 0L) {
            return "评论"
        }
        commentCommentResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCommentResult
    }

    var timeAndViewCountResult: String = ""
    fun getTimeAdnViewCount(): String {
        var viewCount: Long = 0
        when (rtype) {
            1 -> {
                viewCount = artViewsCount

            }
            2 -> {
                viewCount = postsViewsCount

            }
            3 -> {
                viewCount = artViewsCount
            }

        }
        val viewCountStr = CountUtils.formatNum(viewCount.toString(), false).toString()


        timeAndViewCountResult = timeStr.plus("  ").plus(viewCountStr).plus("浏览")
        return timeAndViewCountResult
    }


    fun getPicLists(): List<String>? {
        return pisList
    }

    fun getTopic(): String {
        var topicStr: String = ""
        when (rtype) {
            1 -> {
                topicStr = if (TextUtils.isEmpty(title)) {
                    artTitle
                } else {
                    title
                }
            }
            2 -> {
                topicStr = postsTitle
            }
            3 -> {
                topicStr = artSpecialTopicTitle
            }
        }
        return topicStr
    }

    fun isArtVideoType(): Boolean {
        return artType == 3
    }

    fun getAddress(): String {
        return city
    }

    override val itemType: Int
        get() = getItemTypeLocal()
}

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
    val imags: List<Imag>,
    val userId: String,
    val headFrameImage: String
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
    val commentCount: Int = 0,
    val content: String,
    val createTime: String,
    val isFollow: Int,
    val isLike: Int,
    val isRecommend: Int,
    val isSpecialTopic: Int,
    val keyword: Any,
    val likesCount: Int = 0,
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
    val viewsCount: Int = 0,
    val viewsCountBase: Int,
    val viewsCountMul: Int
)