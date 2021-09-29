package com.changanford.common.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 *  文件名：ActBean
 *  创建者: zcy
 *  创建日期：2021/9/29 9:44
 *  描述: TODO
 *  修改描述：TODO
 */
data class InfoBean(
    val dataList: List<InfoData>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class InfoData(
    val artId: Int,
    val authors: AuthorBaseVo?,
    val catId: Int,
    val collectCount: Int,
    var commentCount: Long,
    val content: Any,
    val createTime: Long,
    val isDeleted: Int,
    var isLike: Int,
    val isRecommend: Int,
    val isSpecialTopic: Int,
    val keyword: String,
    var likesCount: Long,
    val likesCountBase: Int,
    val likesCountMul: Double,
    val pics: String,
    val publishTime: Long,
    val shareCount: Int,
    val sortOrder: Int,
    val specialTopicId: Any,
    val specialTopicTitle: Any,
    val status: Int,
    val summary: String,
    val timeStr: String,
    val title: String,
    val type: Int,
    val updateTime: Any,
    val userId: Int,
    val videoTime: String,
    val videoUrl: String,
    val viewsCount: Long,
    val viewsCountBase: Int,
    val viewsCountMul: Double,
    val jumpVal: String,
    val jumpType: Int
)


data class AuthorBaseVo(
    val authorId: Int = 0,
    val avatar: String = "",
    val imags: ArrayList<Imag> = arrayListOf(),
    val isFollow: Int = 0,
    val medalImage: Any? = Any(),
    val medalName: Any? = Any(),
    val memberIcon: String = "",
    val memberId: Int = 0,
    val memberName: String = "",
    val nickname: String = ""
)

data class PostBean(
    val dataList: ArrayList<PostDataBean> = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class PostDataBean(
    val authorBaseVo: AuthorBaseVo? = AuthorBaseVo(),
    val circleId: Int? = 0,
    var itemImgHeight: Int = 0,
    val collectCount: Int = 0,
    val commentCount: Int = 0,
    val content: String? = "",
    val contentLike: Any? = Any(),
    val createBy: Any? = Any(),
    val createTime: Long = 0,
    val isCheck: Int = 0,
    val isDeleted: Int = 0,
    val isGood: Int = 0,//是否加精 1：加精，2：不加精, 3: 加精审核中
    val isHot: Int = 0,
    val isLike: Int = 0,
    val isPrivate: Int = 0,
    val isPublish: String = "",
    val isRecommend: Int = 0,
    val isTop: Int = 0,
    val keywords: String = "",
    val likesCount: Int = 0,
    val likesCountBase: Int = 0,
    val likesCountMul: Int = 0,
    val picCount: Int = 0,
    val pics: String = "",
    val plate: Int = 0,
    val postsId: Int = 0,
    val publishTime: Any? = Any(),
    val rejectReason: Any? = Any(),
    val remark: Any? = Any(),
    val searchValue: Any? = Any(),
    val shareCount: Int = 0,
    val sortOrder: Any? = Any(),
    val status: Int = 0,
    val timeStr: String = "",
    val title: String? = "",
    val city: String? = "",
    val topTime: Any? = Any(),
    val topicId: Int? = 0,
    val type: Int = 0,
    val updateBy: Any? = Any(),
    val updateTime: Long = 0,
    val userId: Int = 0,
    val videoTime: Any? = Any(),
    val videoUrl: Any? = Any(),
    val viewsCount: Int = 0,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Int = 0
)

data class AcBean(var title: String, var iconUrl: String, var type: Int) : MultiItemEntity {
    override val itemType: Int
        get() = type
}

data class AccBean(
    val dataList: List<AcDataBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class AcDataBean(
    val activityJoinCount: Int,
    val activityTotalCount: Int,
    val beginTime: String,
    val cityName: String,
    val coverImg: String,
    val deadLineTime: Long,
    val endTime: Long,
    val jumpType: Int,
    val jumpVal: String,
    val official: Int,
    val provinceName: String,
    val reason: String,
    val status: Int,
    val title: String,
    val townName: String,
    val userId: Int,
    val wonderfulId: Int,
    val wonderfulType: Int,
    var serverTime: Long,
    val createTime: Long
)