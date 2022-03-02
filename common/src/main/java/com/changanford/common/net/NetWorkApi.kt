package com.changanford.common.net

import com.changanford.common.bean.*
import com.changanford.common.buried.BaseBean
import io.reactivex.Observable
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

    //分享成功回调
    @POST("/con/share/callback")
    suspend fun ShareBack(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /*退出登录*/
    @POST("login/logout")
    suspend fun loginOut(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取用户拉新分享地址接口
     */
    @POST("/user/inviteNewSubscriber")
    suspend fun inviteShare(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<TaskShareBean>

    /**--------------------------------home---------------------------------------**/
    /**
     * 调查详情/调查结果
     */
    @POST("/highlights/queryDetail")
    suspend fun queryDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<QueryDetail>

    /**
     * 修改调查
     */
    @POST("/highlights/updateQuery")
    suspend fun UPdatQuery(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 发布调查
     */
    @POST("/highlights/addQuery")
    suspend fun addQuery(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 获取可填字段列表
     */
    @POST("/highlights/getAttributes")
    suspend fun getAttributes(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AttributeBean>

    /**
     * 发布活动
     */
    @POST("/highlights/addActivity")
    suspend fun ADDAct(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    //id获取活动详情
    @POST("/highlights/activityDetail")
    suspend fun getActDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ActivityBean>

    /**
     * 修改活动
     */
    @POST("/highlights/updateActivity")
    suspend fun UPdateAct(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    @POST("/common/getCityDetailBylngAndlat")
    suspend fun getCityDetailBylngAndlat(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<LocationDataBean>

    /**--------------------------------car---------------------------------------**/

    @POST("/ser/carAuth/getMyCar")
    suspend fun getMiddlePageInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MiddlePageBean>

    //爱车首页顶部banner
    @POST("/base/centerbanner/list")
    suspend fun getCarTopBanner(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<NewCarBannerBean>>

    //爱车首页
    @POST("/myCarModel/getMyCarModelList")
    suspend fun getMyCarModelList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<NewCarInfoBean>>

    @POST("/ser/carAuth/getMyCar")
    suspend fun getMoreCareInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CarMoreInfoBean>

    //获取最近的一家经销商
    @POST("/baseDealer/nearestlimit")
    suspend fun getRecentlyDealers(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewCarInfoBean>

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
     * 昵称敏感词检查
     */
    @POST("user/vailString")
    suspend fun nameNick(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 保存用户信息
     */
    @POST("user/saveUserInfo")
    suspend fun saveUniUserInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取兴趣爱好
     */
    @POST("user/hobby/getUserHobbyList")
    suspend fun getHobbyList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<HobbyBeanItem>>

    //行业
    @POST("user/industry/getUserIndustryList")
    suspend fun queryIndustryList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<IndustryBeanItem>>

    //是否有未读消息
    @POST("userMessage/getUserMessageStatus")
    suspend fun queryMessageStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MessageStatusBean>

    /**
     * 标记消息已读
     */
    @POST("userMessage/changeAllToRead")
    suspend fun changeAllToRead(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("userMessage/changeUserMessageStatusList")
    suspend fun changAllMessage(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    //获取不同类型消息列表
    @POST("userMessage/getAllUserMessages")
    suspend fun queryMessageList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MessageListBean>

    //删除消息
    @POST("userMessage/delUserMessage")
    suspend fun delUserMessage(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取反馈 常用问题
     */
    @POST("userFeedback/getCommonQuestions")
    suspend fun getFeedbackQ(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FeedbackQBean>

    /**
     * 获取意见反馈 所以标签
     */
    @POST("userFeedback/getAllUserFeedbackTags")
    suspend fun getFeedbackTag(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<FeedbackTagsItem>>

    /**
     * 添加意见反馈 所以标签
     */
    @POST("userFeedback/insertFeedback")
    suspend fun addFeedback(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取的我所以意见反馈
     */
    @POST("userFeedback/getAllUserFeedbacks")
    suspend fun getMineFeedback(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FeedbackMineListBean>

    /**
     * 删除一个意见反馈
     */
    @POST("userFeedback/deleteUserFeedback")
    suspend fun deleteUserFeedback(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("base/config/getConfigValueByKey")
    fun querySettingPhone(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SettingPhoneBean>

    /**
     * 用户已读
     */
    @POST("userFeedback/changeToRead")
    suspend fun changeToRead(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("base/config/getConfigValueByKey")
    suspend fun queryMemberNickName(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FeedbackMemberBean>

    /**
     * 意见反馈 获取内容
     */

    @POST("userFeedback/getAllConversation")
    suspend fun queryFeedbackInfoList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FeedbackInfoList>

    /**
     * 关闭意见反馈
     */

    @POST("userFeedback/closeFeedbackStatus")
    suspend fun closeFeedback(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 添加一条数据意见反馈
     */

    @POST("userFeedback/addConversation")
    suspend fun addFeedbackInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**--------------------------------base---------------------------------------**/

    /**
     * 获取基本配置
     * 图片域名
     */
    @POST("/base/config/getConfigValueByKey")
    suspend fun getConfigByKey(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ConfigBean>

    /**
     * 用户签到
     */
    @POST("user/signIn")
    suspend fun daySign(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<DaySignBean>

    /**
     * 本月签到详情
     */
    @POST("/user/monthSignDetail")
    suspend fun monthSignDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MonthSignBean>

    /**
     * 本周签到详情
     */
    @POST("/user/weekSignDetail")
    suspend fun weekSignDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MonthSignBean>

    /**
     * 用户补签
     */
    @POST("/user/signReissue")
    suspend fun signReissue(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

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
    suspend fun scan(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<JumpDataBean>

    /**
     * 获取验证码
     */
    @POST("login/getFDSmsCode")
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
    suspend fun bindMobile(
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
    @POST("user/accountLog/getIntegralLogList")
    suspend fun mineGrowUp(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<GrowUpBean>

    //user/accountLog/getGrowthLogList
    @POST("user/accountLog/getGrowthLogList")
    suspend fun mineGrowUpLog(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<GrowUpBean>


    @POST("user/memberInterests/getUserMemberInterestsList")
    suspend fun queryUserQy(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<GrowUpQYBean>>

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


    /*--------------会员身份--------------*/
    /**
     * 获取会员身份列表
     */
    @POST("user/member/getUserMemberList")
    suspend fun getUserIdCardList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<UserIdCardBeanItem>>

    //user/member/queryListByUserId

    @POST("user/member/queryListByUserId")
    suspend fun queryLoginUserIdCardList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<UserIdCardBeanItem>>


    /**
     * 查询会员身份
     */

    @POST("user/member/getUserMemberInfo")
    suspend fun getUserIdCard(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AuthBean>


    /**
     * 提交会员身份
     */

    @POST("user/member/apply")
    suspend fun submitUserIdCard(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 显示会员身份
     */

    @POST("user/member/show")
    suspend fun showUserIdCard(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /*-------------------车主认证---------------*/
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

    @POST("ser/carAuth/myCarList")
    suspend fun queryAuthCarList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CarAuthBean>

    @POST("ser/carAuth/getAuthDetail")
    suspend fun queryAuthDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CarItemBean>

    /**
     * 车主认证更新手机号
     */
    @POST("ser/carAuth/crmPhoneUpdate")
    suspend fun uniCarUpdatePhone(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


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
    ): CommonResponse<AddressBeanItem>


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
    ): CommonResponse<ArrayList<CircleItemBean>>


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


    @POST("base/config/getConfigValueByKey")
    suspend fun loginBg(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<LoginVideoBean>


    /**
     * 车主权益
     */
    @POST("base/config/getConfigValueByKey")
    suspend fun carAuthQY(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CarAuthQYBean>


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

    /*创建圈子 体验优化 */
    @POST("con/circle/preCreateCircle")
    suspend fun createCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 查询圈子
     */
    @POST("con/circle/getStarsRole")
    suspend fun queryCircleStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CircleStatusItemBean>>

    /**
     * 设置圈子身份
     */
    @POST("con/circle/setStarsRole")
    suspend fun setCircleStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /*---------------------资讯----------------------*/
    //con/article/myColletArticle  我的收藏 资讯
    @POST("con/article/myColletArticle")
    suspend fun queryMineCollectList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<InfoBean>

    //我的足迹  资讯
    @POST("con/article/myVisitArticle")
    suspend fun queryMineFootprintList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<InfoBean>


    //我发布的资讯
    @POST("con/article/myArticles")
    suspend fun queryMineSendInfoList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<InfoBean>

    /*---------------------帖子-------------------*/

    //我的足迹 con/posts/myVisits
    @POST("con/posts/myVisits")
    suspend fun queryMineFootprintInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>


    //我收藏得帖子
    @POST("con/posts/myCollectList")
    suspend fun queryMineCollectInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>


    //我发布的帖子
    @POST("con/posts/myPostsList")
    suspend fun queryMineSendPost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>


    /*------------------活动----------------------*/

    //highlights/myPublishes 我的活动
    @POST("highlights/myPublishes")
    suspend fun queryMineSendAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>

    //highlights/indexPage4User

    @POST("highlights/indexPage4User")
    suspend fun queryTaSendAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>


    //我收藏的活动  highlights/myCollect
    @POST("highlights/myCollect")
    suspend fun queryMineCollectAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>


    //我的足迹活动  highlights/myFootprint
    @POST("highlights/myFootprint")
    suspend fun queryMineFootAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>

    //我参与的活动
    @POST("highlights/myJoin")
    suspend fun queryMineJoinAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>


    //单个 批量 删除资讯
    @POST("con/article/delete")
    suspend fun deleteInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    //单个 批量 删除资讯
    @POST("con/posts/delete")
    suspend fun deletePost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    //获取他人的用户信息
    @POST("user/otherInfo")
    suspend fun queryOtherInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UserInfoBean>

    //user/medal/getOtherUserMedalList
    @POST("user/medal/getOtherUserMedalList")
    suspend fun queryOtherUserMedal(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<MedalListBeanItem>>

    //结束活动
    @POST("highlights/endedActivity")
    suspend fun endAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UserInfoBean>


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


    /**
     * 申请加精
     */
    @POST("con/posts/setGood")
    suspend fun postSetGood(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("ser/carAuth/ocrDistinguish")
    suspend fun ocr(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<OcrBean>


    //我的足迹 商品
    @POST("mall/myMallFootprint")
    suspend fun queryShopFoot(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ShopBean>

    //我的收藏 商品
    @POST("mall/myMallCollect")
    suspend fun queryShopCollect(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ShopBean>


    /**
     * 添加车牌号
     */

    @POST("ser/carAuth/updatePlateNum")
    suspend fun addCarCardNum(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 更换绑定
     */
    @POST("ser/carAuth/changePhoneBind")
    suspend fun changePhoneBind(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("ser/carAuth/oldPhoneAuth")
    suspend fun changeOldPhoneBind(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    ///ser/carAuth/oldPhoneAuth
    /**
     * U享卡添加车牌号
     */

    @POST("/uniCard/updateOrderPlateNum")
    suspend fun addCarCardNumUni(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    /**
     * 提交车主认证
     */
    @POST("/ser/carAuth/submitAuthApply")
    suspend fun submitCarAuth(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CarAUthResultBean>


    @POST("/con/community/postsAddressAdd")
    suspend fun poastsAddressAdd(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    @POST("/user/getIndexPerms")
    suspend fun getIndexPerms(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<String>>

    /**
     * 埋点
     */
    @POST("/buried")
    fun buried(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): Observable<BaseBean<String>>

    /**
     * 问答tagInfo
     * */
    @POST("base/dict/getType")
    suspend fun getQuestionTagInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<QuestionData>>


    // 设置为默认车辆
    @POST("ser/carAuth/setDefaultCar")
    suspend fun setDefaultCar(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    // 删除车辆
    @POST("ser/carAuth/removeCar")
    suspend fun deleteCar(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>


    // 查询有没有带绑定的车辆， 如果有请弹窗。
    @POST("ser/carAuth/waitBindCarList")
    suspend fun waitBindCarList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>
}
