package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2021/10/13
 *Purpose
 */
data class CircleMainBean(
    val allCircles: ArrayList<AllCircle> = arrayListOf(),
    val interestCircles: InterestCircles = InterestCircles(),
    val regionCircles: InterestCircles = InterestCircles(),
    val topics: ArrayList<Topic> = arrayListOf()
)

data class AllCircle(
    val checkNoReason: Any? = Any(),
    val checkPassTime: Any? = Any(),
    val checkStatus: Any? = Any(),
    val circleId: Int = 0,
    val cityId: Any? = Any(),
    val cityName: Any? = Any(),
    val createBy: Any? = Any(),
    val createTime: Any? = Any(),
    val description: Any? = Any(),
    val districtId: Any? = Any(),
    val districtName: Any? = Any(),
    val hotIcon: Any? = Any(),
    val isGrounding: Any? = Any(),
    val isHot: Any? = Any(),
    val isRecommend: Any? = Any(),
    val lastPostsLikeTime: Any? = Any(),
    val lastPostsTime: Any? = Any(),
    val lat: Any? = Any(),
    val lng: Any? = Any(),
    val maxUserCount: Any? = Any(),
    val name: String = "",
    val nameColor: Any? = Any(),
    val nameLike: Any? = Any(),
    val params: Params = Params(),
    val pic: String = "",
    val postsCount: Any? = Any(),
    val provinceId: Any? = Any(),
    val provinceName: Any? = Any(),
    val remark: Any? = Any(),
    val searchValue: Any? = Any(),
    val sortOrder: Any? = Any(),
    val type: Any? = Any(),
    val updateBy: Any? = Any(),
    val updateTime: Any? = Any(),
    val userCount: Any? = Any(),
    val userId: Any? = Any()
)

data class InterestCircles(
    val circleInfos: ArrayList<CircleInfo> = arrayListOf(),
    val circleViewType: String = "",
    val histryCount: Int = 0,
    val interestCount: Int = 0,
    val regionCount: Int = 0
)

data class Topic(
    val description: String = "",
    val likesCount: Int = 0,
    val name: String = "",
    val pic: String = "",
    val postsCount: Int = 0,
    val topicId: Int = 0,
    val userCount: Int = 0
)

class Params

data class CircleInfo(
    val circleId: Int = 0,
    val description: String = "",
    val name: String = "",
    val pic: String = "",
    val postsCount: Int = 0,
    val userCount: Int = 0
)
