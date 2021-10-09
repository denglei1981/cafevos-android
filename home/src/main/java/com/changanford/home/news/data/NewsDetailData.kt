package com.changanford.home.news.data

import android.text.TextUtils

/**
 *  资讯详情。。。。
 * */
data class NewsDetailData(
    var artCount: Any,
    var artId: Int,
    var authors: Authors,
    var catId: Any,
    var collectCount: Int,
    var commentCount: Int,
    var content: String,
    var createBy: Any,
    var createTime: Long,
    var imageTexts: Any,
    var isCollect: Int,
    var isDeleted: Int,
    var isLike: Int,
    var isRecommend: Int,
    var isSpecialTopic: Int,
    var keyWordsParam: Any,
    var keyword: String,
    var likesCount: Int,
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
    var userId: Int,
    var videoTime: String,
    var videoUrl: String,
    var viewsCount: Int,
    var viewsCountBase: Int,
    var viewsCountMul: Int
){
    fun getPicUrl(): String {
        if (!TextUtils.isEmpty(pics)) { // 不为空时逗号，分隔。
            val pisList = pics.split(",")
            return pisList[0]
        }
        return ""
    }
}