package com.changanford.shop.api

import com.changanford.common.bean.*
import com.changanford.common.net.CommonResponse
import com.changanford.shop.bean.CouponData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @Author : wenke
 * @Time : 2021/9/30
 * @Description : ShopNetWorkApi
 */
interface ShopNetWorkApi {
    /**
     * 商品详情
     * */
    @POST("/mall/spus/{spuId}/get")
    suspend fun queryGoodsDetails(@Path("spuId")spuId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<NewGoodsDetailBean>
    /**
     * 首页点击更多秒杀接口
     * */
    @POST("/mall/sckills/get")
    suspend fun getSckills(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<SeckillSessionsBean>
    /**
     * 商品分类
     * */
    @POST("/mall/spus/getTags")
    suspend fun getClassification(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsClassification>
    /**
     * 商城列表
     * */
    @POST("/mall/spus/get")
    suspend fun queryGoodsList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsListBean>
    /**
     * 首页
     * */
    @POST("/mall/index")
    suspend fun queryShopHomeData(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ShopHomeBean>
    /**
     * 秒杀列表
     * */
    @POST("/mall/seckills/{seckillRangeId}/spus/get")
    suspend fun getGoodsKillList(@Path("seckillRangeId")seckillRangeId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsListBean>
    /**
     *秒杀提醒设置/取消
    * */
    @POST("/mall/seckills/spus/{mallMallSpuSeckillRangeId}/noticesSetting")
    suspend fun setKillNotices(@Path("mallMallSpuSeckillRangeId")rangeId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *秒订单评价
     * */
    @POST("/mall/eval/create")
    suspend fun orderEval(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>

    /**
     *评价列表
     * */
    @POST("/mall/eval/list")
    suspend fun goodsEvalList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<CommentBean>

    /**
     *评价列表-维保
     * */
    @POST("/mall/wb/evalsList")
    suspend fun goodsEvalListWb(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<CommentBean>
    /**
     *订单创建
     * */
    @POST("/mall/order/create")
    suspend fun orderCreate(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<OrderInfoBean>
    /**
     *商城订单列表
     * */
    @POST("/mall/order/list")
    suspend fun shopOrderList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ShopOrderBean>
    /**
     *所有订单
     * */
    @POST("/userOrderMergeInfo/getAllUserOrderMergeInfo")
    suspend fun getAllOrderList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ShopOrderBean>
    /**
     *添加足迹
     * */
    @POST("/mall/addFootprint/{spuId}")
    suspend fun addFootprint(@Path("spuId")spuId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *收藏商品
     * */
    @POST("/mall/collectGoods/{spuId}")
    suspend fun collectGoods(@Path("spuId")spuId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *取消收藏商品
     * */
    @POST("/userCollection/cancleMyCollections")
    suspend fun cancelCollectGoods(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>

    /**
     *订单详情
     * */
    @POST("/mall/order/detail")
    suspend fun orderDetail(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<OrderItemBean>
    /**
     *取消订单
     * */
    @POST("/mall/pay/cancel")
    suspend fun orderCancel(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *申请退货
     * */
    @POST("/mall/orders/{orderNo}/rtGoods")
    suspend fun applyRefund(@Path("orderNo")orderNo:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *我的积分
     * */
    @POST("/mall/index/myPoints")
    suspend fun getMyIntegral(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *虚拟币支付
     * */
    @POST("/app/pay/virtualCoinPay")
    suspend fun fbPay(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<PayBackBean>
    /**
     *现金支付
     * */
    @POST("/app/pay/reqPay")
    suspend fun rmbPay(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<PayBackBean>
    /**
     *订单确认收货
     * */
    @POST("/mall/order/confirmReceipt")
    suspend fun confirmReceipt(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     * 获取订单类型
    * */
    @POST("/base/config/getConfigValueByKey")
    suspend fun getOrderKey(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<OrderTypesBean>
    /**
     * 修改商品待支付状态的收货地址
     * */
    @POST("/mall/updateAdrrByOrderNo")
    suspend fun updateAddressByOrderNo(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<OrderTypesBean>
    /**
     * 维保商品列表
     * */
    @POST("/mall/wb/list")
    suspend fun maintenanceGoodsList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsListBean>
    /**
     * 推荐商品分类
     * */
    @POST("/mall/recommend/kindsGet")
    suspend fun getRecommendTypes(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ArrayList<GoodsTypesItemBean>>
    /**
     * 推荐商品列表
     * */
    @POST("/mall/recommend/spuGet")
    suspend fun getRecommendList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ArrayList<GoodsItemBean>>
    /**
     * 加入购物车
     * */
    @POST("/mall/userSkuAdd")
    suspend fun addShoppingCart(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     * 确认订单接口
     * */
    @POST("/mall/mallOrderConfirm")
    suspend fun confirmOrder(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<CreateOrderBean>

    /**
     *  购物车详情
     * */
    @POST("/mall/userSkuIndex")
    suspend fun getShoppingCartList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<MutableList<GoodsItemBean>>

    /**
     *  删除购物车商品
     * */
    @POST("/mall/delUserSkuByIds")
    suspend fun deleteShoppingCart(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>


    /**
     *  我的优惠券
     *          // {"pageNo":1,"pageSize":20,"queryParams":{"states":1}}  1.未使用 2.已使用 3.已失效
     * */
    @POST("/mall/coupon/get")
    suspend fun getCouponList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ListMainBean<CouponData>>


}