package com.changanford.common.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.changanford.common.util.CountUtils
import com.changanford.common.util.TimeUtils

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
    val topicAreaConfig: TopicAreaConfig,
    val jumpDataValue: String,
)

data class TopicAreaConfig(
    //是否在推荐显示
    val indexListShow: Int,
    //是否在资讯列表显示
    val articleListShow: Int,
)

data class RecommendData(
    val artCollectCount: Double = 0.0,
    var artCommentCount: Long = 0,
    val artContent: String = "",
    val artCreateTime: Any = "",
    val artId: String = "",
    val artIsSpecialTopic: Int = 0,
    val artKeyword: Any = "",
    var artLikesCount: Long = 0,
    val artPics: String = "",
    val artPublishTime: Long = 0,
    val artShareCount: Double = 0.0,
    val artSpecialTopicId: String = "",
    val artSpecialTopicTitle: String = "",
    val specialTopics: ArrayList<SpecialListBean>? = null,
    val artSummary: String = "",
    val artTitle: String = "",
    val artType: Int = 0,
    val artVideoTime: String = "",
    val artVideoUrl: Any = "",
    val artViewsCount: Long = 0,
    val artViewsCountBase: Double = 0.0,
    val artViewsCount_mul: Double = 0.0,
    var isLike: Int = 0,
    val postsCircleId: String = "",
    val postsCircleName: String = "",
    val postsCollectCount: Double = 0.0,
    var postsCommentCount: Long = 0,
    val postsContent: String = "",
    val postsCreateTime: String = "",
    val postsId: String = "",
    val postsIsCheck: Double = 0.0,
    val postsIsGood: Int? = 0,
    val postsIsHot: Double = 0.0,
    val postsIsPrivate: Double = 0.0,
    val postsIsPublish: Double = 0.0,
    val postsIsTop: Double = 0.0,
    val postsKeywords: String = "",
    var postsLikesCount: Long = 0,
    val postsPics: String = "",
    val postsPlate: Double = 0.0,
    val postsPublishTime: Long = 0,
    val postsShareCount: Double = 0.0,
    val postsTitle: String = "",
    val postsTopicId: String = "",
    val postsType: Int = 0,
    val postsVideoTime: String = "",
    val postsVideoUrl: Any = "",
    val postsViewsCount: Long = 0,
    val rtype: Int = 0, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
    val authors: AuthorBaseVo? = null,
    val timeStr: String = "",
    val city: String = "",
    val addrName: String? = "",
    val artPicCount: Int = 0,
    val postsTopicName: String = "",
    val title: String = "", // 后台设置的
    val pic: String = "", // 后台设置的封面。
    var pisList: List<String>? = null,
    val townName: String? = null,
    val beginTime: Long = 0,
    val deadlineTime: Long = 0,
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
    val wonderful: ActBean = ActBean(),
    val postBean: PostBean? = null,
    val infoFlowTopicVo: InfoFlowTopicVoBean? = null,
    val specialList: SpecialListMainBean? = null,
    val adBean: AdBean? = null
) : MultiItemEntity {
    fun getItemTypeLocal(): Int {
        if (rtype == 3) {// 活动
            return 3
        }
        //话题
        if (rtype == 21) {
            return 4
        }
        if (!TextUtils.isEmpty(postsPics)) { // 不为空时逗号，分隔。
            if (postsType == 3) {
                val list = arrayListOf<String>()
                list.add(postsPics)
                pisList = list
            } else {
                pisList = postsPics.split(",")
            }

        } else if (!TextUtils.isEmpty(artPics)) {
            pisList = artPics.split(",")
        }
        if (pisList != null && pisList!!.size > 1) {
            return 2
        }
        //专题
        if (specialList != null) {
            return 5
        }
        if (adBean != null) {
            return 6
        }
        return 1
    }

    fun plusCityAddr(): String {
        return if (!city.isNullOrEmpty()) {
            "${city} · ${addrName}"
        } else ({
            addrName
        }).toString()
    }

    fun getContent(): String { // 获取内容
        var contentString: String = ""
        when (rtype) {//1 资讯 2 帖子 3 活动
            1 -> {
                contentString = artSummary
            }

            2 -> {
                contentString = postsContent
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
            return "赞"
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

    fun getViewCount(): String {
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
        return CountUtils.formatNum(viewCount.toString(), false).toString()
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

            else -> {
                topicStr = ""
            }
        }
        return if (topicStr.isNullOrEmpty()) "" else topicStr
    }

    fun isArtVideoType(): Boolean {
        return artType == 3
    }

    fun getAddress(): String {

        return city
    }

    fun getEndStr(): Long {
        if (deadlineTime > 1000) {
            return deadlineTime
        }
        return 0
    }

    fun getTimeStateStr(): String {
        when (timeState) {
            "NOT_BEGIN" -> {
                return "未开始"
            }

            "ON_GOING" -> {
                return "进行中"
            }

            "CLOSED" -> {
                return "已截止"
            }
        }
        return ""

    }


    fun getActTimeS(): String {
        return "活动时间: ".plus(TimeUtils.formateActTime(beginTime)).plus(" 至 ")
            .plus(TimeUtils.formateActTime(endTime))
    }

    fun getSignTimes(): String {
        return "报名截止时间: ".plus(TimeUtils.formateActTime(deadlineTime))
    }

    fun getEndTimeTips(): String {
        return "截止时间: ".plus(TimeUtils.formateActTime(deadlineTime))
    }

    override val itemType: Int get() = getItemTypeLocal()

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
    val imags: ArrayList<Imag>,
    val userId: String,
    val headFrameImage: String,
    var carOwner: String = ""
) {
    fun getMemberNames(): String? {
        return if (TextUtils.isEmpty(carOwner)) {
            null
        } else {
            carOwner
        }
    }

    fun showSubtitle(): Boolean {
        if (TextUtils.isEmpty(carOwner)) {
            return false
        }
        return true
    }


}

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
    val specialTopicTitle: String?,
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
) {
    fun getPicUrl(): String {
        if (!TextUtils.isEmpty(pics)) { // 不为空时逗号，分隔。
            val pisList = pics.split(",")
            return pisList[0]
        }
        return ""
    }

    fun getCount(): String {

        var countStr = "${CountUtils.formatNum(totalCount.toString(), false)}资讯    ${
            CountUtils.formatNum(
                viewsCount.toString(),
                false
            )
        }阅读"

        return countStr;
    }
}

data class InfoFlowTopicVoBean(
    val description: String,
    val name: String,
    val pic: String,
    val positionNum: String,
    val topicId: String,
    val postsList: ArrayList<PostDataBean>
)