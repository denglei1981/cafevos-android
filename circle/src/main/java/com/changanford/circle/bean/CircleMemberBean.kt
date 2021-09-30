package com.changanford.circle.bean

import com.changanford.common.bean.Imag


/**
 * @Author: hpb
 * @Date: 2020/5/20
 * @Des:
 */
data class CircleMemberBean(
    val avatar: String,
    val circleId: Int,
    val createTime: Long,
    val memberIcon: Any,
    val memberId: Any,
    val memberName: Any,
    val nickname: String,
    val status: Int,
    val headFrameImage: String,
    val imags: ArrayList<Imag>,
    val userId: String,
    val starOrderNumStr: String?,
    var isCheck: Boolean = false
)