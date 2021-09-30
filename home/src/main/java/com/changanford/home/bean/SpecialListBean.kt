package com.changanford.home.bean

import com.changanford.common.bean.HomeExtendBean


class SpecialListMainBean(
    var dataList: ArrayList<SpecialListBean>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class SpecialListBean(
    var artCount: Int=0,
    var artId: Int,
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
    var totalCount: Int,
    var type: Any,
    var updateBy: Any,
    var updateTime: String,
    var userId: Long=-1,
    var videoTime: Long,
    var videoUrl: String="",
    var viewsCount: Int,
    var viewsCountBase: Int,
    var viewsCountMul: Int
)