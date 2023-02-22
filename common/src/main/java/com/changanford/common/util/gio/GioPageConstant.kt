package com.changanford.common.util.gio

/**
 *Author lcw
 *Time on 2023/1/17
 *Purpose
 */
object GioPageConstant {

    var mainTabName = "发现页"
    var findSecondPageName = "发现页-推荐"
    var communitySecondPageName = "社区页-广场"
    var infoEntrance = ""
    var postEntrance = ""
    var infoTheme: String? = null
    var infoId = ""
    var infoName = ""
    var infoShareType = ""
    var isInInfoActivity = false
    var topicEntrance = ""
    var topicDetailTabName = "推荐"
    var circleDetailTabName = "推荐"
    var hotCircleEntrance = ""
    var askSourceEntrance = ""
    var shopOneTabName = "全部商品"
    var prePageType = "无"
    var prePageTypeName = "无"
    var postDetailsName = ""

    fun mainSecondPageName(): String {
        return when (mainTabName) {
            "发现页" -> findSecondPageName
            "社区页" -> communitySecondPageName
            "爱车页" -> "爱车页"
            "商城页" -> "商城页"
            "我的页" -> "我的页"
            else -> ""
        }

    }
}