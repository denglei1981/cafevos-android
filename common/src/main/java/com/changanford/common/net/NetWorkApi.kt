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
     * 获取CAC验证码
     */
    @POST("login/getSmsCode")
    suspend fun sendCacSmsCode(
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
     * 绑定手机
     */
    @POST("login/bindPhone")
    fun bindMobile(
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

    /**
     * 任务列表
     */
    @POST("userTask/getAllTasks")
    suspend fun queryTasksList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<RootTaskBean>>


    //user/accountLog/getUserAccountLogList 成长值
    //
    @POST("user/accountLog/getUserAccountLogList")
    suspend fun mineGrowUp(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<GrowUpBean>

    //user/accountLog/getIntegralLogList
    @POST("user/accountLog/getIntegralLogList")
    suspend fun mineGrowUpNew(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<GrowUpBean>


    /*---------徽章-----------------*/

    /**
     * 获取徽章列表
     */

    @POST("user/medal/getAllMedalList")
    suspend fun queryMedalList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<MedalListBeanItem>>


    /**
     * 佩戴徽章
     */
    @POST("user/medal/wear")
    suspend fun wearMedal(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取用户徽章列表
     */
    @POST("user/medal/getUserMedalList")
    suspend fun queryUserMedalList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<MedalListBeanItem>>


    /**
     * 获取车主认证状态
     */
    @POST("ser/carAuth/isAuthCrmAndIncall")
    suspend fun getAuthStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CarItemBean>>

    @POST("ser/carAuth/getCarAuthAndInCallList")
    suspend fun queryAuthCarAndIncallList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CarItemBean>>

}