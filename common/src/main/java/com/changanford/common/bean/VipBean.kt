package com.changanford.common.bean

import java.io.Serializable

/**
 *  文件名：VipBean
 *  创建者: zcy
 *  创建日期：2021/9/14 14:26
 *  描述: TODO
 *  修改描述：TODO
 */
data class MedalListBeanItem(
    val createBy: String,
    val createTime: String,
    val fillCondition: String,
    val isGet: String?,
    var isShow: String,
    val medalId: String,
    val medalImage: String,
    var medalName: String,
    var medalType: Int,
    val medalTypeName: String,
    val remark: String,
    val searchValue: String,
    val sort: String,
    val status: String,
    val updateBy: String,
    val updateTime: String
) : Serializable
