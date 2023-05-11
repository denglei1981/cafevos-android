package com.changanford.common.bean

import android.os.Parcelable
import android.text.SpannableString
import android.text.TextUtils
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.painter.Painter
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.common.wutil.WConstant
import com.changanford.common.wutil.wLogE
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * @Author : wenke
 * @Time : 2021/9/28
 * @Description : 商城
 */
class Extend

//商品分类
class GoodsClassification : ArrayList<GoodsTypesItemBean>()

data class GoodsTypesItemBean(
    val mallMallTagId: String = "0",
    val tagName: String = "全部",
    val tagType: String? = null,
    val kindId: String? = null,
    val kindName: String? = null,
)

data class GoodsListBean(
    val dataList: List<GoodsItemBean> = listOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class GoodsItemBean(
    val blackDiscountPrice: Int = 0,
    val blackDiscountPriceIntegral: Any? = null,
    val carownerDiscountPrice: Any? = null,
    val carownerDiscountPriceIntegral: Any? = null,
    val conditional: Int = 0,
    val conditionalVal: Any? = null,
    val hasCard: Any? = null,
    val img: String = "",
    val isCarowner: Int = 0,
    val isHot: Int = 0,
    val isNew: Int = 0,
    val isOwner: Int = 0,
    val isSeckill: Int = 0,
    val isSingleSpec: Int = 0,
    val jumpTargetName: Any? = null,
    val originalPrice: Int = 0,
    val originalPriceIntegral: Int = 0,
    val payType: Int = 0,
    val price: Int = 0,
    val priceIntegral: Int = 0,
    val remainNum: Int = 0,
    val restrictionNum: String = "",
    val saleNum: String = "",
    val sort: Any? = null,
    val spuCode: String = "",
    val spuDesc: String = "",
    val spuDetail: Any? = null,
    var spuId: String = "0",
    var spuName: String = "",
    val fb: String? = null,
    val fbOfLine: String? = "0",
    val imgUrl: String = "",
    var isSettedNotice: String = "",
    val mallMallSpuSeckillRangeId: String = "0",
    var salesCount: Int = 0,
    val stockNow: Int = 0,
    val stockPlusSalesCount: Int = 1,
    var timeState: String = "",
    val createBy: Any? = null,
    val createTime: Long = 0,
    val dataState: String = "",
    val detailsHtml: String = "",
    val evalCount: String? = "0",
    val evalScoreSum: Any? = null,
    val hot: Any? = null,
    val isRecommend: String = "",
    val limitBuyNum: String? = "0",
    val limitIdentity: String? = null,
    val limitPhone: String? = null,
    val limitSeckill: String? = null,
    val lineFb: String = "",
    val mallMallBrandId: Int = 0,
    val mallMallCategoryId: Int = 0,
    val mallMallSpuId: String = "0",
    val memo: Any? = null,
    var normalFb: String = "0",
    val onShelveTime: Any? = null,
    val `operator`: String = "",
    val orderNum: Any? = null,
    val params: Params = Params(),
    val remark: Any? = null,
    val searchValue: Any? = null,
    var secondName: String = "",
    val seeLimit: String = "",
    val skuCodeRule: Any? = null,
    val skuJson: String = "",
    val specJson: String = "",
    var spuImgs: String? = null,
    val spuNew: Any? = null,
    var spuPageTagType: String? = null,
    var spuPageType: String = "",
    val spuStatus: String = "",
    val stock: Int = 0,
    val updateBy: Any? = null,
    val updateTime: Long = 0,
    var vipFb: String = "0",
    var stockProportion: String = "0",//库存百分比 0-100
    var killStates: Int = 0,//秒杀状态 //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
    val beginTime: Long = 0,
    val mallMallSeckillRangeId: Int = 0,
    val mallMallSeckillSessionId: Int = 0,
    val recommend: String = "",
    var robbedPercentage: String = "",
    val seckillFb: String = "0",
    var seckillNumLimit: String? = "0",
    var seckillStatus: String = "",
    var seckillStatuTxt: String = "",
    var seckillStock: Int? = 0,
    var sekillCount: Int = 0,
    var totalStock: Int = 1,
    var secondarySpuPageTagType: String? = "",
    val jumpDataType: Int? = 3,
    val jumpDataValue: String? = null,
    val exchageCount: String? = null,
    val goodsImg: String = "",
    val goodsName: String = "",
    val goodsNameSecond: String = "",
    val mallWbGoodsId: String? = null,
    val fbPrice: String? = "0",
    val priceFb: String? = null,
    var rmbPrice: String? = null,
) {
    fun getLineFbEmpty(): Boolean {  //商城划线价，后台未设置的时候需要隐藏不显示
        if (TextUtils.isEmpty(lineFb)) {
            return true
        }
        if (lineFb == "0") {
            return true
        }
        return false
    }

    fun getJdType(): Int {
        return jumpDataType ?: 3
    }

    fun getJdValue(): String {
        return jumpDataValue ?: mallMallSpuId
    }

    /**
     * 将福币转换为人民币 1元=100福币
     * */
    fun getRMB(fb: String? = priceFb): String {
        if (fb != null) {
            val fbToFloat = fb.toFloat()
            val remainder = fbToFloat % 100
            rmbPrice = if (remainder > 0) "${fbToFloat / 100}"
            else "${fb.toInt() / 100}"
        }
        return rmbPrice ?: "0"
    }

    /**
     * 将维保商品数据转为普通商品
     * */
    fun maintenanceToGoods() {
        spuImgs = goodsImg
        spuName = goodsName
        secondName = goodsNameSecond
        normalFb = fbPrice ?: "0"
//        spuId=mallWbGoodsId?:"0"
        spuPageType = "MAINTENANCE"//标识为维保商品
    }

    /**
     * 获取图片单个路径
     * */
    fun getImgPath(imgUrls: String? = spuImgs): String? {
        imgUrls?.apply {
            if (this.contains(",")) return split(",")[0]
        }
        return imgUrls
    }

    /**
     * 销量-（维保列表销量和其它商品销量字段不统一）
     * */
    fun getSales(): String {
        return if (mallWbGoodsId == null) "$salesCount" else exchageCount ?: "0"
    }
}

class Params

// 秒杀时段
data class SeckillSessionsBean(
    val now: Long? = null,
    val seckillSessions: ArrayList<SeckillSession>? = ArrayList()
)

data class SeckillSession(
    val date: Long = 0,
    val seckillTimeRanges: ArrayList<SeckillTimeRange> = ArrayList(),
    val sessionId: Int = 0,
    val sessionName: String = "",
    var dateFormat: Int = 0,
    var index: Int = 0,
)

data class SeckillTimeRange(
    val timeBegin: Long = 0,
    val timeEnd: Long = 0,
    val timeRangeId: String = "0",
    var time: String? = "",
    var states: Int = 0,//状态 0 已结束  1 进行中  2未开始
    var statesTxt: String = "已结束",
    var index: Int = 0,
)


/**
 * 商城首页商品列表
 * */
data class GoodsHomeBean(
    val list: ArrayList<GoodsTypesItemBean> = arrayListOf(),
    val responsePageBean: GoodsListBean? = null,
)

data class NewGoodsDetailBean(
    var normalSpuDetail: GoodsDetailBean? = null,
    var seckillSpuDetail: GoodsDetailBean? = null,
    var haggleSpuDetailDto: GoodsDetailBean? = null,
)

data class ConfirmOrderBean(
    var orderConfirmType: Int? = 0,//来源 0商品详情 1购物车
    var fbBalance: Int? = null,//用户福币余额
    var totalBuyNum: Int = 0,//总购买数量
    var addressInfo: String? = null,
    var addressId: Int? = null,
    var totalOriginalFb: Int = 0,//原总价
    var totalFb: Int = 0,//现总价
    var freightPrice: String? = "0.00",//运费
    var vinCode: String? = null,//维保商品 VIN码
    var models: String? = null,//车型
    var dataList: ArrayList<GoodsDetailBean>? = null,
    var totalPayFbPrice: Int = 0,//单位福币
    var totalPayFb: Int = 0,
) {
    /**
     * 获取福币总支付价格 总共支付 (商品金额+运费-优惠福币)
     * [couponsFb]优惠福币 单位福币
     * [isFb]是否为福币
     * */
    fun getTotalPayFbPrice(couponsFb: String, isFb: Boolean = false): Int {
        val multiple = if (isFb) 1 else 100
        totalPayFbPrice = WCommonUtil.getHeatNumUP(
            "${(totalFb ?: 0) + ((freightPrice ?: "0").toFloat() * 100) - (couponsFb.toFloat() * multiple)}",
            0
        ).toInt()
        return if (totalPayFbPrice > 0) totalPayFbPrice else 0
    }
}

data class ConfirmOrderInfoBean(
    var busSourse: Int = 0,
    var carModel: String? = null,
    var mallMallHaggleUserGoodsId: String? = null,
    var num: Int = 0,
    var skuId: String? = null,
    var vin: String? = null,
) {
    fun initBean(spuPageType: String?) {
        busSourse = 0
        when (spuPageType ?: "") {
            //秒杀
            "SECKILL" -> {
                busSourse = 1
//                skuId=mallMallSkuSpuSeckillRangeId?:skuId
            }
            //砍价
            "2" -> {
                busSourse = 2
            }
            WConstant.maintenanceType -> {//维保商品
                busSourse = 3
//                body["mallMallWbVinSpuId"]= mallMallWbVinSpuId?:""
//                vin= vinCode?:""
            }
        }
    }
}

/**
 * 商品详情
 * */
data class GoodsDetailBean(
    val attributes: List<Attribute> = listOf(),
    val detailsHtml: String = "",
    val spuSource: String = "0",//来源(0-后台;1-京东)
    val managerSpuDetail: String = "0",//来源(0-后台;1-京东)
    var price: String? = "0",
    var orginPrice0: String? = "0",//原价
    var orginPrice: String? = null,//原价
    var fbLine: String? = "0",
    var fbPrice: String = "0",
    var orFbPrice: String = "0",
    val imgs: ArrayList<String> = arrayListOf(),
    val limitBuy: String = "",
    val limitBuyNum: String? = "0",
    val now: Long = 0,
    var purchasedNum: Int? = 0,
    var salesCount: Int = 0,
    var totalStock: Int? = 0,//总库存
    val secKillInfo: SecKillInfo? = null,
    val secondName: String = "",
    val shareBeanVO: TaskShareBean? = null,
    val skuCodeRule: String = "",
    var skuVos: ArrayList<SkuVo> = ArrayList(),
    var spuPageType: String? = null,
    var stock: Int = 0,
    var allSkuStock: Int = 0,//sku库存之和
    val mallOrderEval: CommentItem? = null,
    var stockProportion: String = "0",//库存百分比 0-100
    val spuName: String? = "",
    var spuId: String = "0",
    var skuId: String = "",
    var buyNum: Int = 1,//购买数量
    var skuCodeTxts: List<String>? = null,
    var acountFb: Int = 0,//账号积分
    val param: String? = "",
    var totalPayFb: String = "",//总支付积分
    var freightPrice: String? = "0.00",//运费 0为包邮
    val collect: String = "",//是否收藏 YES NO
    var addressId: Int? = 0,
    var preferentialFb: String? = "",//会员优惠
    var skuCode: String? = "",
    var specifications: String? = null,
    var addressInfo: String? = null,
    var skuImg: String? = null,
    var mallMallSkuSpuSeckillRangeId: String? = null,
    val mallMallHaggleUserGoodsId: String? = null,//发起砍价id
    var source: String? = "0",
    var evalCount: String? = "0",
    var isAgree: Boolean = false,//是否同意协议
    var killStates: Int = 0,//秒杀状态
    var secondarySpuPageTagType: String? = "",
    var isUpdateBuyNum: Boolean = true,//是否可以更改购买数量
    var vinCode: String? = null,//维保商品 VIN码
    var models: String? = null,//车型
    var busSourse: String? = null,
    var mallMallWbVinSpuId: String? = null,
    var recommend: ArrayList<GoodsItemBean>? = null,//推荐
    var rmbPrice: String? = fbPrice,
//    var orderConfirmType:Int?=0,//确认订单来源
    var carModel: String? = null,
    var mallMallUserSkuId: Long = 0,
    var fbPer: String? = null,
    var mallMallSkuId: String? = null,
    var mallMallSpuId: String? = null,
    var num: Int? = 0,
    var vipFb: String? = null,
    var mallSkuState: String? = null,
    var shoppingCartCount: Int = 0,//购物车数量
    var limitSeckill: String? = "",
    var mallMallRefundId: String? = "" // 退款id

) {
    fun getLimitBuyNum(): Int {
        return if ("YES" == limitBuy) (limitBuyNum ?: "0").toInt() else 0
    }

    fun getCurrentLimitBuyNum(): Int {
        return if ("YES" == limitSeckill) (limitBuyNum ?: "0").toInt() else 0
    }

    fun getLineFbEmpty(): Boolean {  //商城划线价，后台未设置的时候需要隐藏不显示
        if (TextUtils.isEmpty(fbLine) || "0" == fbLine) {
            return true
        }
        return false
    }

    fun getTagList(): List<String> {
        if (!TextUtils.isEmpty(specifications)) {
            return specifications!!.split(",").filter { "" != it }
        }
        return arrayListOf()
    }

    /**
     * 将福币转换为人民币 1元=100福币
     * */
    fun getRMB(fb: String? = fbPrice): String {
        if (fb != null) {
            val fbToFloat = fb.toFloat()
            val remainder = fbToFloat % 100
            rmbPrice = if (remainder > 0) "${fbToFloat / 100}"
            else "${fb.toInt() / 100}"
        }
        return rmbPrice ?: "0"
    }

    /**
     * 将购物车数据转为确认订单列表数据
     * */
    fun carBeanToOrderBean() {
        skuId = mallMallSkuId ?: "0"
        spuId = mallMallSpuId ?: "0"
        buyNum = num ?: 0
        skuCodeTxts = getTagList()
        fbPrice = fbPer ?: "0"
        orginPrice = fbPer
        rmbPrice = getRMB(fbPer)
    }


}

data class Attribute(
    val attributeId: Int = 0,
    val attributeName: String = "",
    val optionVos: List<OptionVo> = listOf()
)

data class SecKillInfo(
    val timeBegin: Long = 0,
    val timeEnd: Long = 0
)

data class ShareBeanVO(
    val bizId: Int = 0,
    val shareDesc: String = "",
    val shareImg: String = "",
    val shareTitle: String = "",
    val shareUrl: String = "",
    val sign: String = "",
    val type: Int = 0,
    val wxminiprogramCode: String = ""
)

data class SkuVo(
    val fbPrice: String = "0",
    val skuCode: String = "",
    val skuId: String = "0",
    val skuImg: String = "",
    var stock: String = "0",
    val skuStatus: String = "",//上线:ON_SHELVE  下线:UNDER_SHELVE
    var skuTxt: String? = "",
    var mallMallSkuSpuSeckillRangeId: String? = "0",
    var orginPrice: String? = "0",//原价
    var skuCodeArr: List<String> = listOf(),
)

data class OptionVo(
    val optionId: String = "0",
    val optionName: String = "",
    var isVisibility: Boolean = false
)

/**
 * 评价
 * */
data class CommentBean(
    val dataList: List<CommentItem>? = null,
    val totalEvalNum: Int = 0,
    val totalEvalScore: Float = 0f,
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0,
    var type: Int = 0,
    var queryTypeCount: QueryTypeCountBean? = null,
)

data class CommentInfoBean(
    val totalEvalNum: Int = 0,
    val totalEvalScore: Float = 0f,
    var queryTypeCount: QueryTypeCountBean? = null,
)

data class QueryTypeCountBean(
    val ALL: String = "0",
    val HAVE_IMG: String = "0",
    val NEGATIVE: String = "0",
    val PRAISE: String = "0",
    val REVIEWS: String = "0",
)

/**
 * 评价
 * */
data class CommentItem(
    val anonymous: String = "",
    val avater: String = "",
    val createBy: String = "",
    val createTime: String = "",
    val dataState: String = "",
    val evalScore: Int = 0,
    val evalStatus: String = "",
    val evalText: String = "",
    var evalTime: Long = 0,
    var evalTimeTxt: String? = "0",
    val evalType: String = "",
    val mallMallOrderEvalId: Int = 0,
    val mallMallOrderId: Int = 0,
    val mallMallSkuId: Int = 0,
    val mallMallSpuId: Int = 0,
    val memo: String = "",
    var nickName: String = "",
    val `operator`: String = "",
    val orderNo: String = "",
    val params: Params = Params(),
    val remark: String = "",
    val searchValue: String = "",
    val skuCode: String = "",
    val spuName: String = "",
    val updateBy: String = "",
    val updateTime: String = "",
    val userId: Int = 0,
    var imgs: String? = null,
    var evalImgs: List<String>? = null,
    var reviewEval: ReviewEvalBean? = null,
) {
    fun getImgArr(imgs: String? = this.imgs): List<String> {
        return if (!TextUtils.isEmpty(imgs) && imgs!!.contains(",")) {
            imgs.split(",").filter { "" != it }
        } else arrayListOf(imgs ?: "")
    }
}

/*
* 追评
* */
data class ReviewEvalBean(var evalText: String? = null, var evalTime: Long = 0)

/**
 * 商品首页
 * */
data class ShopHomeBean(
    val indexSeckillDtoList: List<GoodsItemBean> = listOf(),
    val mallIndexDto: MallIndexDto = MallIndexDto(),
    val mallTags: ArrayList<GoodsTypesItemBean>? = null,
    val mallSpuKindDtos: ArrayList<ShopRecommendBean>? = null,//推荐列表
    var totalIntegral: String? = null,//我的福币
    var shoppingCartCount: Int? = null,//购物车数量
)

class MallIndexDto

/**
 * 订单列表
 * */
data class ShopOrderBean(
    val dataList: List<OrderItemBean> = listOf(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0,
    var nowTime: Long? = 0,
)

data class OrderItemBean(
    var addressId: Int = 0,
    var addressInfo: String = "",
    val addressName: String = "",
    var buyNum: String? = "0",
    val consignee: String = "",
    val consumerMsg: String? = "",
    val courierCompany: String? = "",
    val courierNo: String? = "",
    val createBy: String = "",
    val createTime: String = "",
    val dataState: String = "",
    val discount: String = "0",
    val discountScale: Int = 0,
    var evalStatus: String? = "",
    var fbCost: String? = "0",
    val fbOfOrderPrice: String = "0",
    var fbOfUnitPrice: String = "0",
    val haggleOrder: String = "",//是否砍价订单
    val mallMallDiscountScaleId: String = "0",
    val mallMallHaggleActivityId: String = "0",
    val mallMallHaggleSkuId: String = "0",
    val mallMallHaggleSpuId: String = "0",
    val mallMallHaggleUserGoodsId: String = "0",
    val mallMallSeckillRangeId: Int = 0,
    val mallMallSeckillSessionId: Int = 0,
    val mallMallSkuId: String = "0",
    var mallOrderSkuId: String? = null,
    val mallMallSkuSpuSeckillRangeId: Int = 0,
    val mallMallSpuId: String = "0",// 没有用
    val mallMallSpuSeckillRangeId: Int = 0,
    val memo: String = "",
    val nickName: String = "",
    val `operator`: String = "",
    var orderNo: String = "",
    var orderStatus: String = "",
    var orderTime: Long? = 0,
    var orderTimeTxt: String? = "",
    val params: Params = Params(),
    val phone: String = "",
    var preferentialFb: String? = "0",
    var fbMemberDiscount: String? = "0",
    val remark: String = "",
    val searchValue: String = "",
    val seckill: String = "",//是否秒杀
    val seckillFb: Int = 0,
    val skuCode: String = "",
    var skuImg: String? = "",
    val snapshotOfAttrOption: String = "",
    var specifications: String? = null,
    var spuName: String? = "",
    val spuSecondName: String = "",
    val updateBy: String = "",
    val updateTime: Long? = 0,
    val userId: Int = 0,
    val cost: String? = "0",
    var waitPayCountDown: Long? = 0,
    var acountFb: String = "0",
    var busSourse: String? = "0",
    val closeTime: Long? = 0,
    val evalStatusDetail: String = "",
    val payTime: Long? = 0,
    val sendTime: Long? = 0,
    var preferentialFbOfUnitPrice: String? = null,
    val receiveTime: String? = "", // 收货时间？
    val waitPayDuration: Long = 0,//待支付有效时间
    val payTimeDeadline: String? = null,
    var orderType: Int = 0,
    val jumpDataType: Int? = null,
    val jumpDataValue: String? = null,
    val orderBrief: String = "",
    val orderImg: String = "",
    var orderStatusName: String? = "",
    val skuName: String? = null,
    var logisticsInfo: String? = "",//物流信息
    var freightPrice: String? = "0.00",//运费 0为包邮
    var otherName: String? = "",
    var otherValue: String? = "",
    var totalIntegral: String? = null,
    var orderTypeName: String? = "",
    var orginPrice: String? = "0",
    var hagglePrice: String? = null,//砍价的原价
    var canApplyServiceOfAfterSales: String? = null,//是否可以退货 YES  NO
    var rmbPrice: String? = null,
    var orderReceiveAddress: OrderReceiveAddress?=null,
    var skuList: MutableList<OrderItemBean> = mutableListOf(),
    var payFb: String? = null,
    var payRmb: String? = null,
    var freightFb: String? = null,
    var payType: String? = null,
    var mallMallOrderId: String? = null,
    var skuOrderVOList: ArrayList<OrderSkuItem>? = null,
    var totalNum: String? = "0",
    var fb: String? = null,
    var rmb: String? = null,
    var busSource: String? = null,
    var isNewOrder: String? = null,
    var price: String? = null,
    var invoiced: String? = null, // 发票状态
    var statusDesc: String? = null,
    var couponDiscount: String = "",
    var haggleDiscount: String? = null,
    var sharedFb: String? = null,
    var sharedRmb: String? = null,
    var mallRefundStatus: String? = null, // 单个sku 状态
    var historyPackage: String? = null,
    var reviewEval: Boolean? = false,
    var canReview: String = "NO",
    var mallMallspuId: String = "",
    var packageJump: PackageJumpBean? = null,
    var refundStatus: String? = "",
    var refundTimes: Int? = null, // 申请售后次数
    var timestamp: Long? = null,
    var mallMallRefundId: String? = null //退款id

) {
    fun getRMBPrice() {
        rmbPrice = if (rmb != "0") rmb else getRMB(fb, "")
    }

    /**
     * 将福币转换为人民币 1元=100福币
     * */
    fun getRMB(fb: String? = fbCost, unit: String? = "¥"): String {
        if (fb != null) {
            val fbToFloat = fb.toFloat()
            val remainder = fbToFloat % 100
            rmbPrice = if (remainder > 0) "${fbToFloat / 100}"
            else "${fb.toInt() / 100}"
        }
        return "${unit ?: ""}${rmbPrice ?: "0"}"
    }

    fun getTagList(): List<String> {
        if (!TextUtils.isEmpty(specifications)) {
            return specifications!!.split(",").filter { "" != it }
        }
        return arrayListOf()
    }

//    fun getRMBExtendsUnit(fb: String? = fbCost): String {
//        if (fb != null) {
//            val fbToFloat = fb.toFloat()
//            val remainder = fbToFloat % 100
//            rmbPrice = if (remainder > 0) "${fbToFloat / 100}"
//            else "${fb.toInt() / 100}"
//        }
//        return rmbPrice?:"0"
//    }


}

data class PackageJumpBean(
    val jumpCode: Int = 0,
    val jumpVal: String? = null,
)

data class ShopOrderRefundBean(
    val dataList: List<OrderRefundItemBean>? = null,
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0,
    var nowTime: Long? = 0,
)

/**
 * 退款item
 * */
data class OrderRefundItemBean(
    val fbRefund: String? = null,
    val fbRefundApply: String? = null,
    val mallMallOrderId: String = "0",
    val mallMallRefundId: String = "0",
    val orderNo: String = "",
    val refundMethod: String = "",//CONTAIN_GOODS ONLY_COST
    val refundNo: String = "",
    val refundNum: Int = 0,
    val refundSkus: List<OrderSkuItem> = listOf(),
    val refundStatus: String = "",
    val refundType: String = "",
    val rmbRefund: String? = null,
    val rmbRefundApply: String? = null,
    var busSourse: String? = null,
    var busSource: String? = null,
) {
    fun getRefundStatusTxt(): String {
        return when (refundStatus) {
            "ON_GOING" -> "退款中"
            "SUCESS" -> "退款完成"
            "CLOSED" -> "退款关闭"
            "FINISH" -> "退款成功"
            else -> ""
        }
    }
}

data class OrderReceiveAddress(
    var addressId: String,
    var addressName: String,
    var phone: String = "",
    var consignee: String = ""
    /**用户名字*/
) {
    fun getUserInfo(): String {
        return consignee.plus("\t").plus(phone)
    }
}

@Parcelize
data class OrderInfoBean(
    @SerializedName("privatePayNo")
    val orderNo: String,//订单号
    val cost: String? = "0",
    val waitPayDuration: Long? = 0,
    var accountFb: String? = "",//账号余额
    var source: String? = "0",//1商品详情（原生）2 H5
    var payRmb: String? = null,
    //支付方式(0纯积分/1纯现金/2混合支付)
    var payType: Int? = null,
    var payFb: String? = null,
):Parcelable

data class ShopAddressInfoBean(
    val addressId: Int = 0,
    val addressName: String = "",
    val addressValueObj: AddressValueObj = AddressValueObj(),
    val city: Int = 0,
    val cityName: String = "",
    val consignee: String = "",
    val district: Int = 0,
    val districtName: String = "",
    val isDefault: Int = 0,
    val phone: String = "",
    val province: Int = 0,
    val provinceName: String = "",
    val status: Int = 0,
    val userId: Int = 0,
    var userInfo: String? = "$consignee   $phone",
    var addressInfo: String? = "$provinceName$cityName$districtName$addressName"
) {
    fun getAddress(): String {
        return "$provinceName$cityName$districtName$addressName"
    }

    fun getUserInfos(): String {
        return consignee.plus("\t" + phone)
    }
}

data class AddressValueObj(
    val addressName: String = "",
    val city: Int = 0,
    val cityName: String = "",
    val district: Int = 0,
    val districtName: String = "",
    val province: Int = 0,
    val provinceName: String = ""
)

data class OrderBriefBean(
    val busSource: String? = null,
    val busSourse: String? = null,
    val buyNum: String = "0",
    val fbCost: String = "0",
    val payType: String = "",
    val snapshotOfAttrOption: String? = null,
    var fbOfUnitPrice: String? = "0",
    var orginPrice: String? = "0",
    var hagglePrice: String? = null,//砍价的原价
    var totalPrice: String = "0",
    var price: String = "0",
    var num: String = "1",
)

data class SnapshotOfAttrOption(
    val mallMallAttributeId: String = "0",
    val mallMallOptionId: String = "0",
    val optionName: String = ""
)

class OrderTypesBean : ArrayList<OrderTypeItemBean>()
data class OrderTypeItemBean(
    val jumpDataType: Int = 0,
    val jumpDataValue: String? = "",
    val typeName: String? = "",
)

data class ShopRecommendBean(
    val topId: Int = 0,
    val topName: String? = null,
    val kindId: String? = null,
    val kindName: String? = null,
    val spuInfoList: ArrayList<GoodsItemBean>? = null,
)

//支付方式
data class PayWayBean(
    var id: Int = 0,
    var rmbPrice: String? = null,
    var fbPrice: String? = null,
    var isCheck: MutableState<Boolean>? = null,
    var payWayName: String? = null,
    var icon: Painter? = null,
    var payType: String = "0",
)

data class PayBackBean(
    var aliPay: String? = null,
    var uacPay: String? = null,
    var wxPay: WxPayBean? = null,
)

data class CreateOrderBean(
    var freight: String? = null,
    var orderConfirmType: Int = 0,
    var payBfb: String? = null,
    var totalIntegral: Int? = 0,
    var coupons: ArrayList<CouponsItemBean>? = null,
    var skuItems: ArrayList<OrderSkuItem>? = null,
) {
    fun getRmbBfb(payBfb: String? = this.payBfb): Float {
        "payBfb:$payBfb".wLogE("okhttp")
        return if (TextUtils.isEmpty(payBfb)) -1f
        else payBfb!!.toFloat() / 100f
    }
}

data class OrderSkuItem(
    val busSource: String? = null,
    val busSourse: String? = null,
    val carModel: String? = null,
    val mallMallHaggleUserGoodsId: Int? = 0,
    val num: Int = 0,
    val skuId: String? = null,
    val skuImg: String? = null,
    val specifications: String? = null,
    val spuId: String = "0",
    val spuName: String? = null,
    val stock: Int = 0,
    val unitPrice: String? = "0",
    val unitPriceFb: String? = "0",
    val unitPriceFbOld: String? = "0",
    val unitPriceOld: String? = "0",
    val vin: String? = null,
    val buyNum: String = "0",
    val price: String? = null,
    val sharedRmb: String? = null,
    var orderType: Int = 0,
    var fbPrice: String? = null,
    var rmbPrice: String? = null,
    var orderImg: String? = null,
    val refundNum: Int = 0,//退款数量
) {
    /**
     * 获取标签
     * */
    fun getTag(source: String? = busSourse): String {
        return when (source) {
            "1", "SECKILL" -> "秒杀"
            "2", "HAGGLE" -> "砍价"
            "3", "WB" -> "维保"
            else -> ""
        }
    }
}

/**
 * 优惠券
 *
 * */

data class CouponsItemBean(
    //满减条件(满多少钱可满减)
    val conditionMoney: String? = "0",
    val couponId: String? = null,
    val couponMarkId: String? = null,
    //抵扣金额(优惠类型为满减/立减时,这是抵扣金额;优惠类型折扣时,这是最多扣减金额) 单位人民币
    val couponMoney: String = "0",
    val couponName: String? = null,
    val couponRatio: String? = null,
    val couponRecordId: String? = null,
    val desc: String? = null,
    val discountType: String = "",
    val img: String = "",
    val mallMallSkuIds: List<String>? = null,
    val markImg: String = "",
    val markName: String = "",
    var state: String = "",
    val type: String = "",
    val useLimit: String = "",
    val useLimitValue: String = "",
    val userId: Int = 0,
    val validityBeginTime: Long? = 0,
    val validityEndTime: Long? = 0,
    var isAvailable: Boolean = false,//是否可用
    var discountsFb: Long = 0,//实际优惠福币
    var couponSendId: String, // 发放id
    var conditionName: String,

    ) {
    fun getRmbToFb(conditionMoney: String? = this.conditionMoney): Long {
        return ((conditionMoney ?: "0").toFloat() * 100).toLong()
    }

    /**
     * 计算折扣金额
     * [couponRatio]折扣比例 0.1-9.9折 -5折
     * */
    fun discountAmount(
        totalPrice: Long,
        couponRatio: Float? = (this.couponRatio ?: "0").toFloat()
    ): Long {
        if (couponRatio == null || couponRatio == 0f) return 0
//        //优惠向下
//        return totalPrice-WCommonUtil.getHeatNum("${totalPrice * (couponRatio / 10)}").toLong()
        //截取2位
        return WCommonUtil.getHeatNum("${totalPrice - totalPrice * (couponRatio / 10)}").toLong()
    }

    fun getShowMoney(): SpannableString {
        when (discountType) {
            "FULL_MINUS", "LEGISLATIVE_REDUCTION" -> { // 满减,立减
                val moneyStr = "￥".plus(couponMoney)
                return SpannableStringUtils.textSizeSpan(moneyStr, 0, 1, 12)
            }
            "DISCOUNT" -> { // 折扣
                val moneyStr = couponRatio.toString().plus("折")
                return SpannableStringUtils.textSizeSpan(
                    moneyStr,
                    moneyStr.length - 1,
                    moneyStr.length,
                    12
                )
            }

        }
        return SpannableStringUtils.textSizeSpan("00", 0, 1, 12)
    }

    private fun getCouponTypeStr(): String {
        return when (type) {
            "MALL_COUPON" -> {
                "劵类型: 商城劵\n"
            }
            "SERVICE_COUPON" -> {
                "劵类型: 服务劵\n"
            }
            else -> {
                "劵类型: 商城劵\n"
            }
        }
    }

    // 领取条件
    private fun getCouponConditionName(): String {
        if (TextUtils.isEmpty(conditionName)) {
            return ""
        }
        return "领取条件: ".plus(conditionName).plus("\n")
    }

    private fun getUseLimitStr(): String {
        if (TextUtils.isEmpty(desc)) {
            return ""
        }
        return "使用限制: ".plus(desc).plus("\n")
    }

    private fun getCouponNum(): String {
        return "劵编号 ".plus(couponRecordId).plus("\n")
    }

    fun getBottomTips(): String {
        return getCouponTypeStr().plus(getCouponConditionName()).plus(getUseLimitStr())
            .plus(getCouponNum())
    }

    fun getTips(): String {
        return when (discountType) {
            "LEGISLATIVE_REDUCTION" -> { // 满减,立减
                "无门槛"
            }
            "FULL_MINUS", "DISCOUNT" -> {
                "满${conditionMoney}元可用"
            }
            else -> {
                ""

            }
        }
    }
}

data class WxPayBean(
    val appid: String? = null,
    val minipath: String? = null,
    val miniuser: String? = null,
    val noncestr: String? = null,
    val `package`: String? = null,
    val partnerid: String? = null,
    val prepayid: String? = null,
    val sign: String? = null,
    val timestamp: String? = null,
)

// // CONTAIN_GOODS 退货退款
// ONLY_COST  仅退款
// （如果是单个sku退，则需要传此参数 ， refundType =  allOrderRefund 的情况，singleRefundType参数和skuItem参数可不传）
data class RefundOrderItemBean(
    var specifications: String = "",
    var spuName: String,
    var skuImg: String,
    var mallMallSkuId: String,
    var mallOrderSkuId: String,
    var buyNum: Int,
    var price: String,
    var singleRefundType: String,
    var sharedFb: String,
    var sharedRmb: String,
    var orderNo: String,
    var refundType: String,
    var refundNum: String,
    var fbOfUnitPrice: String,
) {
    fun getTagList(): List<String> {
        if (!TextUtils.isEmpty(specifications)) {
            return specifications.split(",").filter { "" != it }
        }
        return arrayListOf()
    }

    fun getSaleNum(): String {
        return "申请数量: ".plus(refundNum)
    }
}

data class OtherInfoBean(var content: String? = null)

data class ShopTagInfoBean(
    var tagId: String? = null,
    var tag: String? = null,
    var tagName: String? = null,
    var tagExtension: String? = null
)

data class RefundBean(
    var orderNo: String,
    var payFb: String?,
    var payRmb: String?,
    var refundType: String,
    var skuItem: RefundOrderItemBean? = null,
    var busSource: String = ""
) {

}