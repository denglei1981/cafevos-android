package com.changanford.common.bean

import java.io.Serializable

/**
 * Created by Kevin on 2018/9/17.
 * 广告
 */
class AdBean : Serializable {
    val adId: String = ""//":3
    val posId: String? = ""//":2,
    val adName: String? = ""//":"1",
    val adSubName: String? = ""//":"1",
    val adImg: String? =
        ""//":"https://img.cs.leshangche.com/uni-stars-manager/2020/05/14/b4359795c955416c83e5b6dd7b21600b.png",
    val jumpDataValue: String? = ""//":null,
    val jumpDataType: Int? = 0//":null,
    val startTime: String? = ""//":null,
    val endTime: String? = ""//":null,
    val status: String? = ""//":1,
    val sortOrder: String? = ""//":null,
    val isVideo: Int? = 0//":0,
    val video: Int? = 0//":0,
    val videoTime: String? = ""//":null}
}

class LoginVideoBean(val video: String? = "")