package com.changanford.common.bean

import java.io.Serializable

/**
 *  文件名：UserData
 *  创建者: zcy
 *  创建日期：2021/9/10 16:52
 *  描述: TODO
 *  修改描述：TODO
 */
data class UserInfoBean(
    val avatar: String,
    val bindName: String,
    val bindType: String,
    val birthday: Long,
    val brief: String,
    val city: Int,
    val cityName: String,
    val constellation: String,
    val count: Count,
    val createBy: String,
    val createTime: Long,
    val currentUser: Boolean,
    val dealerId: String,
    val delReason: String,
    val delTime: Long,
    val district: Int,
    val districtName: String,
    val email: String,
    val ext: Ext,
    val growSeriesName: String,
    val growthDecimal: String,
    val hasFeedbacks: Int,
    val hobbyIds: String,
    val hobbyNames: String,
    val industryIds: String,
    val industryNames: String,
    val integralDecimal: String,
    val inviterId: String,
    val isAuth: String,
    val isFollow: Int,
    val isLogin: Int,
    val isSignIn: Int,
    val isUnread: Int,
    val jpushRid: String,
    val lastLoginIp: String,
    val lastLoginTime: Long,
    val loginCount: Int,
    val medalName: String,
    val memberId: String,
    val memberShip: String,
    val mobile: String,
    val nickname: String,
    val password: String,
    val phone: String,
    val province: Int,
    val provinceName: String,
    val realName: String,
    val registerChannel: Int,
    val remark: String,
    val searchValue: String,
    val sex: Int,
    val status: Int,
    val updateBy: String,
    val updateTime: String,
    val userId: String,
    val userName: String
)

data class Count(
    val collections: Int,
    val fans: Int,
    val follows: Int,
    val likeds: Int,
    val releases: Int
)

data class Ext(
    val cumulativeIntegralDecimal: Int,
    val growSeriesId: Int,
    val growSeriesName: String,
    val growthDecimal: Int,
    val headFrameId: Int,
    val headFrameImage: String,
    val headFrameName: String,
    val imags: List<Imag>,
    val integralDecimal: Int,
    val medalId: Int,
    val memberIcon: String,
    val memberId: Int,
    val multiple: Double,
    val nextGrowSeriesName: String,
    val nextSeriesMinGrow: Int,
    val rulesDesc: String,
    val userId: Int,
    val userMedalList: ArrayList<MedalListBeanItem>? = arrayListOf(),
    val isAllTaskDone: Int,
    val isNewTaskDone: Int,
    val medalImage: String,
    val medalName: String,
    val memberName: String,
)

data class Imag(
    val img: String,
    val jumpDataType: Int,
    val jumpDataValue: String
)


data class FansListBean(
    val dataList: List<FansItemBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class FansItemBean(
    val authorId: String,
    val avatar: String,
    val isMutualAttention: Int,//是否互相关注 1 是，0 否
    val nickname: String,
    val isFollow: Int //他人的粉丝/关注是否被登录用户关注 1 是，0 否
)

data class BindAuthBeanItem(
    val bind: Boolean,
    val img: String,
    val name: String,
    val type: String
)

data class CancelVerifyBean(
    val condition: String,
    val conditionDesc: String,
    val isFinish: Int,
    val jumpDataType: Int,
    val jumpDataValue: String
)

data class CancelReasonBeanItem(
    val dictLabel: String,
    var isCheck: Boolean = false,
    var reasonId: Int = 0
)

data class UserIdCardBeanItem(
    val actionIds: String,
    val dataScope: String,
    val fillCondition: String,
    val interestsId: String,
    val isAuth: String,
    var isShow: String,
    val keepCondition: String,
    val keepConditionNo: String,
    val memberDesc: String,
    val memberIcon: String,
    val memberId: Int,
    val memberKey: String,
    val memberName: String,
    val sort: String,
    val status: String,
    val medalType: Int//勋章类型（1.身份 2.成长值 3社交互动 4每日动态 5节日）
)

data class AddressBeanItem(
    val addressId: Int = 0,
    val addressName: String,
    val city: String,
    val cityName: String,
    val consignee: String,
    val district: String,
    val districtName: String,
    var isDefault: Int,
    val phone: String,
    val province: String,
    val provinceName: String,
    val status: Int,
    val userId: Int
) : Serializable