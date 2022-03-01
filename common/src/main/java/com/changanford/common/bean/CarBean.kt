package com.changanford.common.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *  文件名：CarBean
 *  创建者: zcy
 *  创建日期：2021/9/15 9:19
 *  描述: TODO
 *  修改描述：TODO
 */


data class CarAuthBean(
    val userId: Int = 0,
    val isCarOwner: Int = 0,
    val carAuthConfVo: CarAuthConfVo?,
    val carList: ArrayList<CarItemBean>?
)

data class CarAuthConfVo(
    val img: String? = "",
    val title: String? = "",
    val des: String? = ""
)

/**
 * 查询认证车辆，筛选数据状态
 */

data class CarItemBean(
    var vin: String = "",
    val modelName: String = "",//车型名称
    val modelUrl: String = "",//车型图
    var plateNum: String = "",
    @SerializedName(
        value = "authStatus",
        alternate = ["status"]
    ) var authStatus: Int = 0,
    val examineRemakeFront: String? = "", //审核备注
    val dealerName: String = "",
    val dealerPhone: String = "",
    val saleDate: Long = 0L,//购车日期


    val isNeedChangeBind: Int = 0, //是否需要更换绑定 1是 0 否
    val ownerCertImg: String = "",//车主证件图片地址
    val ownerCertType: Int = 0,//车主证件类型（1:行驶证 2:发票）
    val oldBindPhone: String = "",

    val authId: String = "",

    val authTime: String = "",
    val avatar: String = "",
    val carColor: String = "",
    val carStatus: String = "",
    val createBy: String = "",
    val createTime: String = "",
    val crmAuthRemake: String = "",
    val dealerId: String = "",
    val driverImg: String = "",
    val driverSImg: String = "",
    val carId: String = "",
    val id: Int = 0,
    val idSImg: String = "",
    val idsNumber: String = "",
    val idsType: Int = 0,
    val isBind: String = "",
    val isRecPurchase: String = "",
    val latestMaintainKm: String = "",
    val modelCode: String = "",
    val nickname: String = "",
    val ownerName: String = "",
    val ownerType: Int = 0,
    var personalShow: Int = 0,
    val phone: String = "",
    val pretrialRemake: String = "",
    val salesDate: String = "",
    val seriesCode: String = "",
    val userId: Int = 0,
    val idsImg: String = "",
    val soldierCardImg: String = "",
    val policeCardImg: String = "",
    var reason: String = "",
    var invoiceImg: String = "", //发票
    var msgCode: String = "", //msgCode =“700001”
    val msgButton: String = "", //消息弹框内容
    val isIncall: Boolean = false,
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
    var realnameAuthStatus: String = "",
    val incallAuthRemake: String = "",
    val carName: String = "",

    ) : Serializable


/**
 * Ocr识别提交数据
 * imgExt:图片地址/base64
 * imgType:  HTTP_URL(1, "网络地址"),     BASE64(2, "BASE64");
 *  ocrSceneType: VIN(1, "车架号"),     ID_CARD(2, "身份证"),     DRIVER_LICENCE(3, "驾驶证"),     WALK_LICENCE(4, "行驶证");
 *  INVOICE(4, "发票"); CAR_INVOICE
 *  path : 上传图片地址（无前缀）
 */

data

class OcrRequestBean(
    var imgExt: String,
    var ocrSceneType: String,
    var path: String,
    var imgType: String = "HTTP_URL"
)

