package com.changanford.shop.api

import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsList
import com.changanford.common.bean.GoodsTypesBean
import com.changanford.common.bean.SeckillSessionsBean
import com.changanford.common.net.CommonResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * @Author : wenke
 * @Time : 2021/9/30 0030
 * @Description : ShopNetWorkApi
 */
interface ShopNetWorkApi {
    /**
     * 商品详情
     * */
    @POST("/shop/goodsDetail")
    suspend fun queryGoodsDetails(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsItemBean>
    /**
     * 首页点击更多秒杀接口
     * */
    @POST("/mall/sckills/get")
    suspend fun getSckills(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<SeckillSessionsBean>
    /**
     * 秒杀列表
     * */
    @POST("/mall/index")
    suspend fun queryGoodsKillData(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ArrayList<GoodsItemBean>>
    /**
     * 商品分类
     * */
    @POST("/points/typeList")
    suspend fun queryGoodsClassification(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsTypesBean>
    /**
     * 商城列表
     * */
    @POST("/mall/spus/get")
    suspend fun queryGoodsList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<GoodsList>
}