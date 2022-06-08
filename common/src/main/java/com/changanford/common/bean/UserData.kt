package com.changanford.common.bean

import android.text.TextUtils
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
    var brief: String,
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
    val industryIds: String? = "",
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
    var nickname: String,
    val password: String,
    val phone: String,
    val province: Int,
    val provinceName: String,
    val realName: String,
    val registerChannel: Int,
    val remark: String,
    val searchValue: String,
    val sex: Int,
    var status: Int,
    val updateBy: String,
    val updateTime: String,
    val userId: String,
    val userName: String,
    var couponCount:Int=0,
    var medalCount:Int=0,
    val userMedalList: ArrayList<MedalListBeanItem>? = arrayListOf(),
) {
    /**
     * 处理因is开头属性转json丢失数据的问题
     */
    fun getIsAuth() = isAuth
    fun getIsFollow() = isFollow
    fun getIsLogin() = isLogin
    fun getIsSignIn() = isSignIn
    fun getIsUnread() = isUnread
}

data class Count(
    var collections: Int=0,
    var fans: Int=0,
    var follows: Int=0,
    var likeds: Int=0,
    var releases: Int=0
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
    var integralDecimal: Int? = 0,
    val medalId: Int,
    val memberIcon: String,
    val memberId: Int,
    val multiple: Double,
    val nextGrowSeriesName: String,
    val nextSeriesMinGrow: Long,
    val rulesDesc: String,
    val userId: Int,
    val userMedalList: ArrayList<MedalListBeanItem>? = arrayListOf(),
    val isAllTaskDone: Int,
    val isNewTaskDone: Int,
    val medalImage: String,
    val medalName: String? = "",
    val memberName: String,
    val totalIntegral: String,
    val totalGrowth: Long,
    val growSeriesMaxGrow: String,
    val carOwner: String = ""
) {
    fun getCarOwnerEmpty(): Boolean {
        return TextUtils.isEmpty(carOwner)
    }
}

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
    var isMutualAttention: Int,//是否互相关注 1 是，0 否
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

data class QuestionData(
    val dictLabel: String,
    var isCheck: Boolean = false,
    var reasonId: Int = 0,
    var dictCode: Int = 0,
    var dictValue: String = ""
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
) : Serializable{
    fun getAddress(): String {
        return "$provinceName$cityName$districtName$addressName"
    }
    fun getUserInfo():String{
        return consignee.plus("\t"+phone)
    }
}

data class AuthBean(
    val conditionList: List<Condition>? = arrayListOf(),
    val fillCondition: String = "",
    val images: ArrayList<AuthImagesItem>? = arrayListOf(),
    val interestsId: String = "",
    val interestsList: List<Interests> = arrayListOf(),
    val keepCondition: String = "",
    val keepConditionNo: String = "",
    val memberDesc: String = "",
    val memberIcon: String = "",
    val memberId: Int = 0,
    val memberKey: String = "",
    val memberName: String = "",
    var auditStatus: String = "",// 0 待审 1 审核通过 2 审核不通过
    val reason: String = ""
)

data class Condition(
    val conditionId: Int = 0,
    val conditionKey: String = "",
    val conditionName: String = "",
    val createTime: String = "",
    val isFinish: String = "",
    val jumpDataType: Int = 0,
    val jumpDataValue: String = "",
    val noConditionName: String = "",
    val num: Int = 0,
    val sort: Int = 0,
    val status: Int = 0
)

data class Interests(
    val createBy: String = "",
    val createTime: String = "",
    val desc: String = "",
    val icon: String = "",
    val interestsId: Int = 0,
    val interestsName: String = "",
    val interestsType: Int = 0,
    val remark: String = "",
    val searchValue: String = "",
    val status: Int = 0,
    val updateBy: String = "",
    val updateTime: String = ""
)


data class AuthImagesItem(
    val imgPosition: Int = 0,//图片位置（1名称资料证明 2个人展示照片）
    val imgUrls: List<String> = arrayListOf()
)

data class ShopBean(
    val dataList: List<MyShopBean>? = arrayListOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class MyShopBean(
    val imageUrl: String = "",
    val spuName: String = "",
    val normalFb: String = "0",
    val count: String = "0",
    val spuImgs: String = "",
    val mallMallSpuId: String
)

data class SettingPhoneBean(
    val date: String,
    val desc: String,
    val holidayHotline: String?,//服务热线
    val mobile: String? //24小时服务
)