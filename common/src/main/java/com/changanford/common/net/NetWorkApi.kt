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
    /**--------------------------------app---------------------------------------**/
    @POST("/appinit/getpk")
    suspend fun getKey(@Body requestBody: RequestBody): CommonResponse<String>

    @POST("/base/app/getLastestAppVersion")
    suspend fun getUpdateInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UpdateInfo>

    @POST("/con/ads/list")
    suspend fun getHeadBanner(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<AdBean>>

    /**--------------------------------my---------------------------------------**/
    @POST("/base/oss/getSTS")
    suspend fun getOSS(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<STSBean>

    /**
     * 常用功能列表
     */
    @POST("user/navigaMenu/getUserNavigaMenuList")
    suspend fun queryMenuList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<MenuBeanItem>>

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


    /*------------------粉丝 关注---------------------*/

    @POST("userFans/getAllFansOrFollowsList")
    suspend fun queryFansList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FansListBean>


    @POST("userFans/userFollowOrCanaleFollow")
    suspend fun cancelFans(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    //user/getAccountBindList
    @POST("user/getAccountBindList")
    suspend fun queryBindMobileList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<BindAuthBeanItem>>

    /**
     * 绑定三方账号
     */
    @POST("login/bindOauth")
    suspend fun bindOtherAuth(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 注销，验证注销条件
     */
    @POST("user/verifyCancel")
    suspend fun verifyCancelAccount(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CancelVerifyBean>>


    @POST("base/dict/getType")
    suspend fun cancelAccountReason(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CancelReasonBeanItem>>


    /**
     * 获取全部区域
     */
    @POST("base/region/getAllProvinceAndCityRegion")
    suspend fun getAllCity(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CityBeanItem>>


    /**
     * 获取用户地址
     */
    @POST("user/shippingAddress/getUserShippingAddressList")
    suspend fun getAddressList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<AddressBeanItem>>


    /**
     * 保存用户地址
     */
    @POST("user/shippingAddress/saveUserShippingAddress")
    suspend fun saveAddress(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 删除地址地址
     */
    @POST("user/shippingAddress/deleteUserShippingAddressById")
    suspend fun deleteAddress(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /*-------------------圈子---------------------*/

    //con/circle/getJoinCircles 我参与的圈子
    @POST("con/circle/getJoinCircles")
    suspend fun queryMineJoinCircleList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


    //我创建的圈子
    @POST("con/circle/getCreateCircles")
    suspend fun queryMineCreateCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


    //我管理的圈子
    @POST("con/circle/getMyCircles")
    suspend fun queryMineMangerCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CircleMangerBean>>


    //我的圈子 其他状态
    @POST("con/circle/getMyOrtherCircles")
    suspend fun queryMineMangerOtherCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


    //查询申请圈子的人
    @POST("con/circle/getCircleApplyers")
    suspend fun queryJoinCreateCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleMemberBean>

    //已加入圈子的成员
    @POST("con/circle/getCircleUsers")
    suspend fun queryJoinCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleMemberBean>


    //审核加入圈子的人
    @POST("con/circle/auditApplyers")
    suspend fun agreeJoinCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    ///base/config/getConfigByKey    configKey=circle.refuse
    //查询 圈子成员审核失败得标签
    @POST("base/config/getConfigValueByKey")
    suspend fun agreeJoinTags(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleTagBean>


    //圈子审核 人数查询
    @POST("con/circle/getUserCount")
    suspend fun queryCircleCount(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleUserBean>

    //圈子审核 删除
    @POST("con/circle/deleteCircleUsers")
    suspend fun deleteCircleUsers(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 注销账户
     */
    @POST("user/cancel")
    suspend fun cancelAccount(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 解除绑定
     */
    //login/removeOauth
    @POST("login/removeOauth")
    suspend fun unBindMobile(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

}