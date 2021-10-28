package com.changanford.common.bean

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
)
data class GoodsList(
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
    val jumpDataType: Int = 0,
    val jumpDataValue: String = "",
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
    val spuId: String = "0",
    val spuName: String = "",
    val fb: Int? = 0,
    val fbOfLine: Int? = 0,
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
    val lineFb: String? = null,
    val mallMallBrandId: Int = 0,
    val mallMallCategoryId: Int = 0,
    val mallMallSpuId: String = "0",
    val memo: Any? = null,
    val normalFb: String = "0",
    val onShelveTime: Any? = null,
    val `operator`: String = "",
    val orderNum: Any? = null,
    val params: Params = Params(),
    val remark: Any? = null,
    val searchValue: Any? = null,
    val secondName: String = "",
    val seeLimit: String = "",
    val skuCodeRule: Any? = null,
    val skuJson: String = "",
    val specJson: String = "",
    val spuImgs: String = "",
    val spuNew: Any? = null,
    val spuPageTagType: String = "",
    val spuStatus: String = "",
    val stock: Int = 0,
    val updateBy: Any? = null,
    val updateTime: Long = 0,
    val vipFb: String ="0",
    var stockProportion:String="0",//库存百分比 0-100
    var killStates:Int=0,//秒杀状态 //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
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
    var totalStock:Int=1,
)
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
    var dateFormat: Int=0,
    var index:Int=0,
)

data class SeckillTimeRange(
    val timeBegin: Long = 0,
    val timeEnd: Long = 0,
    val timeRangeId: String = "0",
    var time:String?="",
    var states:Int=0,//状态 0 已结束  1 进行中  2未开始
    var statesTxt:String="已结束",
    var index:Int=0,
)


/**
 * 商城首页商品列表
* */
data class GoodsHomeBean(
    val list: ArrayList<GoodsTypesItemBean> = arrayListOf(),
    val responsePageBean: GoodsList? = null,
)
/**
 * 商品详情
* */
data class GoodsDetailBean(
    val attributes: List<Attribute> = listOf(),
    val detailsHtml: String = "",
    var orginPrice: String? = "0",//原价
    var fbLine: String? = "0",
    var fbPrice: String = "0",
    val imgs: ArrayList<String> = arrayListOf(),
    val limitBuy: String = "",
    val limitBuyNum: String? = "0",
    val now: Long = 0,
    var purchasedNum: Int? = 0,
    var salesCount: Int = 0,
    var totalStock:Int?=0,//总库存
    val secKillInfo: SecKillInfo? = null,
    val secondName: String = "",
    val shareBeanVO: TaskShareBean?=null,
    val skuCodeRule: String = "",
    val skuVos: List<SkuVo> = listOf(),
    val spuPageType: String = "",
    var stock: Int = 0,
    val mallOrderEval:CommentItem?=null,
    var stockProportion:String="0",//库存百分比 0-100
    val spuName:String?="",
    val spuId:String="",
    var skuId: String="",
    var buyNum:Int=1,//购买数量
    var skuCodeTxts:List<String>?= listOf(),
    var acountFb:Int=0,//账号积分
    val param:String?="",
    var totalPayFb:String="",//总支付积分
    var freightPrice:String?="0.00",//运费 0为包邮
    val collect:String="",//是否收藏 YES NO
    var addressId:Int?=0,
    var preferentialFb:String?="",//会员优惠
    var skuCode:String?="",
    var specifications:String?="",
    var addressInfo:String?=null,
    var skuImg:String?=null,
    var mallMallSkuSpuSeckillRangeId:String?=null,
    val mallMallHaggleUserGoodsId: String? = null,
    var source:String?="0",
    var evalCount:String?="0",
    var isAgree:Boolean=false,//是否同意协议
    var killStates:Int=0,//秒杀状态
)

data class Attribute(
    val attributeId: Int = 0,
    val attributeName: String = "",
    val optionVos: List<OptionVo> = listOf()
)

data class SecKillInfo(
    val timeBegin: Long = 0,
    val timeEnd: Long =0
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
    val stock: String = "0",
    var skuTxt:String?="",
    var mallMallSkuSpuSeckillRangeId:String?="0",
    var orginPrice: String? = "0",//原价
)

data class OptionVo(
    val optionId: String = "0",
    val optionName: String = ""
)
/**
 * 评价
* */
data class CommentBean(
    val pageList: PageList?,
    val totalEvalNum: Int = 0,
    val totalEvalScore: Float =0f
)

data class PageList(
    val dataList: List<CommentItem>?,
    val extend: Extend = Extend(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class CommentItem(
    val anonymous: String = "",
    val avater: String = "",
    val createBy: String = "",
    val createTime: String = "",
    val dataState: String = "",
    val evalScore: Int = 0,
    val evalStatus: String = "",
    val evalText: String = "",
    var evalTime: Long? = 0,
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
    val userId: Int = 0
)
/**
 * 商品首页
* */
data class ShopHomeBean(
    val indexSeckillDtoList: List<GoodsItemBean> = listOf(),
    val mallIndexDto: MallIndexDto = MallIndexDto()
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
    var nowTime:Long?=0,
)

data class OrderItemBean(
    var addressId: Int = 0,
    var addressInfo: String = "",
    val addressName: String = "",
    var buyNum: String ="0",
    val consignee: String = "",
    val consumerMsg: String? = "",
    val courierCompany: String = "",
    val courierNo: String = "",
    val createBy: String = "",
    val createTime: String = "",
    val dataState: String = "",
    val discount: String = "0",
    val discountScale: Int = 0,
    val evalStatus: String? = "",
    var fbCost: String? = "0",
    val fbOfOrderPrice: String = "0",
    var fbOfUnitPrice: String ="0",
    val haggleOrder: String = "",//是否砍价订单
    val mallMallDiscountScaleId: Int = 0,
    val mallMallHaggleActivityId: Int = 0,
    val mallMallHaggleSkuId: Int = 0,
    val mallMallHaggleSpuId: Int = 0,
    val mallMallHaggleUserGoodsId: String = "0",
    val mallMallOrderId: String ="0",
    val mallMallSeckillRangeId: Int = 0,
    val mallMallSeckillSessionId: Int = 0,
    val mallMallSkuId: String = "0",
    val mallMallSkuSpuSeckillRangeId: Int = 0,
    val mallMallSpuId: String = "0",
    val mallMallSpuSeckillRangeId: Int = 0,
    val memo: String = "",
    val nickName: String = "",
    val `operator`: String = "",
    val orderNo: String = "",
    var orderStatus: String = "",
    var orderTime: Long? = 0,
    var orderTimeTxt: String? = "",
    val params: Params = Params(),
    var payType: String = "",
    val phone: String = "",
    var preferentialFb: String? = "0",
    val remark: String = "",
    val searchValue: String = "",
    val seckill: String = "",//是否秒杀
    val seckillFb: Int = 0,
    val skuCode: String = "",
    var skuImg: String? = "",
    val snapshotOfAttrOption: String = "",
    var specifications: String = "",
    var spuName: String? = "",
    val spuSecondName: String = "",
    val updateBy: String = "",
    val updateTime: Long? = 0,
    val userId: Int = 0,
    val cost:String?="0",
    var waitPayCountDown:Long?=0,
    var acountFb:String="0",
    val busSourse: String = "0",
    val closeTime: Long? = 0,
    val evalStatusDetail: String = "",
    val payTime: Long? = 0,
    val sendTime: Long? = 0,
    var preferentialFbOfUnitPrice: String? = null,
    val receiveTime: Any? = null,
    val waitPayDuration: Long = 0,//待支付有效时间
    var orderType:Int=0,
    val jumpDataType: Int = 0,
    val jumpDataValue: String = "",
    val orderBrief: String = "",
    val orderImg: String = "",
    var orderStatusName: String? = "",
    val skuName: String = "",
    var logisticsInfo:String?="",//物流信息
    var freightPrice:String?="0.00",//运费 0为包邮
    var otherName:String?="",
    var otherValue:String?="",
    var totalIntegral:String?="0",
    var orderTypeName:String?="",
    var orginPrice:String?="0",
)
data class OrderInfoBean(
    val orderNo:String,
    val cost:String?="0",
    var accountFb:String?="",
    var source:String?="0",//1商品详情（原生）
)
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
    var userInfo:String?="$consignee   $phone",
    var addressInfo: String?="$provinceName$cityName$districtName$addressName"
)

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
    val busSourse: Int = 0,
    val buyNum: String ="0",
    val fbCost: String = "0",
    val payType: String = "",
    val snapshotOfAttrOption: String?="",
    var fbOfUnitPrice:String?="0",
)

data class SnapshotOfAttrOption(
    val mallMallAttributeId: String = "0",
    val mallMallOptionId: String = "0",
    val optionName: String = ""
)
class OrderTypesBean : ArrayList<OrderTypeItemBean>()
data class OrderTypeItemBean(
    val jumpDataType: Int=0,
    val jumpDataValue: String?="",
    val typeName: String?="",
)
