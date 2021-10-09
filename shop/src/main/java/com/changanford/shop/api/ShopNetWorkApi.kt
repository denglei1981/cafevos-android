package com.changanford.shop.api

import com.changanford.common.bean.*
import com.changanford.common.net.CommonResponse
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
    suspend fun queryGoodsDetails(@Path("spuId")spuId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsDetailBean>
    /**
     * 首页点击更多秒杀接口
     * */
    @POST("/mall/sckills/get")
    suspend fun getSckills(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<SeckillSessionsBean>
    /**
     * 商城列表
     * */
    @POST("/mall/spus/get")
    suspend fun queryGoodsList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsHomeBean>
    /**
     * 首页
     * */
    @POST("/mall/index")
    suspend fun queryShopHomeData(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ShopHomeBean>
    /**
     * 秒杀列表
     * */
    @POST("/mall/seckills/{seckillRangeId}/spus/get")
    suspend fun getGoodsKillList(@Path("seckillRangeId")seckillRangeId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsList>
    /**
     *秒杀提醒设置/取消
    * */
    @POST("/mall/seckills/spus/{rangeId}/noticesSetting")
    suspend fun setKillNotices(@Path("rangeId")rangeId:String,@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>
    /**
     *秒订单评价
     * */
    @POST("/mall/eval/create")
    suspend fun orderEval(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>

    /**
     *评价列表
     * */
    @POST("/mall/eval/list")
    suspend fun orderEvalList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<CommentBean>
    /**
     *订单创建
     * */
    @POST("/mall/order/create")
    suspend fun orderCreate(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<*>

}