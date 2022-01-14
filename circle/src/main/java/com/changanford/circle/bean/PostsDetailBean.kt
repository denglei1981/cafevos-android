package com.changanford.circle.bean

import com.changanford.common.bean.HomeAuthorsBean
import com.changanford.common.bean.Imag

/**
 * @Author: lcw
 * @Date: 2020/10/27
 * @Des:
 */
data class PostsDetailBean(
    val authorBaseVo: HomeAuthorsBean? = null,
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
    val keywords: String = "",
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
    val topicName: String? = "",
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
    val circleName: String? = "",
    val isManager: Boolean? = false,
    val address: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val province: String = "",
    val cityCode: String = "",
    val city: String = "",
    val tags:MutableList<PostKeywordBean>?= mutableListOf()
) {
}

data class ImageList(
    val imgUrl: String? = "",
    val imgDesc: String? = ""//type为4,图文长帖时,图片描述
)
