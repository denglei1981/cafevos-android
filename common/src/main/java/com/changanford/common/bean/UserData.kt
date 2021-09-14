package com.changanford.common.bean

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
    val multiple: Int,
    val nextGrowSeriesName: String,
    val nextSeriesMinGrow: Int,
    val rulesDesc: String,
    val userId: Int
)

data class Imag(
    val img: String,
    val jumpDataType: Int,
    val jumpDataValue: String
)