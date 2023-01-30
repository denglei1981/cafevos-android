package com.changanford.common.util.gio

import com.changanford.common.bean.UserInfoBean
import com.changanford.common.util.MConstant
import com.changanford.common.util.gio.GIOUtils.gioEmpty
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
        map["fy_province_ppl"] = userInfoBean.provinceName.gioEmpty()
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
    fun homePageClick(areaName: String, position: String, trafficName: String?) {
        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_prePageUrl_var"] = "无"
        //流量区域
        map["fy_area_var"] = areaName
        //banner具体内容position
        map["fy_position_var"] = position
        //banner名称
        map["fy_trafficName_var"] = trafficName.gioEmpty()
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
        map["fy_informationID_var"] = infoId
        //资讯名称
        map["fy_informationName_var"] = infoName
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

    //资讯评论成功
    fun commentSuccessInfo(
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

        trackCustomEvent("fy_commentSuccess", map)
    }

    //帖子评论成功
    fun commentSuccessPost(
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

        trackCustomEvent("fy_commentSuccess", map)
    }

    //帖子收藏成功
    fun collectSuccessPost(
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

        trackCustomEvent("fy_collectClick", map)
    }

    //资讯收藏成功
    fun collectSuccessInfo(
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

        trackCustomEvent("fy_collectClick", map)
    }

    //帖子取消收藏成功
    fun cancelCollectSuccessPost(
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

        trackCustomEvent("fy_cancelCollectClick", map)
    }

    //资讯取消收藏成功
    fun cancelCollectSuccessInfo(
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

        trackCustomEvent("fy_cancelCollectClick", map)
    }

    //关注账号
    fun followClick(userId: String?, userNick: String?, pageName: String) {

        val map = HashMap<String, String>()

        //账号id
        map["fy_userID_var"] = userId.gioEmpty()
        //账号昵称
        map["fy_userNick_var"] = userNick.gioEmpty()
        //所在页面
        map["fy_currentPageName_var"] = pageName

        trackCustomEvent("fy_followClick", map)
    }

    //取消关注账号
    fun cancelFollowClick(userId: String?, userNick: String?, pageName: String) {

        val map = HashMap<String, String>()

        //账号id
        map["fy_userID_var"] = userId.gioEmpty()
        //账号昵称
        map["fy_userNick_var"] = userNick.gioEmpty()
        //所在页面
        map["fy_currentPageName_var"] = pageName

        trackCustomEvent("fy_followCancel", map)
    }

    //个人主页曝光
    fun personalHomepageView(userId: String?, nickName: String?) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        map["fy_userID_var"] = userId.gioEmpty()
        map["fy_userNick_var"] = nickName.gioEmpty()

        trackCustomEvent("fy_personalHomepageView", map)
    }

    //热门榜单详情曝光
    fun hotCircleDetailPageView(tabName: String?) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        //社区-圈子-热门榜单-更多
        //社区-圈子-热门榜单-车友会-更多
        //社区-圈子-热门榜单-车型圈-更多
        //社区-圈子-热门榜单-车生活-更多
        map["fy_hotCircleEntrance_var"] = GioPageConstant.hotCircleEntrance
        map["fy_tabName_var"] = tabName.gioEmpty()

        trackCustomEvent("fy_hotCircleDetailPageView", map)
    }

    //圈子详情曝光
    fun circleDetailPageView(circleId: String?, circleName: String) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        map["fy_circleID_var"] = circleId.gioEmpty()
        map["fy_circleName_var"] = circleName.gioEmpty()

        trackCustomEvent("fy_circleDetailPageView", map)
    }

    //圈子详情资源位点击
    fun circleDetailPageResourceClick(areaName: String, position: String, trafficName: String?) {

        val map = HashMap<String, String>()

        //流量位区域
        map["fy_area_var"] = areaName.gioEmpty()
        map["fy_position_var"] = position.gioEmpty()
        map["fy_trafficName_var"] = trafficName.gioEmpty()

        trackCustomEvent("fy_circleDetailPageResourceClick", map)
    }

    //加入圈子点击
    fun joinCircleClick(pageName: String, circleId: String?, circleName: String?) {

        val map = HashMap<String, String>()

        //流量位区域
        map["fy_currentPageName_var"] = pageName
        map["fy_circleID_var"] = circleId.gioEmpty()
        map["fy_circleName_var"] = circleName.gioEmpty()

        trackCustomEvent("fy_joinCircleClick", map)
    }

    //提问页面曝光
    fun askPageView() {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        map["fy_sourceEntrance_var"] = GioPageConstant.askSourceEntrance

        trackCustomEvent("fy_askPageView", map)
    }

    //提问按钮点击
    fun questionPublishClick(askName: String, askType: String?, askFB: String?) {

        val map = HashMap<String, String>()

        //问答标题
        map["fy_questionName_var"] = askName.gioEmpty()
        //问答类型
        map["fy_questionType_var"] = askType.gioEmpty()
        //打赏福币
        map["fy_fbReward_var"] = askFB.gioEmpty() + "福币"

        trackCustomEvent("fy_questionPublishClick", map)
    }

    //提问发布成功
    fun questionPublishSuccess(askName: String, askType: String?, askFB: String?, askId: String?) {

        val map = HashMap<String, String>()

        //问答id
        map["fy_questionID_var"] = askId.gioEmpty()
        //问答标题
        map["fy_questionName_var"] = askName.gioEmpty()
        //问答类型
        map["fy_questionType_var"] = askType.gioEmpty()
        //打赏福币
        map["fy_fbReward_var"] = askFB.gioEmpty()

        trackCustomEvent("fy_questionPublishSuccess", map)
    }

    //商品详情页浏览
    fun productDetailPageView(
        spuId: String?,
        skuId: String?,
        name: String?,
        price: String?,
        fbPrice: String?,
        isSeckill: String
    ) {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"
        map["fy_productSpuID_var"] = spuId.gioEmpty()
        map["fy_productSkuID_var"] = skuId.gioEmpty()
        map["fy_productName_var"] = name.gioEmpty()
        map["fy_productPrice_var"] = price.gioEmpty()
        map["fy_productFbPrice_var"] = fbPrice.gioEmpty()
        map["fy_ifSeckill_var"] = isSeckill

        trackCustomEvent("fy_productDetailPageView", map)
    }

    //加入购物车成功
    fun productAddToCart(
        spuId: String?,
        skuId: String?,
        name: String?,
        price: String?,
        fbPrice: String?,
        isSeckill: String,
        productNumber: String?
    ) {

        val map = HashMap<String, String>()

        map["fy_productSpuID_var"] = spuId.gioEmpty()
        map["fy_productSkuID_var"] = skuId.gioEmpty()
        map["fy_productName_var"] = name.gioEmpty()
        map["fy_productPrice_var"] = price.gioEmpty()
        map["fy_productFbPrice_var"] = fbPrice.gioEmpty()
        map["fy_ifSeckill_var"] = isSeckill
        map["fy_productNumber_var"] = productNumber.gioEmpty()

        trackCustomEvent("fy_ProductAddToCart", map)
    }

    //点击立即兑换
    fun exchangeCtaClick(
        spuId: String?,
        skuId: String?,
        name: String?,
        price: String?,
        fbPrice: String?,
        isSeckill: String,
        productNumber: String?
    ) {
        val map = HashMap<String, String>()

        map["fy_productSpuID_var"] = spuId.gioEmpty()
        map["fy_productSkuID_var"] = skuId.gioEmpty()
        map["fy_productName_var"] = name.gioEmpty()
        map["fy_productPrice_var"] = price.gioEmpty()
        map["fy_productFbPrice_var"] = fbPrice.gioEmpty()
        map["fy_ifSeckill_var"] = isSeckill
        map["fy_productNumber_var"] = productNumber.gioEmpty()

        trackCustomEvent("fy_exchangeCtaClick", map)
    }

    //商品兑换成功
    fun productOrderCreate(
        spuId: String?,
        skuId: String?,
        name: String?,
        price: String?,
        fbPrice: String?,
        isSeckill: String,
        productNumber: String?,
        mainOrderId: String?
    ) {

        val map = HashMap<String, String>()

        map["fy_productSpuID_var"] = spuId.gioEmpty()
        map["fy_productSkuID_var"] = skuId.gioEmpty()
        map["fy_productName_var"] = name.gioEmpty()
        map["fy_productPrice_var"] = price.gioEmpty()
        map["fy_productFbPrice_var"] = fbPrice.gioEmpty()
        map["fy_ifSeckill_var"] = isSeckill
        map["fy_productNumber_var"] = productNumber.gioEmpty()
        map["fy_mainOrderId_var"] = mainOrderId.gioEmpty()

        trackCustomEvent("fy_productOrderCreate", map)
    }

    // 兑换订单提交成功
    fun orderCreate(
        orderId: String?,
        priceAll: String?,
        fbPrice: String?,
        productNumber: String?,
        ifCoupon: String,
        couponId: String?,
        couponDeduct: String?,
        couponName: String?
    ) {

        val map = HashMap<String, String>()

        //主订单号
        map["fy_mainOrderId_var"] = orderId.gioEmpty()
        //商品价格总额
        map["fy_productPriceAll_var"] = priceAll.gioEmpty()
        //商品福币总量
        map["fy_productFbPriceAll_var"] = fbPrice.gioEmpty()
        //商品数量
        map["fy_productNumber_var"] = productNumber.gioEmpty()
        //是否使用优惠券
        map["fy_ifCoupon_var"] = ifCoupon
        //优惠券id
        map["fy_couponId_var"] = couponId.gioEmpty()
        //优惠券抵扣金额
        map["fy_couponDeduct_var"] = couponDeduct.gioEmpty()
        //优惠券名称
        map["fy_couponName_var"] = couponName.gioEmpty()

        trackCustomEvent("fy_orderCreate", map)
    }

    // 任务中心浏览
    fun taskCenterPageView() {

        val map = HashMap<String, String>()

        map["fy_pageName_var"] = GioPageConstant.mainSecondPageName()
        map["fy_pageType_var"] = GioPageConstant.mainTabName
        map["fy_pageUrl_var"] = "无"

        trackCustomEvent("fy_taskCenterPageView", map)
    }

    //任务中心点击
    fun taskCenterCtaClick(ctaName: String, fbNumber: String?, taskName: String?) {

        val map = HashMap<String, String>()

        //立即签到、去完成
        map["fy_ctaName_var"] = ctaName
        //赚取福币数量
        map["fy_earnFbNumber_var"] = fbNumber.gioEmpty()
        //任务名称
        map["fy_taskName_var"] = taskName.gioEmpty()

        trackCustomEvent("fy_taskCenterCtaClick", map)
    }

    //消息列表点击
    fun newsClick(title: String?) {
        val map = HashMap<String, String>()

        map["fy_journeyID_var"] = "无"
        map["fy_controlID_var"] = "无"
        map["fy_newName_var"] = title.gioEmpty()

        trackCustomEvent("fy_newsClick", map)
    }

    //车型点击
    fun carClick(carName: String?, carType: String?) {
        val map = HashMap<String, String>()

        map["fy_currentPageName_var"] = "爱车页"
        map["fy_carName_var"] = carName.gioEmpty()
        map["fy_carType_var"] = carType.gioEmpty()

        trackCustomEvent("fy_carClick", map)
    }

    private fun String?.gioEmpty(): String {
        return if (this.isNullOrEmpty()) {
            "无"
        } else {
            this
        }
    }
}