package com.changanford.common.bean

import com.changanford.common.utilext.GlideUtils

/**
 * Created by Kevin on 2018/9/17.
 * 广告
 */
class AdBean(
    val adId: String = "",//":3
    val posId: String? = "",//":2,
    val adName: String? = "",//":"1",
    val adSubName: String? = "",//":"1",
    val adImg: String? = "",//":"https://img.cs.leshangche.com/uni-stars-manager/2020/05/14/b4359795c955416c83e5b6dd7b21600b.png",
    val jumpDataValue: String? = "",//":null,
    val jumpDataType: Int? = 0,//":null,
    val startTime: String? = "",//":null,
    val endTime: String? = "",//":null,
    val status: String? = "",//":1,
    val sortOrder: String? = "",//":null,
    val isVideo: Int? = 0,//":0,
    val video: Int? = 0,//":0,
    val videoTime: String? = "",
    var androidShow: Int,
    var showPosition: Int,
    var androidVersion: String,
    var iosShow: Int,
    var iosVersion: String,
    var posName: Any,
    var seeAuthType: String,
    var tagIds: Any,
    var tagNames: Any,
    var code:String? = "",
    var maPlanId:String? = "",
    var maJourneyActCtrlId:String? = "",
    var maJourneyId:String? = "",
) {

    fun getAdImgUrl(): String {
        return GlideUtils.defaultHandleImageUrl(adImg)
    }
    fun getImg(): String {
        val split = adImg?.split(",")
        return split?.get(0)?:""
    }
}

class LoginVideoBean(val video: String? = null)

//{
//  "img": "ford-manager/2021/11/29/11d59da44e2c460ab2a54127f6ef4311.png",
//  "title": "升级您的用车体验",
//  "des": "绑定您的爱车，成为认证车主享受更多专属服务",
//  "carListRightsIsShow": "true",
//  "carListRightsContent": "您已认证车主成功，可以享受相关车主权益",
//  "rightsDetailContent": "车主权益详情介绍内容xxxxxxx",
//  "authDetailRightsIsShow": "true",
//  "authDetailRightsContent": "详情页底部权益内容xxxx"
//}
data class CarAuthQYBean(
    val img: String = "",
    val title: String = "",
    val carListRightsIsShow: Boolean = false,
    val carListRightsContentY: String = "",
    val carListRightsContentN: String = "",
    val rightsDetailContent: String = "",
    val authDetailRightsIsShow: Boolean = false,
    val authDetailRightsContent: String = "",
    val authPrompt: String? = "",
    val removeCarNotice: String? = "",
    val contactCustomerService:String?=""
)