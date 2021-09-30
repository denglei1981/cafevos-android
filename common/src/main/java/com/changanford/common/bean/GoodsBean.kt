package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2021/9/28
 * @Description : 商城
 */
data class GoodsTypesBean(
    val dataList: ArrayList<GoodsTypesItemBean> = ArrayList(),
    val extend: Extend = Extend(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class GoodsTypesItemBean(
    val img: Any? = null,
    val imgSelected: Any? = null,
    val typeId: String = "0",
    val typeName: String = ""
)

class Extend
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
    val fb: Int = 0,
    val fbOfLine: Int = 0,
    val imgUrl: String = "",
    var isSettedNotice: String = "",
    val mallMallSpuSeckillRangeId: String = "0",
    val salesCount: Int = 0,
    val stockNow: Int = 0,
    val stockPlusSalesCount: Int = 0,
    var timeState: String = "",
    var stockProportion:String="0",//库存百分比 0-100
    var killStates:Int=0//秒杀状态 //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
)
// 秒杀时段
data class SeckillSessionsBean(
    val now: Long? = null,
    val seckillSessions: ArrayList<SeckillSession> = ArrayList()
)

data class SeckillSession(
    val date: Long = 0,
    val seckillTimeRanges: ArrayList<SeckillTimeRange> = ArrayList(),
    val sessionId: Int = 0,
    val sessionName: String = ""
)

data class SeckillTimeRange(
    val timeBegin: Long = 0,
    val timeEnd: Long = 0,
    val timeRangeId: String = "0",
    var time:String?="",
    var states:Int=0,//状态 0 已结束  1 进行中  2未开始
    var statesTxt:String="已结束"
)
/**
 * 商品详情
* */
data class GoodsDetailBean(
    val attributes: List<Attribute> = listOf(),
    val detailsHtml: String = "",
    val fbLine: Int = 0,
    val fbPrice: Int = 0,
    val imgs: List<String> = ArrayList(),
    val limitBuy: String = "",
    val limitBuyNum: Int = 0,
    val now: String = "",
    val purchasedNum: Int = 0,
    val salesCount: Int = 0,
    val secKillInfo: SecKillInfo = SecKillInfo(),
    val secondName: String = "",
    val shareBeanVO: ShareBeanVO = ShareBeanVO(),
    val skuCodeRule: String = "",
    val skuVos: List<SkuVo> = listOf(),
    val spuPageType: String = "",
    val stock: Int = 0
)

data class Attribute(
    val attributeId: Int = 0,
    val attributeName: String = "",
    val optionVos: List<OptionVo> = listOf()
)

data class SecKillInfo(
    val timeBegin: String = "",
    val timeEnd: String = ""
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
    val fbPrice: Int = 0,
    val skuCode: String = "",
    val skuId: Int = 0,
    val skuImg: String = "",
    val stock: Int = 0
)

data class OptionVo(
    val optionId: Int = 0,
    val optionName: String = ""
)