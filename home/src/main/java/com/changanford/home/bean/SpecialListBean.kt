package com.changanford.home.bean

import android.text.TextUtils
import com.changanford.common.bean.HomeExtendBean
import com.changanford.common.util.CountUtils


class SpecialListMainBean(
    var dataList: ArrayList<SpecialListBean>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class SpecialListBean(
    var artCount: Int = 0,
    var artId: String,
    var articles: Any,
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
    var shares: Any,
    var sortOrder: Int,
    var specialTopicId: Any,
    var specialTopicTitle: String,
    var status: Int,
    var summary: String,
    var timeStr: String,
    var title: String,
    var titleLike: Any,
    var totalCount: Long,
    var type: Any,
    var updateBy: Any,
    var updateTime: String,
    var userId: Long = -1,
    var videoTime: Long,
    var videoUrl: String = "",
    var viewsCount: Int,
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

    fun getCount(): String {

        var countStr = "${CountUtils.formatNum(totalCount.toString(), false)}资讯 ${
            CountUtils.formatNum(
                viewsCount.toString(),
                false
            )
        }阅读量"

        return countStr;
    }
}