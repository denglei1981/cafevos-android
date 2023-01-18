package com.changanford.common.util.gio

import com.changanford.common.bean.UserInfoBean
import com.changanford.common.util.MConstant
import com.changanford.common.util.gio.GioPageConstant.infoEntrance
import com.changanford.common.util.gio.GioPageConstant.infoId
import com.changanford.common.util.gio.GioPageConstant.infoName
import com.changanford.common.util.gio.GioPageConstant.infoTheme
import com.changanford.common.util.gio.GioPageConstant.topicDetailTabName
import com.changanford.common.util.gio.GioPageConstant.topicEntrance
import com.growingio.android.sdk.autotrack.GrowingAutotracker

/**
 *Author lcw
 *Time on 2023/1/17
 *Purpose
 */
object GIOUtils {

    //写入用户信息
    fun setLoginUserAttributes(userInfoBean: UserInfoBean) {
        val map = HashMap<String, String>()
        //福特系统里记录的用户id
        map["fy_cmcID_ppl"] = userInfoBean.cmcOpenid
        //性别
        map["fy_gender_ppl"] = when (userInfoBean.sex) {
            0 -> "保密"
            1 -> "男"
            2 -> "女"
            else -> "无"
        }
        //城市
        map["fy_province_ppl"] = if (userInfoBean.provinceName.isNullOrEmpty()) {
            "无"
        } else userInfoBean.provinceName
        //生日
        map["fy_birthday_ppl"] = userInfoBean.birthday.toString()
        //年龄
        map["fy_userAge_ppl"] = "无"
        //会员等级
        map["fy_memberLever_ppl"] = userInfoBean.ext.growSeriesName
        //是否车主
        map["fy_ifCarOwner_ppl"] = if (MConstant.isCarOwner == 1) {
            "是"
        } else "否"
        //车主认证时间
        map["fy_verificationTime_ppl"] = "无"
        //app注册时间
        map["fy_registerTime_ppl"] = userInfoBean.createTime.toString()
        //当前福币数量
        map["fy_fbNumber_ppl"] = userInfoBean.ext.totalIntegral
        //注册渠道
        map["fy_registerChannel_ppl"] = userInfoBean.registerChannel.toString()
        GrowingAutotracker.get().setLoginUserAttributes(map)
    }

    //导航浏览
    fun homePageView() {
        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        map["fy_prePageType_var"] = "无"
        map["fy_prePageName_var"] = "无"
        map["fy_prePageUrl_var"] = "无"

        trackCustomEvent("fy_homePageView", map)
    }

    //导航流量区域曝光
    fun homePageExposure(areaName: String, position: String, trafficName: String) {
        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_prePageUrl_var"] = "无"
        //流量区域
        map["fy_area_var"] = areaName
        //banner具体内容position
        map["fy_position_var"] = position
        //banner名称
        map["fy_trafficName_var"] = trafficName
        map["fy_operationPlanID_var"] = "无"
        map["fy_journeyID_var"] = "无"
        map["fy_controlID_var"] = "无"

        trackCustomEvent("fy_homePageExposure", map)
    }

    //导航流量区域点击
    fun homePageClick(areaName: String, position: String, trafficName: String) {
        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_prePageUrl_var"] = "无"
        //流量区域
        map["fy_area_var"] = areaName
        //banner具体内容position
        map["fy_position_var"] = position
        //banner名称
        map["fy_trafficName_var"] = trafficName
        map["fy_operationPlanID_var"] = "无"
        map["fy_journeyID_var"] = "无"
        map["fy_controlID_var"] = "无"

        trackCustomEvent("fy_homePageClick", map)
    }

    //资讯详情页面浏览
    fun infoDetailInfo() {
        val map = HashMap<String, String>()
        //上一级页面类型
        map["fy_prePageType_var"] = if (infoEntrance.isEmpty()) {
            "无"
        } else {
            GioPageConstant.mainTabName
        }
        //上一级页面名称
        map["fy_prePageName_var"] = if (infoEntrance.isEmpty()) {
            "无"
        } else {
            GioPageConstant.mainSecondPageName()
        }
        map["fy_prePageUrl_var"] = "无"
        //资讯入口
        map["fy_infoEntrance_var"] = infoEntrance
        //资讯所属专题
        map["fy_informationTheme_var"] = infoTheme.gioEmpty()
        //资讯id
        map["fy_informationID_var"] = infoId
        //资讯名称
        map["fy_informationName_var"] = infoName

        trackCustomEvent("fy_infoDetailView", map)
    }

    //资讯分享
    fun infoShareSuccess() {

        val map = HashMap<String, String>()

        //资讯入口
        map["fy_infoEntrance_var"] = infoEntrance
        //资讯所属专题
        map["fy_informationTheme_var"] = infoTheme.gioEmpty()
        //资讯id
        map["fy_informationID_var"] = GioPageConstant.infoId
        //资讯名称
        map["fy_informationName_var"] = GioPageConstant.infoName
        map["fy_shareChannel_var_var"] = GioPageConstant.infoShareType

        trackCustomEvent("fy_infoShareSuccess", map)
    }

    //热门话题列表浏览
    fun topicListPageView() {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"

        trackCustomEvent("fy_topicListPageView", map)
    }

    //话题详情浏览
    fun topicDetailPageView(topicId: String, topicName: String) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        //推荐、最新、精华
        map["fy_tabName_var"] = topicDetailTabName
        //话题详情入口
        map["fy_topicEntrance_var"] = topicEntrance
        //话题id
        map["fy_topicID_var"] = topicId
        //话题名称
        map["fy_topicName_var"] = topicName

        trackCustomEvent("fy_topicDetailPageView", map)
    }

    //帖子详情浏览
    fun postDetailPageView(
        topicId: String?,
        topicName: String?,
        posterId: String?,
        postId: String,
        postName: String,
        circleId: String?,
        circleName: String?
    ) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        //帖子入口
        map["fy_postEntrance_var"] = GioPageConstant.postEntrance
        //话题id
        map["fy_topicID_var"] = if (topicId.isNullOrEmpty()) {
            "无"
        } else topicId
        //话题名称
        map["fy_topicName_var"] = if (topicName.isNullOrEmpty()) {
            "无"
        } else topicName
        //发帖人id
        map["fy_posterID_var"] = if (posterId.isNullOrEmpty()) {
            "无"
        } else posterId
        //帖子id
        map["fy_postID_var"] = postId
        //帖子标题
        map["fy_postName_var"] = postName
        //圈子id
        map["fy_circleID_var"] = if (circleId.isNullOrEmpty()) {
            "无"
        } else circleId
        //圈子名称
        map["fy_circleName_var"] = if (circleName.isNullOrEmpty()) {
            "无"
        } else circleName

        trackCustomEvent("fy_postDetailPageView", map)
    }

    //资讯点赞
    fun infoLickClick(
        pageName: String,
        infoTheme: String?,
        infoId: String?,
        infoName: String?
    ) {

        val map = HashMap<String, String>()

        //发现-推荐
        //话题详情-推荐
        //话题详情-最新
        //话题详情-精华
        //圈子详情-推荐
        //圈子详情-最新
        //圈子详情-精华
        //圈子详情-圈主专区
        //社区-广场
        //帖子详情页
        //资讯详情页
        //发现-资讯
        //专题详情页
        //我的足迹-资讯
        //我的足迹-帖子
        //我的收藏-资讯
        //我的收藏-帖子
        //所在页面
        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "资讯"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = infoTheme.gioEmpty()
        //资讯id
        map["fy_informationID_var"] = infoId.gioEmpty()
        //资讯名称
        map["fy_informationName_var"] = infoName.gioEmpty()
        //话题id
        map["fy_topicID_var"] = "无"
        //话题名称
        map["fy_topicName_var"] = "无"
        //发帖人id
        map["fy_posterID_var"] = "无"
        //帖子id
        map["fy_postID_var"] = "无"
        //帖子标题
        map["fy_postName_var"] = "无"
        //圈子id
        map["fy_circleID_var"] = "无"
        //圈子名称
        map["fy_circleName_var"] = "无"

        trackCustomEvent("fy_likeClick", map)
    }

    //帖子点赞
    fun postLickClick(
        pageName: String,
        topicId: String?,
        topicName: String?,
        posterId: String?,
        postId: String?,
        postName: String?,
        circleId: String?,
        circleName: String?
    ) {

        val map = HashMap<String, String>()

        //所在页面
        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "帖子"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = "无"
        //资讯id
        map["fy_informationID_var"] = "无"
        //资讯名称
        map["fy_informationName_var"] = "无"
        //话题id
        map["fy_topicID_var"] = topicId.gioEmpty()
        //话题名称
        map["fy_topicName_var"] = topicName.gioEmpty()
        //发帖人id
        map["fy_posterID_var"] = posterId.gioEmpty()
        //帖子id
        map["fy_postID_var"] = postId.gioEmpty()
        //帖子标题
        map["fy_postName_var"] = postName.gioEmpty()
        //圈子id
        map["fy_circleID_var"] = circleId.gioEmpty()
        //圈子名称
        map["fy_circleName_var"] = circleName.gioEmpty()

        trackCustomEvent("fy_likeClick", map)
    }

    //资讯取消点赞
    fun cancelInfoLickClick(
        pageName: String,
        infoTheme: String?,
        infoId: String?,
        infoName: String?
    ) {

        val map = HashMap<String, String>()

        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "资讯"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = infoTheme.gioEmpty()
        //资讯id
        map["fy_informationID_var"] = infoId.gioEmpty()
        //资讯名称
        map["fy_informationName_var"] = infoName.gioEmpty()
        //话题id
        map["fy_topicID_var"] = "无"
        //话题名称
        map["fy_topicName_var"] = "无"
        //发帖人id
        map["fy_posterID_var"] = "无"
        //帖子id
        map["fy_postID_var"] = "无"
        //帖子标题
        map["fy_postName_var"] = "无"
        //圈子id
        map["fy_circleID_var"] = "无"
        //圈子名称
        map["fy_circleName_var"] = "无"

        trackCustomEvent("fy_cancelLikeClick", map)
    }

    //帖子取消点赞
    fun cancelPostLickClick(
        pageName: String,
        topicId: String?,
        topicName: String?,
        posterId: String?,
        postId: String?,
        postName: String?,
        circleId: String?,
        circleName: String?
    ) {

        val map = HashMap<String, String>()

        //所在页面
        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "帖子"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = "无"
        //资讯id
        map["fy_informationID_var"] = "无"
        //资讯名称
        map["fy_informationName_var"] = "无"
        //话题id
        map["fy_topicID_var"] = topicId.gioEmpty()
        //话题名称
        map["fy_topicName_var"] = topicName.gioEmpty()
        //发帖人id
        map["fy_posterID_var"] = posterId.gioEmpty()
        //帖子id
        map["fy_postID_var"] = postId.gioEmpty()
        //帖子标题
        map["fy_postName_var"] = postName.gioEmpty()
        //圈子id
        map["fy_circleID_var"] = circleId.gioEmpty()
        //圈子名称
        map["fy_circleName_var"] = circleName.gioEmpty()

        trackCustomEvent("fy_cancelLikeClick", map)
    }

    //资讯评论按钮点击
    fun clickCommentInfo(
        pageName: String,
        infoTheme: String?,
        infoId: String?,
        infoName: String?
    ) {

        val map = HashMap<String, String>()

        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "资讯"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = infoTheme.gioEmpty()
        //资讯id
        map["fy_informationID_var"] = infoId.gioEmpty()
        //资讯名称
        map["fy_informationName_var"] = infoName.gioEmpty()
        //话题id
        map["fy_topicID_var"] = "无"
        //话题名称
        map["fy_topicName_var"] = "无"
        //发帖人id
        map["fy_posterID_var"] = "无"
        //帖子id
        map["fy_postID_var"] = "无"
        //帖子标题
        map["fy_postName_var"] = "无"
        //圈子id
        map["fy_circleID_var"] = "无"
        //圈子名称
        map["fy_circleName_var"] = "无"

        trackCustomEvent("fy_commentClick", map)
    }

    //帖子评论按钮点击
    fun clickCommentPost(
        pageName: String,
        topicId: String?,
        topicName: String?,
        posterId: String?,
        postId: String?,
        postName: String?,
        circleId: String?,
        circleName: String?
    ) {

        val map = HashMap<String, String>()

        //所在页面
        map["fy_currentPageName_var"] = pageName
        //帖子、资讯
        map["fy_contentType_var"] = "帖子"
        //福域_资讯所属专题
        map["fy_informationTheme_var"] = "无"
        //资讯id
        map["fy_informationID_var"] = "无"
        //资讯名称
        map["fy_informationName_var"] = "无"
        //话题id
        map["fy_topicID_var"] = topicId.gioEmpty()
        //话题名称
        map["fy_topicName_var"] = topicName.gioEmpty()
        //发帖人id
        map["fy_posterID_var"] = posterId.gioEmpty()
        //帖子id
        map["fy_postID_var"] = postId.gioEmpty()
        //帖子标题
        map["fy_postName_var"] = postName.gioEmpty()
        //圈子id
        map["fy_circleID_var"] = circleId.gioEmpty()
        //圈子名称
        map["fy_circleName_var"] = circleName.gioEmpty()

        trackCustomEvent("fy_commentClick", map)
    }

    private fun String?.gioEmpty(): String {
        return if (this.isNullOrEmpty()) {
            "无"
        } else {
            this
        }
    }
}