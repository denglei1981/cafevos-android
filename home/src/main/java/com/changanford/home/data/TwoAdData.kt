package com.changanford.home.data

data class TwoAdData(
    val app_index_topic: MutableList<AdBean>,
    val app_index_ads: MutableList<AdBean>,
    val app_index_banner: MutableList<AdBean>,
    val app_index_background: MutableList<AdBean>
)

data class AdBean(
    var adId: Int,
    var adImg: String,
    var adName: String,
    var adSubName: String,
    var androidShow: Int,
    var androidVersion: String,
    var endTime: Long,
    var iosShow: Int,
    var iosVersion: String,
    var isVideo: Int,
    var jumpDataType: Int,
    var jumpDataValue: String,
    var posId: Int,
    var posName: Any,
    var seeAuthType: String,
    var sortOrder: Int,
    var startTime: Long,
    var status: Int,
    var tagIds: Any,
    var tagNames: Any,
    var videoTime: Any
)