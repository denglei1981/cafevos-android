package com.changanford.home.bean

import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.util.CountUtils
import com.changanford.common.util.TimeFromUtils
import com.changanford.common.util.TimeUtils

data class BigShotPostBean(
    var address: String,
    var authorBaseVo: AuthorBaseVo?,
    var circleId: Int,
    var city: String,
    var cityCode: Any,
    var collectCount: Int,
    var commentCount: Long,
    var content: String,
    var contentLike: Any,
    var createBy: Any,
    var createTime: Long,
    var heat: Any,
    var heatUpdateTime: Any,
    var isCheck: Int,
    var isDeleted: Int,
    var isGood: Int,
    var isHot: Int,
    var isLike: Int,
    var isPrivate: Int,
    var isPublish: String,
    var isRecommend: Int,
    var isTop: Int,
    var keywords: Any,
    var lat: Double,
    var likesCount: Long,
    var likesCountBase: Int,
    var likesCountMul: Int,
    var lon: Double,
    var picCount: Int,
    var pics: String,
    var plate: Int,
    var postsId: Int,
    var province: String,
    var provinceCode: Any,
    var publishTime: String,
    var rejectReason: Any,
    var remark: Any,
    var searchValue: Any,
    var shareCount: Int,
    var sortOrder: Int,
    var status: Int,
    var timeStr: String,
    var title: String,
    var topTime: Any,
    var topicId: Int,
    var topicName: Any,
    var type: Int,
    var updateBy: Any,
    var updateTime: Long,
    var userId: Int,
    var videoTime: String,
    var videoUrl: String,
    var viewsCount: Long,
    var viewsCountBase: Int,
    var viewsCountMul: Int
) {

    fun getLikeCount(): String {
        if (likesCount == 0L) {
            return "0"
        }
        return CountUtils.formatNum(likesCount.toString(), false).toString()
    }


    fun getViewCount(): String {
        if (viewsCount == 0L) {
            return "0"
        }
        return CountUtils.formatNum(viewsCount.toString(), false).toString()
    }

    fun getCommentCount(): String {
        if (commentCount == 0L) {
            return "评论"
        }
        return CountUtils.formatNum(commentCount.toString(), false).toString()
    }


    fun getTimeShow():String{
        return TimeFromUtils.format(TimeUtils.MillisToStr(createTime))
    }


}