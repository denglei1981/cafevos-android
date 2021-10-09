package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2021/9/28
 * @Description : 商城
 */
class Extend
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
    val salesCount: Int = 0,
    val stockNow: Int = 0,
    val stockPlusSalesCount: Int? = 0,
    var timeState: String = "",
    val createBy: Any? = null,
    val createTime: Long = 0,
    val dataState: String = "",
    val detailsHtml: String = "",
    val evalCount: Any? = null,
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
    val vipFb: Any? = null,
    var stockProportion:String="0",//库存百分比 0-100
    var killStates:Int=0//秒杀状态 //按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
)
class Params
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
    val fbLine: String? = "0",
    val fbPrice: String? = "0",
    val imgs: ArrayList<String> = arrayListOf(),
    val limitBuy: String = "",
    val limitBuyNum: String = "0",
    val now: String = "",
    val purchasedNum: Int? = 0,
    val salesCount: Int = 0,
    val secKillInfo: SecKillInfo? = null,
    val secondName: String = "",
    val shareBeanVO: ShareBeanVO = ShareBeanVO(),
    val skuCodeRule: String = "",
    val skuVos: List<SkuVo> = listOf(),
    val spuPageType: String = "",
    val stock: Int = 0,
    var stockProportion:String="0",//库存百分比 0-100
    var timestamp:Long?=0//服务器时间
)

data class Attribute(
    val attributeId: Int = 0,
    val attributeName: String = "",
    val optionVos: List<OptionVo> = listOf()
)

data class SecKillInfo(
    val timeBegin: String = "",
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
/**
 * 评价
* */
data class CommentBean(
    val pageList: PageList?,
    val totalEvalNum: Int = 0,
    val totalEvalScore: Int = 0
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
    val evalTime: String = "",
    val evalType: String = "",
    val mallMallOrderEvalId: Int = 0,
    val mallMallOrderId: Int = 0,
    val mallMallSkuId: Int = 0,
    val mallMallSpuId: Int = 0,
    val memo: String = "",
    val nickName: String = "",
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
