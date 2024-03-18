package com.changanford.common.bean


class SpecialListMainBean(
    var dataList: ArrayList<SpecialListBean>,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

//data class SpecialListBean(
//    var artCount: Int = 0,
//    var artId: String = "",
//    var articles: Any = Any(),
//    var authors: Any = Any(),
//    var catId: Any = Any(),
//    var collectCount: Int = 0,
//    var commentCount: Int = 0,
//    var content: Any = Any(),
//    var createBy: Any = Any(),
//    var createTime: Long = 0L,
//    var isDeleted: Int = 0,
//    var isLike: Int = 0,
//    var isRecommend: Int = 0,
//    var isSpecialTopic: Int = 0,
//    var keyWordsParam: Any = Any(),
//    var keyword: Any = Any(),
//    var likesCount: Int = 0,
//    var likesCountBase: Int = 0,
//    var likesCountMul: Int = 0,
//    var picCount: Int = 0,
//    var pics: String = "",
//    var publishTime: Long = 0L,
//    var remark: Any = Any(),
//    var searchValue: Any = Any(),
//    var shareCount: Int = 0,
//    var shares: Any = Any(),
//    var sortOrder: Int = 0,
//    var specialTopicId: Any = Any(),
//    var specialTopicTitle: String = "",
//    var status: Int = 0,
//    var summary: String = "",
//    var timeStr: String = "",
//    var title: String = "",
//    var titleLike: Any = Any(),
//    var totalCount: Long = 0L,
//    var type: Any = Any(),
//    var updateBy: Any = Any(),
//    var updateTime: String = "",
//    var userId: Long = -1,
//    var videoTime: Long = 0L,
//    var videoUrl: String = "",
//    var viewsCount: Int = 0,
//    var viewsCountBase: Int = 0,
//    var viewsCountMul: Int = 0
//) {
//    fun getPicUrl(): String {
//        if (!TextUtils.isEmpty(pics)) { // 不为空时逗号，分隔。
//            val pisList = pics.split(",")
//            return pisList[0]
//        }
//        return ""
//    }
//
//    fun getCount(): String {
//
//        var countStr = "${CountUtils.formatNum(totalCount.toString(), false)}资讯 ${
//            CountUtils.formatNum(
//                viewsCount.toString(),
//                false
//            )
//        }阅读量"
//
//        return countStr;
//    }
//}