package com.changanford.common.net

import com.changanford.common.bean.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.NetWorkApi
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 14:16
 * @Description: 　
 * *********************************************************************************
 */
interface NetWorkApi {
    @POST("/appinit/getpk")
    suspend fun getKey(@Body requestBody: RequestBody): CommonResponse<String>

    /**
     * 获取基本配置
     * 图片域名
     */
    @POST("/base/config/getConfigValueByKey")
    suspend fun getConfigByKey(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ConfigBean>


    @POST("/goods/getAttributeList")
    suspend fun getUserData(
        @HeaderMap map: HashMap<String, String>?,
        @Body request: RequestBody
    ): CommonResponse<List<User>>

    @POST("con/recommend/list")
    suspend fun getRecommendList(
        @HeaderMap map: HashMap<String, String>?,
        @Body request: RequestBody
    ): CommonResponse<RecommendListBean>

    @POST("/con/ads/list")
    suspend fun getAdList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<AdBean>>

    //扫描二维码 扫一扫
    @POST("base/app/scan")
    fun scan(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CommonResponse<JumpDataBean>>

    /**
     * 获取验证码
     */
    @POST("login/getUNISmsCode")
    suspend fun sendFordSmsCode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 手机验证码登录
     */
    @POST("login/loginBySmsCode")
    suspend fun smsCodeSign(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<LoginBean>

    /**
     * qq 微信 抖音
     */
    @POST("login/oauth")
    suspend fun otherOauthSign(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<LoginBean>

    /**
     * 获取个人信息
     */
    @POST("user/myInfo")
    suspend fun queryUserInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UserInfoBean>

}