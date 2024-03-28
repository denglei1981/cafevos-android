package com.changanford.circle.bean

import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.CircleShareBean
import com.changanford.common.util.CountUtils

/**
 * @Author: lcw
 * @Date: 2020/10/27
 * @Des:
 */
data class PostsDetailBean(
    val authorBaseVo: AuthorBaseVo? = null,
    val circleId: Int = 0,
    var collectCount: Int = 0,
    var commentCount: Int = 0,
    val content: String?="",
    val createTime: Long = 0,
    val imageList: List<ImageList>? = arrayListOf(),
    val isCheck: Int = 0,
    var isCollection: Int = 0,
    val isDeleted: Int = 0,
    var isGood: Int = 0,
    val isHot: Int = 0,
    var isLike: Int = 0,
    val isPrivate: Int = 0,
    val isPublish: String = "",
    val isRecommend: Int = 0,
    val isTop: Int = 0,
    var keywords: String? = null,
    val keywordsId: String = "",
    var likesCount: Int = 0,
    val likesCountBase: Int = 0,
    val likesCountMul: Any = Any(),
    val picCount: Int = 0,
    val pics: String = "",
    val plate: Int = 0,
    val postsId: String = "",
    val publishTime: Long = 0,
    var shareCount: Int = 0,
    val sortOrder: Int = 0,
    val status: Int = 0,
    val title: String = "",
    val topicId: String = "",
    val topicName: String? = null,
    val type: Int = 0,
    val updateTime: Long = 0,
    val userId: String = "",
    val videoTime: String = "",
    val videoUrl: String = "",
    val viewsCount: Int = 0,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Any = Any(),
    val shares: CircleShareBean? = null,
    val timeStr: String = "",
    val actionCode: String = "",
    val plateName: String = "",
    val carModelIds: String? = null,
    val carModelName: String? = null,
    val circleName: String? = "",
    val isManager: Boolean? = false,
    var address: String? =null,
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    val province: String = "",
    val cityCode: String = "",
    val city: String = "",
    val tags:MutableList<PostKeywordBean>?= mutableListOf(),
    var addrName:String?="",
) {
    fun showCity():String{
        if(addrName?.isNotEmpty() == true){
            return city.plus("·").plus(addrName)
        }
        return city
    }

    fun getCommentCountResult(): String {
        var commentCountResult: String = ""
        if (commentCount == 0) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCountResult
    }

    fun getLikesCountResult(): String {
        var commentCountResult: String = ""
        if (likesCount == 0) {
            return "0"
        }
        commentCountResult = CountUtils.formatNum(likesCount.toString(), false).toString()
        return commentCountResult
    }

    fun getCollectCountResult(): String {
        var commentCountResult: String = ""
        if (collectCount == 0) {
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
}

data class ImageList(
    val imgUrl: String? = "",
    val imgDesc: String? = ""//type为4,图文长帖时,图片描述
)
