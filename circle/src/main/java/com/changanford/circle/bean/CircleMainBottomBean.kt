package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2021/9/27
 *Purpose
 */
data class CircleMainBottomBean(
    val dataList: ArrayList<CircleMainBottomItemBean> = arrayListOf(),
    val extend: Extend = Extend(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class CircleMainBottomItemBean(
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
    val isGood: Int = 0,
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
    val params: Params = Params(),
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

class Extend

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

class Params

data class Imag(
    val img: String = "",
    val jumpDataType: Int = 0,
    val jumpDataValue: String = ""
)