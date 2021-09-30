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
    val dataList: List<InfoDataBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class InfoDataBean(
    override val itemType: Int,
    val artId: Int = 0,
    val authors: AuthorBaseVo? = AuthorBaseVo(),
    val catId: Int = 0,
    val collectCount: Int = 0,
    var commentCount: Long = 0L,
    val content: String="",
    val createTime: Long = 0L,
    val isDeleted: Int = 0,
    var isLike: Int = 0,
    val isRecommend: Int = 0,
    val isSpecialTopic: Int = 0,
    val keyword: String = "",
    var likesCount: Long = 0L,
    val likesCountBase: Int = 0,
    val likesCountMul: Double = 0.0,
    val pics: String = "",
    val publishTime: Long = 0L,
    val shareCount: Int = 0,
    val sortOrder: Int = 0,
    val specialTopicId: Any = Any(),
    val specialTopicTitle: Any = Any(),
    val status: Int = 0,
    val summary: String = "",
    val timeStr: String = "",
    val title: String = "",
    val type: Int = 0,
    val updateTime: Any = Any(),
    val userId: Int = 0,
    val videoTime: String = "",
    val videoUrl: String = "",
    val viewsCount: Long = 0L,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Double = 0.0,
    val jumpVal: String = "",
    val jumpType: Int = 0
) : MultiItemEntity


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
    val dataList: List<ActDataBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class ActDataBean(
    val activityJoinCount: Int = 0,
    val activityTotalCount: Int = 0,
    val beginTime: String = "",
    val cityName: String = "",
    val coverImg: String = "",
    val deadLineTime: Long = 0L,
    val endTime: Long = 0L,
    val jumpType: Int = 0,
    val jumpVal: String = "",
    val official: Int = 0,
    val provinceName: String = "",
    val reason: String = "",
    val status: Int = 0,
    val title: String = "",
    val townName: String = "",
    val userId: Int = 0,
    val wonderfulId: Int = 0,
    val wonderfulType: Int = 0,
    var serverTime: Long = 0L,
    val createTime: Long = 0L
)