package com.changanford.circle.bean

import com.changanford.common.bean.HomeAuthorsBean
import com.changanford.common.bean.Imag

/**
 * @Author: hpb
 * @Date: 2020/5/20
 * @Des:
 */
data class PostsDetailBean(
    val authorBaseVo: HomeAuthorsBean?,
    val circleId: Int,
    var collectCount: Int,
    var commentCount: Int,
    val content: String?,
    val createTime: Long,
    val imageList: List<ImageList>?,
    val isCheck: Int,
    var isCollection: Int,
    val isDeleted: Int,
    var isGood: Int,
    val isHot: Int,
    var isLike: Int,
    val isPrivate: Int,
    val isPublish: String,
    val isRecommend: Int,
    val isTop: Int,
    val keywords: String,
    val keywordsId: String,
    var likesCount: Int,
    val likesCountBase: Int,
    val likesCountMul: Any,
    val picCount: Int,
    val pics: String,
    val plate: Int,
    val postsId: String,
    val publishTime: Long,
    val shareCount: Int,
    val sortOrder: Int,
    val status: Int,
    val title: String,
    val topicId: String,
    val topicName: String?,
    val type: Int,
    val updateTime: Long,
    val userId: String,
    val videoTime: String,
    val videoUrl: String,
    val viewsCount: Int,
    val viewsCountBase: Int,
    val viewsCountMul: Any,
    val shares: CircleShareBean?,
    val timeStr: String,
    val actionCode: String,
    val plateName: String,
    val circleName: String?,
    val isManager: Boolean?,
    val address: String,
    val lat: Double,
    val lon: Double,
    val province: String,
    val cityCode: String,
    val city: String
)

data class ImageList(
    val imgUrl:String?=""
)
