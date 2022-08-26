package com.changanford.common.bean

import android.os.Parcelable
import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.changanford.common.util.CountUtils
import com.changanford.common.util.TimeUtils
import kotlinx.android.parcel.Parcelize

/*********活动新返回**********/
class ActBean {
    val jumpType = 0
    val jumpVal: String? = null
    val serverTime: Long = 0
    fun getSignTimes(): String {
        return "报名截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }

    fun getEndTimeTips(): String {
        return "截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }


    fun getAddress(): String {
//            if (!TextUtils.isEmpty(cityName)) {
//                return cityName
//            }
//            if (TextUtils.isEmpty(provinceName)) {
//                return "未知"
//            }
//            return provinceName
        var addr = "$activityAddr"
        if (!activityAddr.isNullOrEmpty()&& !provinceName.isNullOrEmpty()){
            if (activityAddr?.contains(provinceName) == false){
                addr = "$provinceName$activityAddr"
            }
        }
        return "活动地点: $addr"
    }
    fun getActTimeS(): String {
        return "活动时间: ".plus(TimeUtils.formateActTime(beginTime)).plus(" - ")
            .plus(TimeUtils.formateActTime(endTime))
    }
    var activityAddr: String? = ""//活动详细地址(报名活动)
    var activityButton: String? =
        ""//报名活动按钮,可用值:ActivityButtonEnum.SIGN_NOT_BEGIN(code=SIGN_NOT_BEGIN, dbCode=0, message=报名未开始),ActivityButtonEnum.SIGN_NOW(code=SIGN_NOW, dbCode=1, message=立即报名),ActivityButtonEnum.SIGN_FULL(code=SIGN_FULL, dbCode=2, message=报名已满),ActivityButtonEnum.SIGNED(code=SIGNED, dbCode=3, message=已报名),ActivityButtonEnum.SIGN_ENDED(code=SIGN_END, dbCode=4, message=报名结束),ActivityButtonEnum.MUNUAL_END(code=MUNUAL_END, dbCode=5, message=结束),ActivityButtonEnum.VIEW_RESULT(code=VIEW_RESULT, dbCode=6, message=查看结果)
    val activityJoinCount: String? = "" //参加活动人数
    var activityTag: String? =
        ""//,,ActivityTagEnum.NOT_PASS(code=NOT_PASS, message=未通过)
    fun showTag():String{
        return if (activityTag.isNullOrEmpty()) "" else {
            when(activityTag){
                "NOT_BEGIN"-> "未开始"
                "ON_GOING"-> "进行中"
                "ENDED"-> "已结束"
                "CHECKING"-> "审核中"
                "OFF_SHELF"-> "已下架"
                "NOT_PASS"-> "未通过"
                else -> ""
            }
        }
    }
    var activityTotalCount: String = ""//	活动总人数（报名活动,-1表示不限制）
    val beginTime: Long = 0//活动开始时间
    val cityName: String = ""//城市名称
    val coverImg: String = ""//封面图片
    val deadLineTime: Long = 0//	报名活动截止时间
    val endTime: Long = 0//活动结束时间
    val hot: String = ""//是否热门,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    var jumpDto: JumpDTO = JumpDTO()
    var needSignUp: String? = "YES"//是否报名（YES/NO），如果是NO则隐藏按钮（0830版本之后取消非报名常规活动）
    val official: String? = ""//0-官方，1-个人，2-经销商,可用值:WonderfulOfficialEnum.OFFICIAL(code=0, dbCode=0, message=官方),WonderfulOfficialEnum.UNOFFICIAL(code=1, dbCode=1, message=个人),WonderfulOfficialEnum.DISTRIBUTOR(code=2, dbCode=2, message=经销商)
    var openTime: String? = ""//	报名活动开始时间
    var outChain: String? =
        ""//是否外链（若是 需回调接口/highlights/callBackOuterChain）,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    val provinceName: String = ""//省名称
    var reason: String = ""//拒绝原因
    val recommend: String = ""//是否推荐,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    var status: String? =
        ""//status	状态（0-审核中、1-驳回、2-正常、3-下架）,可用值:WonderfulStatusEnum.CHECKING(code=0, dbCode=0, message=审核中),WonderfulStatusEnum.REJECT(code=1, dbCode=1, message=驳回),WonderfulStatusEnum.NORMAL(code=2, dbCode=2, message=正常),WonderfulStatusEnum.OFFLINE(code=3, dbCode=3, message=下架)
    val title: String = ""//	标题
    val townName: String = ""//	区域名称
    val userId = 0//	发布者id
    var voteButton: String? =
        ""//voteButton	投票按钮,可用值:VoteButtonEnum.VOTE_NOT_BEGIN(code=VOTE_NOT_BEGIN, dbCode=0, message=投票未开始),VoteButtonEnum.VOTE_NOW(code=VOTE_NOW, dbCode=1, message=立即投票),VoteButtonEnum.VOTED(code=VOTED, dbCode=2, message=已投票),VoteButtonEnum.VOTE_ENDED(code=VOTE_ENDED, dbCode=3, message=投票结束),VoteButtonEnum.MUNUAL_END(code=MUNUAL_END, dbCode=5, message=结束),VoteButtonEnum.VIEW_RESULT(code=VIEW_RESULT, dbCode=6, message=查看结果)
    val wonderfulId = 0//	精彩id
    var wonderfulType: Int =
        0//精彩类型,可用值:WonderfulTypeEnum.ONLINE_ACTIVITY(code=0, message=线上活动, desc=线上活动, dbCode=0),WonderfulTypeEnum.OFFLINE_ACTIVITY(code=1, message=线下活动, desc=线下活动, dbCode=1),WonderfulTypeEnum.UNOFFICIAL(code=2, message=问卷调研, desc=问卷调研, dbCode=2),WonderfulTypeEnum.CAR_FAC_ACTIVITY(code=3, message=福域活动, desc=福域活动, dbCode=3),WonderfulTypeEnum.VOTE(code=4, message=投票活动, desc=投票活动, dbCode=4),WonderfulTypeEnum.QUES(code=5, message=问卷中心(0815), desc=问卷中心(0815), dbCode=5)
    var total = 0//总记录数
    fun showButton():Boolean{
        return activityButton?.isNotEmpty() == true || voteButton?.isNotEmpty() == true
    }
    fun isFinish():Boolean{
        return  if (!activityButton.isNullOrEmpty()||!voteButton.isNullOrEmpty()){
            activityButton == "MUNUAL_END" || voteButton == "MUNUAL_END"
        } else false
    }
    fun buttonBgEnable():Boolean{
        return if (!activityButton.isNullOrEmpty()){
            when(activityButton){
                "SIGN_NOT_BEGIN" -> false
                "SIGN_NOW" -> true
                "SIGN_FULL" -> false
                "SIGNED" -> false
                "SIGN_END" -> false
                "MUNUAL_END" -> true
                "VIEW_RESULT" -> true
                else -> false
            }
        } else if(!voteButton.isNullOrEmpty()){
            when(voteButton){
                "VOTE_NOT_BEGIN" -> false
                "VOTE_NOW" -> true
                "VOTED" -> false
                "VOTE_ENDED" -> false
                "MUNUAL_END" -> true
                "VIEW_RESULT" -> true
                else -> false
            }
        } else {
            false
        }
    }
    fun showButtonText():String{
        return if (!activityButton.isNullOrEmpty()) {
            when(activityButton){
                "SIGN_NOT_BEGIN" -> "报名未开始"
                "SIGN_NOW" -> "立即报名"
                "SIGN_FULL" -> "报名已满"
                "SIGNED" -> "已报名"
                "SIGN_END" -> "报名结束"
                "MUNUAL_END" -> "结束"
                "VIEW_RESULT" -> "查看结果"
                else -> ""
            }
        } else if (!voteButton.isNullOrEmpty()){
            when(voteButton){
                "VOTE_NOT_BEGIN" -> "投票未开始"
                "VOTE_NOW" -> "立即投票"
                "VOTED" -> "已投票"
                "VOTE_ENDED" -> "投票结束"
                "MUNUAL_END" -> "结束"
                "VIEW_RESULT" -> "查看结果"
                else -> ""
            }
        } else {
            ""
        }
    }
}

class FastBeanData(var ads: List<AdBean>, var showType: String = "") {

}

data class JumpDTO(
    var jumpCode: Int = 99,
    var jumpVal: String = "",
)

data class UpdateActivityV2Req(
    var dto:DtoBeanNew,
    var wonderfulId:Int
)

data class UpdateVoteReq(
    var addVoteDto:VoteBean,
    var wonderfulId:Int

)
/*********************************************************************/




/**
 *  文件名：ActBean
 *  创建者: zcy
 *  创建日期：2021/9/29 9:44
 *  描述: TODO
 *  修改描述：TODO
 */
data class InfoBean(
    val dataList: List<InfoDataBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class InfoDataBean(
    override val itemType: Int,
    val artId: String = "",
    var authors: AuthorBaseVo? = null,
    val catId: Int = 0,
    val collectCount: Int = 0,
    var commentCount: Long = 0L,
    val content: String = "",
    val createTime: Long = 0L,
    val isDeleted: Int = 0,
    var isLike: Int = 0,
    val isRecommend: Int = 0,
    val isSpecialTopic: Int = 0,
    val keyword: String = "",
    var likesCount: Long = 0L,
    val likesCountBase: Int = 0,
    val likesCountMul: Double = 0.0,
    val pics: String = "",
    val publishTime: Long = 0L,
    val shareCount: Int = 0,
    val sortOrder: Int = 0,
    val specialTopicId: Any = Any(),
    val specialTopicTitle: String = "",
    val status: Int = 0,
    val summary: String = "",
    val timeStr: String = "",
    val title: String = "",
    val type: Int = 0,//资讯类型 1图文 2 图片 3 视频
    val updateTime: String = "",
    val userId: String = "",
    val videoTime: String = "",
    val videoUrl: String = "",
    val viewsCount: Long = 0L,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Double = 0.0,
    val jumpVal: String = "",
    val jumpType: Int = 0
) : MultiItemEntity {

    fun getCommentCountResult(): String {
        var commentCountResult: String = ""
        if (commentCount == 0L) {
            return "评论"
        }
        commentCountResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCountResult
    }

    fun getCommentCountAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("评论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getCommentDiscussAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("讨论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getContentStr(): String {
        if (!TextUtils.isEmpty(content)) {
            return content
        }
        if (!TextUtils.isEmpty(summary)) {
            return summary
        }
        return ""
    }

    fun getPicCover(): String { // 获取封面。
        if (!TextUtils.isEmpty(pics)) {
            val asList = pics.split(",")
            return asList[0]
        }
        return ""
    }

    fun getSubTitleStr(): String {
        return if (!TextUtils.isEmpty(specialTopicTitle)) {
            "#".plus(specialTopicTitle).plus("#")
        } else {
            summary
        }
    }

    var timeAndViewCountResult: String = ""
    fun getTimeAdnViewCount(): String {
        val viewCountStr = CountUtils.formatNum(viewsCount.toString(), false).toString()
        timeAndViewCountResult = timeStr.plus("  ").plus(viewCountStr).plus("浏览")
        return timeAndViewCountResult
    }
}

@Parcelize
data class AuthorBaseVo(
    val authorId: String,
    val avatar: String = "",
    val imags: ArrayList<Imag> = arrayListOf(),
    var isFollow: Int = 0, //1 是已关注
    val memberIcon: String = "",
    val memberId: Int = 0,
    val memberName: String = "",
    val nickname: String = "",
    // 一下三个参数是用户搜索的。
    val headFrameName: String = "",
    val headFrameImage: String = "",
    val userId: String = "",
    var carOwner: String = ""
): Parcelable {
    fun getMemberNames(): String {
        return if (TextUtils.isEmpty(carOwner)) {
            ""
        } else {
            carOwner
        }
    }

    fun showSubtitle(): Boolean {
        if (TextUtils.isEmpty(carOwner)) {
            return false
        }
        return true
    }


}

data class PostBean(
    val dataList: ArrayList<PostDataBean> = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class PostDataBean(
    val authorBaseVo: AuthorBaseVo? = null,
    val circleId: Int? = 0,
    val circle: PostCircleDataBean? = null,
    var itemImgHeight: Int = 0,
    val collectCount: Int = 0,
    var commentCount: Long = 0,
    val content: String = "",
    val firstComment: FirstCommentData? = null,
    val contentLike: Any? = Any(),
    val createBy: Any? = Any(),
    val createTime: Long = 0,
    val isCheck: Int = 0,
    val isDeleted: Int = 0,
    val isGood: Int = 0,//是否加精 1：加精，2：不加精, 3: 加精审核中
    val isHot: Int = 0,
    var isLike: Int = 0,
    val isPrivate: Int = 0,
    val isPublish: String = "",
    val isRecommend: Int = 0,
    val isTop: Int = 0,
    val keywords: String = "",
    var likesCount: Long = 0,
    val likesCountBase: Int = 0,
    val likesCountMul: Int = 0,
    val picCount: Int = 0,
    val pics: String = "",
    val plate: Int = 0,
    val postsId: Int = 0,
    val publishTime: Any? = Any(),
    val rejectReason: Any? = Any(),
    val remark: Any? = Any(),
    val searchValue: Any? = Any(),
    var shareCount: Int = 0,
    val sortOrder: Any? = Any(),
    val status: Int = 0,
    val timeStr: String = "",
    val title: String? = "",
    val city: String? = "",
    val topicName: String? = "",
    val topTime: Any? = Any(),
    val topicId: Int? = 0,
    val type: Int = 0,
    val updateBy: Any? = Any(),
    val updateTime: Long = 0,
    val userId: Int = 0,
    val videoTime: Any? = Any(),
    val videoUrl: Any? = Any(),
    val shares: CircleShareBean? = null,
    val viewsCount: Long = 0,
    val viewsCountBase: Int = 0,
    val viewsCountMul: Int = 0,
    val picList: List<String>? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val address: String = "",
    var tags: MutableList<PostKeywordBean>? = null,
    var addrName: String? = ""
) {
    fun getCommentCountAnViewCount(): String {
        val commentStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("评论")
        val viewStr = CountUtils.formatNum(commentCount.toString(), false).toString().plus("阅读")
        return commentStr.plus("\t").plus(viewStr)
    }

    fun getCommentCountResult(): String {
        var commentCountResult: String = ""
        if (commentCount == 0L) {
            return "评论"
        }
        commentCountResult = CountUtils.formatNum(commentCount.toString(), false).toString()
        return commentCountResult
    }

    fun getShareCountResult(): String {
        var commentCountResult: String = ""
        if (shareCount == 0) {
            return "分享"
        }
        commentCountResult = CountUtils.formatNum(shareCount.toString(), false).toString()
        return commentCountResult
    }

    fun getContentStr(): String {
        if (!TextUtils.isEmpty(content)) {
            return content
        }
        return ""
    }

    fun getShowTitle(): String {
        if (!TextUtils.isEmpty(title)) {
            return title!!
        }
        return "12232313123"
    }

    var timeAndViewCountResult: String = ""
    fun getTimeAdnViewCount(): String {
        val viewCountStr = CountUtils.formatNum(viewsCount.toString(), false).toString()
        timeAndViewCountResult = timeStr.plus("  ").plus(viewCountStr).plus("浏览")
        return timeAndViewCountResult
    }

    fun getPicsList(): List<String> {
        if (!TextUtils.isEmpty(pics)) {
            val asList = pics.split(",")
            return asList
        }
        return mutableListOf()
    }

    fun showCity(): String {
        if (addrName?.isNotEmpty() == true) {
            return city.plus("·").plus(addrName)
        }
        if (!TextUtils.isEmpty(city)) {
            return city!!
        }
        return ""
    }


}

data class AcBean(var title: String, var iconUrl: String, var type: Int) : MultiItemEntity {
    override val itemType: Int
        get() = type
}

data class FirstCommentData(
    val bizId: String,
    val content: String,
    val nickname: String,
    val avatar: String,
    val userId: String
)

data class AccBean(
    val dataList: List<ActBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class ActDataBean(
    val activityJoinCount: Int = 0,
    val activityTotalCount: Int = 0,
    val beginTime: String = "",
    val cityName: String = "",
    val coverImg: String = "",
    val deadLineTime: Long = 0L,
    val endTime: Long = 0L,
    val jumpType: Int = 0,
    val jumpVal: String = "",
    val official: Int = 0,
    val provinceName: String = "",
    val reason: String = "",
    val status: Int = 0,
    val title: String = "",
    val townName: String = "",
    val userId: Int = 0,
    val wonderfulId: Int = 0,
    val wonderfulType: Int = 0,
    var serverTime: Long = 0L,
    val createTime: Long = 0L,
    val browseCount: Long = 0L
)

data class LocationLotLon(val lat: Double, val lon: Double, val realCity: String)


data class CreateLocation(
    val address: String,
    val province: String,
    val addrName: String,
    val lat: Double,
    val lon: Double,
    val city: String,
)

data class PostKeywordBean(
    val createTime: Long,
    val id: String,
    val status: Int,
    val tagName: String,
    val tagNameLike: Any,
    val type: Int,
    var isselect: Boolean = false
)

/**
 * 圈子的加入状态 1:未加入 2:加入中 3:已加入
TOJOIN("TOJOIN",1, "未加入"),
PENDING("PENDING",2, "加入中(待审核)"),
JOINED("JOINED",3, "已加入")
 */
data class PostCircleDataBean(
    val circleId: String,
    val name: String,
    var isJoin: String,
    val pic: String,
    val starName: String?
)

data class BackEnumBean(var code: String, var message: String) {

}

data class CircleShareBean(
    val bizId: String,
    val shareDesc: String,
    val shareImg: String,
    val shareTitle: String,
    val shareUrl: String,
    val type: String,
    val wxminiprogramCode: String
)