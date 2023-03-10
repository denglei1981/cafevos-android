package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2023/1/10
 *Purpose
 */
data class NewEstOneBean(
    val appVo: NewEstOneItem?,
    val maVo: NewEstOneItem?,
)

data class NewEstOneItem(
    val ads: ArrayList<NewEstOneItemBean> = arrayListOf(),
    val showType: String = ""
)

data class NewEstOneItemBean(
    val adId: Int = 0,
    val adImg: String = "",
    val adName: String = "",
    val adSubName: Any? = Any(),
    val androidShow: Int = 0,
    val androidVersion: String = "",
    val endTime: Long = 0,
    val iosShow: Int = 0,
    val iosVersion: String = "",
    val isVideo: Int = 0,
    val jumpDataType: Int = 0,
    val jumpDataValue: String? = "",
    val maJourneyActCtrlId: Any? = Any(),
    val maPlanId: Any? = Any(),
    val posId: Int = 0,
    val posName: Any? = Any(),
    val seeAuthType: String = "",
    val sortOrder: Int = 0,
    val sourceType: Int = 0,
    val startTime: Long = 0,
    val status: Int = 0,
    val tagIds: Any? = Any(),
    val tagNames: Any? = Any(),
    val videoTime: Any? = Any()
)