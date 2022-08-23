package com.changanford.home.data

import android.text.TextUtils
import com.changanford.common.util.TimeUtils

class ActBean {
    val townName: String? = null
    val beginTime: Long = 0
    val deadLineTime: Long = 0
    val endTime: Long = 0
    val official: Int? = null
    val title: String? = null
    val userId = 0
    val wonderfulId = 0
    var wonderfulType: Int =
        0//精彩类型,可用值:WonderfulTypeEnum.ONLINE_ACTIVITY(code=0, message=线上活动, desc=线上活动, dbCode=0),WonderfulTypeEnum.OFFLINE_ACTIVITY(code=1, message=线下活动, desc=线下活动, dbCode=1),WonderfulTypeEnum.UNOFFICIAL(code=2, message=问卷调研, desc=问卷调研, dbCode=2),WonderfulTypeEnum.CAR_FAC_ACTIVITY(code=3, message=福域活动, desc=福域活动, dbCode=3),WonderfulTypeEnum.VOTE(code=4, message=投票活动, desc=投票活动, dbCode=4),WonderfulTypeEnum.QUES(code=5, message=问卷中心(0815), desc=问卷中心(0815), dbCode=5)
    val activityJoinCount: String? = null //参加活动人数
    val coverImg: String? = null
    val jumpType = 0
    val jumpVal: String? = null
    val serverTime: Long = 0
    val cityName: String = ""
    val provinceName: String = ""
    val recommend: String? = null
    val hot: String? = null
    var needSignUp: String? = "YES"
    fun getAddress(): String {
//            if (!TextUtils.isEmpty(cityName)) {
//                return cityName
//            }
//            if (TextUtils.isEmpty(provinceName)) {
//                return "未知"
//            }
//            return provinceName
        return "活动地点：$activityAddr"
    }

    fun getActTimeS(): String {
        return "活动时间: ".plus(TimeUtils.formateActTime(beginTime)).plus(" 至 ")
            .plus(TimeUtils.formateActTime(endTime))
    }

    fun getSignTimes(): String {
        return "报名截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }

    fun getEndTimeTips(): String {
        return "截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }

    var activityAddr: String = ""//活动详细地址(报名活动)
    var activityButton: String =
        ""//报名活动按钮,可用值:ActivityButtonEnum.SIGN_NOT_BEGIN(code=SIGN_NOT_BEGIN, dbCode=0, message=报名未开始),ActivityButtonEnum.SIGN_NOW(code=SIGN_NOW, dbCode=1, message=立即报名),ActivityButtonEnum.SIGN_FULL(code=SIGN_FULL, dbCode=2, message=报名已满),ActivityButtonEnum.SIGNED(code=SIGNED, dbCode=3, message=已报名),ActivityButtonEnum.SIGN_ENDED(code=SIGN_END, dbCode=4, message=报名结束),ActivityButtonEnum.MUNUAL_END(code=MUNUAL_END, dbCode=5, message=结束),ActivityButtonEnum.VIEW_RESULT(code=VIEW_RESULT, dbCode=6, message=查看结果)
    var activityTag: String =
        ""//activityTag	列表左上角标签,可用值:ActivityTagEnum.NOT_BEGIN(code=NOT_BEGIN, message=未开始),ActivityTagEnum.ON_GOING(code=ON_GOING, message=进行中),ActivityTagEnum.ENDED(code=ENDED, message=已结束),ActivityTagEnum.CHECKING(code=CHECKING, message=审核中),ActivityTagEnum.OFF_SHELF(code=OFF_SHELF, message=已下架),ActivityTagEnum.NOT_PASS(code=NOT_PASS, message=未通过)
    var activityTotalCount: String = ""//	活动总人数（报名活动,-1表示不限制）
    var jumpDto: JumpDTO = JumpDTO()
    var openTime: String = ""//	报名活动开始时间
    var outChain: String =
        ""//是否外链（若是 需回调接口/highlights/callBackOuterChain）,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    var reason: String = ""//拒绝原因
    var status: String =
        ""//status	状态（0-审核中、1-驳回、2-正常、3-下架）,可用值:WonderfulStatusEnum.CHECKING(code=0, dbCode=0, message=审核中),WonderfulStatusEnum.REJECT(code=1, dbCode=1, message=驳回),WonderfulStatusEnum.NORMAL(code=2, dbCode=2, message=正常),WonderfulStatusEnum.OFFLINE(code=3, dbCode=3, message=下架)
    var voteButton: String =
        ""//voteButton	投票按钮,可用值:VoteButtonEnum.VOTE_NOT_BEGIN(code=VOTE_NOT_BEGIN, dbCode=0, message=投票未开始),VoteButtonEnum.VOTE_NOW(code=VOTE_NOW, dbCode=1, message=立即投票),VoteButtonEnum.VOTED(code=VOTED, dbCode=2, message=已投票),VoteButtonEnum.VOTE_ENDED(code=VOTE_ENDED, dbCode=3, message=投票结束),VoteButtonEnum.MUNUAL_END(code=MUNUAL_END, dbCode=5, message=结束),VoteButtonEnum.VIEW_RESULT(code=VIEW_RESULT, dbCode=6, message=查看结果)

}

class FastBeanData(var ads: List<AdBean>, var showType: String = "") {

}

data class JumpDTO(
    var jumpCode: Int = 99,
    var jumpVal: String = "",
)