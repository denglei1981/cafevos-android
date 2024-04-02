package com.changanford.home.news.data

import android.text.TextUtils
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.util.CountUtils


/**
 *  资讯详情。。。。
 * */
data class NewsDetailData(
    var artCount: Any,
    var artId: Int,
    var authors: AuthorBaseVo,
    var catId: Any,
    var collectCount: Long,
    var commentCount: Long,
    var content: String,
    var createBy: Any,
    var createTime: Long,
    var isAddAdvs:Boolean,
    var imageTexts: ArrayList<ImageTexts>, // 图片信息。纯图片使用。
    var isCollect: Int,
    var isDeleted: Int,
    var isLike: Int,
    var isRecommend: Int,
    var isSpecialTopic: Int,
    var keyWordsParam: Any,
    var keyword: String,
    var likesCount: Long,
    var likesCountBase: Int,
    var likesCountMul: Int,
    var picCount: Int,
    var pics: String,
    var publishTime: Long,
    var remark: Any,
    var searchValue: Any,
    var shareCount: Int,
    var shares: Shares,
    var sortOrder: Int,
    var specialTopicId: Int,
    var specialTopicTitle: String,
    var status: Int,
    var summary: String,
    var tags: List<Tag>,
    var timeStr: String,
    var title: String,
    var titleLike: Any,
    var type: Int,
    var updateBy: Any,
    var updateTime: Long,
    var userId: String,
    var videoTime: String,
    var videoUrl: String,
    var viewsCount: Long,
    var viewsCountBase: Int,
    var viewsCountMul: Int
) {
    fun getPicUrl(): String {
        if (!TextUtils.isEmpty(pics)) { // 不为空时逗号，分隔。
            val pisList = pics.split(",")
            return pisList[0]
        }
        return ""
    }
    fun getPicSize():Int{
       return listOf(pics.split(",")).size
    }

//    fun getCommentCount(): String { // 获取评论数量
//        return CountUtils.formatNum(commentCount.toString(), false).toString()
//    }
//
//    fun getLikeCount(): String {
//        return CountUtils.formatNum(likesCount.toString(), false).toString()
//    }
//
//    fun getShareCount(): String {
//        return CountUtils.formatNum(shareCount.toString(), false).toString()
//    }

    fun getCommentCountResult(): String {
        var commentCountResult: String = ""
        if (commentCount == 0L) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCountResult
    }

    fun getLikesCountResult(): String {
        var commentCountResult: String = ""
        if (likesCount == 0L) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(likesCount.toString(), false).toString()
        return commentCountResult
    }

    fun getCollectCountResult(): String {
        var commentCountResult: String = ""
        if (collectCount == 0L) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(collectCount.toString(), false).toString()
        return commentCountResult
    }

    fun getShareCountResult(): String {
        var commentCountResult: String = ""
        if (shareCount == 0) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(shareCount.toString(), false).toString()
        return commentCountResult
    }

    fun getShowContent(): String {
        return if (!TextUtils.isEmpty(content)) {
            content
        } else {
            summary
        }
    }

    fun getCollectCount(): String {
        return CountUtils.formatNum(collectCount.toString(), false).toString()
    }


}