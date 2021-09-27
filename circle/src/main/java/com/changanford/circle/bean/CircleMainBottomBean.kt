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
    val address: Any? = Any(),
    val authorBaseVo: Any? = Any(),
    val circleId: Int? = 0,
    val city: Any? = Any(),
    val cityCode: Any? = Any(),
    val collectCount: Int = 0,
    val commentCount: Int = 0,
    val content: String = "",
    val contentLike: Any? = Any(),
    val createBy: Any? = Any(),
    val createTime: Long = 0,
    val heat: Any? = Any(),
    val heatUpdateTime: Any? = Any(),
    val isCheck: Int = 0,
    val isDeleted: Int = 0,
    val isGood: Int = 0,
    val isHot: Int = 0,
    val isLike: Int = 0,
    val isPrivate: Int = 0,
    val isPublish: String = "",
    val isRecommend: Int = 0,
    val isTop: Int = 0,
    val keywords: Any? = Any(),
    val lat: Any? = Any(),
    val likesCount: Int = 0,
    val likesCountBase: Int = 0,
    val likesCountMul: Int = 0,
    val lon: Any? = Any(),
    val params: Params = Params(),
    val picCount: Int = 0,
    val pics: String = "",
    val plate: Int = 0,
    val postsId: Int = 0,
    val province: Any? = Any(),
    val provinceCode: Any? = Any(),
    val publishTime: Any? = Any(),
    val rejectReason: Any? = Any(),
    val remark: Any? = Any(),
    val searchValue: Any? = Any(),
    val shareCount: Int = 0,
    val sortOrder: Int? = 0,
    val status: Int = 0,
    val timeStr: String = "",
    val title: Any? = Any(),
    val topTime: Any? = Any(),
    val topicId: Int? = 0,
    val topicName: Any? = Any(),
    val type: Int = 0,
    val updateBy: Any? = Any(),
    val updateTime: Long = 0,
    val userId: Int = 0,
    val videoTime: Any? = Any(),
    val videoUrl: Any? = Any(),
    val viewsCount: Int = 0,
    var itemImgHeight: Int=0,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Int = 0
)

class Extend

class Params