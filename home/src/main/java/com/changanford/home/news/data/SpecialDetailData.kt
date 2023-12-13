package com.changanford.home.news.data

import android.text.TextUtils
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.util.CountUtils

/**
 *  专题详情。。
 * */
data class SpecialDetailData(
    var artCount: Any,
    var artId: Int,
    var articles: List<InfoDataBean>? = arrayListOf(),
    var authors: Any,
    var catId: Any,
    var collectCount: Int,
    var commentCount: Int,
    var content: Any,
    var createBy: Any,
    var createTime: Long,
    var isDeleted: Int,
    var isLike: Int,
    var isRecommend: Int,
    var isSpecialTopic: Int,
    var keyWordsParam: Any,
    var keyword: Any,
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
    var specialTopicId: Any,
    var specialTopicTitle: Any,
    var status: Int,
    var summary: String,
    var timeStr: String,
    var title: String,
    var titleLike: Any,
    var totalCount: Int,
    var type: Any,
    var updateBy: Any,
    var updateTime: Long,
    var userId: Any,
    var videoTime: Any,
    var videoUrl: Any,
    var viewsCount: Int,
    var viewsCountBase: Int,
    var viewsCountMul: Int
) {
    fun getCount(): String {
        var countStr = "${CountUtils.formatNum(totalCount.toString(), false)}资讯，${
            CountUtils.formatNum(
                viewsCount.toString(),
                false
            )
        }浏览"
        return countStr;
    }

    fun getPicUrl(): String {
        if (!TextUtils.isEmpty(pics)) { // 不为空时逗号，分隔。
            val pisList = pics.split(",")
            return pisList[0]
        }
        return ""
    }
}