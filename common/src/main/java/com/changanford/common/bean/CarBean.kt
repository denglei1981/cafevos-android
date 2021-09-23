package com.changanford.common.bean

import java.io.Serializable

/**
 *  文件名：CarBean
 *  创建者: zcy
 *  创建日期：2021/9/15 9:19
 *  描述: TODO
 *  修改描述：TODO
 */

/**
 * 查询认证车辆，筛选数据状态
 */

data class CarItemBean(
    val authTime: Any,
    val avatar: Any,
    val carColor: String,
    val carStatus: Any,
    val createBy: Any,
    val createTime: String,
    val crmAuthRemake: String,
    val dealerId: Any,
    val dealerName: String,
    val dealerPhoneNumber: String,
    val driverImg: String,
    val driverSImg: String,
    val carId: String,
    val id: Int,
    val idSImg: String,
    val idsNumber: String,
    val idsType: Int,
    val isBind: Any,
    val isRecPurchase: Any,
    val latestMaintainKm: String,
    val modelCode: String,
    val modelName: String,
    val nickname: Any,
    val ownerName: String,
    val ownerType: Int,
    var personalShow: Int,
    val phone: String,
    val plateNum: String,
    val pretrialRemake: String,
    val pretrialTime: Any,
    val pretrialUserId: Any,
    val remark: Any,
    val salesDate: String,
    val searchValue: Any,
    val seriesCode: String,
    val seriesName: String,
    val seriesUrl: String,
    var status: Int,
    val unBindTime: Any,
    val updateBy: Any,
    val updateTime: Any,
    val userId: Int,
    val vehicleId: Any,
    val vin: String,
    val idsImg: String,
    val soldierCardImg: String,
    val policeCardImg: String,
    var reason: String,
    var invoiceImg: String, //发票
    val msgCode: String, //msgCode =“700001”
    val msgButton: String, //消息弹框内容
    val isIncall: Boolean,
    var isExceedThree: Boolean = false,
    //原来列表的基础增加这两个字段（是否开通车控通过realnameAuthStatus字段判断;是否CRM认证 通过status==null
    // （表示并未提交过crm认证）反之则按照以前的状态值判断）
    //AUTHED   认证通过
    //AUTHING  认证中
    //AUTH_FAILED 认证失败
    //CHECK_FAILED 审核失败
    //CHECK_PICFAILED 审核失败
    //CHECK_SUCCESS 审核通过
    //CHECK_WAIT 审核中
    //UNAUTH 未实名  @蒋小寒 @周春宇 
    var realnameAuthStatus: String,
    val incallAuthRemake: String,
    val carName: String

) : Serializable
