package com.changanford.common.bean

import com.changanford.common.util.TimeUtils
import java.io.Serializable

/**
 *  文件名：VipBean
 *  创建者: zcy
 *  创建日期：2021/9/14 14:26
 *  描述: TODO
 *  修改描述：TODO
 */
data class MedalListBeanItem(
    val createBy: String = "",
    val createTime: String = "",
    val fillCondition: String = "",
    var isGet: String? = "",
    var isShow: String = "",
    val medalId: String = "",
    val medalImage: String = "",
    var medalName: String = "",
    var medalType: Int = 0,
    val medalTypeName: String = "",
    val remark: String = "",
    val searchValue: String = "",
    val sort: String = "",
    val status: String = "",
    val updateBy: String = "",
    val updateTime: String = "",
    var getTime: String = "" // 获取时间
) : Serializable {
    fun timeS(): String {

        return TimeUtils.InputTimetamp(getTime, "yyyy-MM-dd").plus("点亮")
    }
}

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