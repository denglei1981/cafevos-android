package com.changanford.common.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.changanford.common.util.CountUtils

/**
 *  文件名：ActBean
 *  创建者: zcy
 *  创建日期：2021/9/29 9:44
 *  描述: TODO
 *  修改描述：TODO
 */
data class InfoBean(
    val dataList: List<InfoDataBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class InfoDataBean(
    override val itemType: Int,
    val artId: String = "",
    var authors: AuthorBaseVo? = null,
    val catId: Int = 0,
    val collectCount: Int = 0,
    var commentCount: Long = 0L,
    val content: String = "",
    val createTime: Long = 0L,
    val isDeleted: Int = 0,
    var isLike: Int = 0,
    val isRecommend: Int = 0,
    val isSpecialTopic: Int = 0,
    val keyword: String = "",
    var likesCount: Long = 0L,
    val likesCountBase: Int = 0,
    val likesCountMul: Double = 0.0,
    val pics: String = "",
    val publishTime: Long = 0L,
    val shareCount: Int = 0,
    val sortOrder: Int = 0,
    val specialTopicId: Any = Any(),
    val specialTopicTitle: String = "",
    val status: Int = 0,
    val summary: String = "",
    val timeStr: String = "",
    val title: String = "",
    val type: Int = 0,//资讯类型 1图文 2 图片 3 视频
    val updateTime: String = "",
    val userId: String = "",
    val videoTime: String = "",
    val videoUrl: String = "",
    val viewsCount: Long = 0L,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Double = 0.0,
    val jumpVal: String = "",
    val jumpType: Int = 0
) : MultiItemEntity {

    fun getCommentCountResult(): String {
        var commentCountResult: String = ""
        if (commentCount == 0L) {
            return "评论"
        }
        commentCountResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCountResult
    }

    fun getCommentCountAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("评论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getCommentDiscussAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("讨论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getContentStr(): String {
        if (!TextUtils.isEmpty(content)) {
            return content
        }
        if (!TextUtils.isEmpty(summary)) {
            return summary
        }
        return ""
    }

    fun getPicCover(): String { // 获取封面。
        if (!TextUtils.isEmpty(pics)) {
            val asList =pics.split(",")
            return asList[0]
        }
        return ""
    }

    fun getSubTitleStr(): String {
        return if (!TextUtils.isEmpty(specialTopicTitle)) {
            "#".plus(specialTopicTitle).plus("#")
        } else {
            summary
        }
    }

    var timeAndViewCountResult: String = ""
    fun getTimeAdnViewCount(): String {
        val viewCountStr = CountUtils.formatNum(viewsCount.toString(), false).toString()
        timeAndViewCountResult = timeStr.plus("  ").plus(viewCountStr).plus("浏览")
        return timeAndViewCountResult
    }
}

data class AuthorBaseVo(
    val authorId: String,
    val avatar: String = "",
    val imags: ArrayList<Imag> = arrayListOf(),
    var isFollow: Int = 0,
    val medalImage: Any? = Any(),
    val medalName: Any? = Any(),
    val memberIcon: String = "",
    val memberId: Int = 0,
    val memberName: String = "",
    val nickname: String = "",
    // 一下三个参数是用户搜索的。
    val headFrameName: String = "",
    val headFrameImage: String = "",
    val userId: String=""
) {
    fun getMemberNames(): String {
        return if (TextUtils.isEmpty(memberName)) {
            "车迷级公民"
        } else {
            memberName
        }
    }
}

data class PostBean(
    val dataList: ArrayList<PostDataBean> = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class PostDataBean(
    val authorBaseVo: AuthorBaseVo? = null,
    val circleId: Int? = 0,
    var itemImgHeight: Int = 0,
    val collectCount: Int = 0,
    var commentCount: Long = 0,
    val content: String = "",
    val contentLike: Any? = Any(),
    val createBy: Any? = Any(),
    val createTime: Long = 0,
    val isCheck: Int = 0,
    val isDeleted: Int = 0,
    val isGood: Int = 0,//是否加精 1：加精，2：不加精, 3: 加精审核中
    val isHot: Int = 0,
    var isLike: Int = 0,
    val isPrivate: Int = 0,
    val isPublish: String = "",
    val isRecommend: Int = 0,
    val isTop: Int = 0,
    val keywords: String = "",
    var likesCount: Long = 0,
    val likesCountBase: Int = 0,
    val likesCountMul: Int = 0,
    val picCount: Int = 0,
    val pics: String = "",
    val plate: Int = 0,
    val postsId: Int = 0,
    val publishTime: Any? = Any(),
    val rejectReason: Any? = Any(),
    val remark: Any? = Any(),
    val searchValue: Any? = Any(),
    val shareCount: Int = 0,
    val sortOrder: Any? = Any(),
    val status: Int = 0,
    val timeStr: String = "",
    val title: String? = "",
    val city: String? = "",
    val topicName: String? = "",
    val topTime: Any? = Any(),
    val topicId: Int? = 0,
    val type: Int = 0,
    val updateBy: Any? = Any(),
    val updateTime: Long = 0,
    val userId: Int = 0,
    val videoTime: Any? = Any(),
    val videoUrl: Any? = Any(),
    val viewsCount: Int = 0,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Int = 0
) {
    fun getCommentCountAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("评论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getContentStr(): String {
        if (!TextUtils.isEmpty(content)) {
            return content
        }
        return ""
    }

}

data class AcBean(var title: String, var iconUrl: String, var type: Int) : MultiItemEntity {
    override val itemType: Int
        get() = type
}

data class AccBean(
    val dataList: List<ActDataBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class ActDataBean(
    val activityJoinCount: Int = 0,
    val activityTotalCount: Int = 0,
    val beginTime: String = "",
    val cityName: String = "",
    val coverImg: String = "",
    val deadLineTime: Long = 0L,
    val endTime: Long = 0L,
    val jumpType: Int = 0,
    val jumpVal: String = "",
    val official: Int = 0,
    val provinceName: String = "",
    val reason: String = "",
    val status: Int = 0,
    val title: String = "",
    val townName: String = "",
    val userId: Int = 0,
    val wonderfulId: Int = 0,
    val wonderfulType: Int = 0,
    var serverTime: Long = 0L,
    val createTime: Long = 0L,
    val browseCount: Long = 0L
)