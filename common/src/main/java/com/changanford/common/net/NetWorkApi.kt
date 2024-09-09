package com.changanford.common.net

import com.alibaba.fastjson.JSONObject
import com.changanford.common.bean.AccBean
import com.changanford.common.bean.ActBean
import com.changanford.common.bean.ActivityBean
import com.changanford.common.bean.ActivityListBean
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.AppNavigateBean
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.AuthBean
import com.changanford.common.bean.BackEnumBean
import com.changanford.common.bean.BindAuthBeanItem
import com.changanford.common.bean.BindCarBean
import com.changanford.common.bean.BizCodeBean
import com.changanford.common.bean.CancelReasonBeanItem
import com.changanford.common.bean.CancelVerifyBean
import com.changanford.common.bean.CarAUthResultBean
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.bean.CarAuthQYBean
import com.changanford.common.bean.CarItemBean
import com.changanford.common.bean.CarMoreInfoBean
import com.changanford.common.bean.CircleItemBean
import com.changanford.common.bean.CircleListBean
import com.changanford.common.bean.CircleMainBean
import com.changanford.common.bean.CircleMemberBean
import com.changanford.common.bean.CircleStatusItemBean
import com.changanford.common.bean.CircleTagBean
import com.changanford.common.bean.CircleUserBean
import com.changanford.common.bean.CityBeanItem
import com.changanford.common.bean.CmcStatePhoneBean
import com.changanford.common.bean.CmcUrl
import com.changanford.common.bean.ConfigBean
import com.changanford.common.bean.CouponMiddleData
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.DaySignBean
import com.changanford.common.bean.FansListBean
import com.changanford.common.bean.FeedbackInfoList
import com.changanford.common.bean.FeedbackMemberBean
import com.changanford.common.bean.FeedbackMineListBean
import com.changanford.common.bean.FeedbackQBean
import com.changanford.common.bean.FeedbackTagsItem
import com.changanford.common.bean.FordPhotosBean
import com.changanford.common.bean.GrowUpBean
import com.changanford.common.bean.GrowUpQYBean
import com.changanford.common.bean.HobbyBeanItem
import com.changanford.common.bean.HotPicBean
import com.changanford.common.bean.IndustryBeanItem
import com.changanford.common.bean.InfoBean
import com.changanford.common.bean.JFExpireBean
import com.changanford.common.bean.JoinCircleCheckBean
import com.changanford.common.bean.JumpDataBean
import com.changanford.common.bean.ListMainBean
import com.changanford.common.bean.LocationDataBean
import com.changanford.common.bean.LoginBean
import com.changanford.common.bean.LoginVideoBean
import com.changanford.common.bean.LoveCarActivityListBean
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.bean.MessageListBean
import com.changanford.common.bean.MessageStatusBean
import com.changanford.common.bean.MiddlePageBean
import com.changanford.common.bean.MineRecommendCircle
import com.changanford.common.bean.MonthSignBean
import com.changanford.common.bean.MyBindCarListBean
import com.changanford.common.bean.MyFastInData
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.OcrBean
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.bean.QueryDetail
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.RecommendListBean
import com.changanford.common.bean.RootTaskBean
import com.changanford.common.bean.STSBean
import com.changanford.common.bean.SettingPhoneBean
import com.changanford.common.bean.ShopBean
import com.changanford.common.bean.SmartCodeBean
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.bean.SpecialDetailData
import com.changanford.common.bean.TaskShareBean
import com.changanford.common.bean.Topic
import com.changanford.common.bean.UpdateActivityV2Req
import com.changanford.common.bean.UpdateInfo
import com.changanford.common.bean.UpdateVoteReq
import com.changanford.common.bean.User
import com.changanford.common.bean.UserIdCardBeanItem
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.bean.WaitReceiveBean
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
    @POST("/appinit/getpk_v2")
    suspend fun getKeyV2(@Body requestBody: RequestBody): CommonResponse<String>
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

    @POST("con/ads/bathList")
    suspend fun getMoreBanner(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CouponMiddleData>

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
    /**
     * 发布活动-新
     */
    @POST("/highlights/v2/addActivity")
    suspend fun ADDActNew(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 发布投票-新
     */
    @POST("/highlights/v2/addVote")
    suspend fun ADDVote(
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

    /**
     * 推荐爱车活动列表
     */
    @POST("/loveCar/recommendList")
    suspend fun loveCarRecommendList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<ActivityListBean>>

    /**
     * 爱车活动列表
     */
    @POST("/loveCar/activityList")
    suspend fun loveCarActivityList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<LoveCarActivityListBean>>

    /**
     * 爱车活动顶部配置
     */
    @POST("base/config/getConfigValueByKey")
    suspend fun getLoveCarConfig(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<JSONObject>

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

    @POST("con/specialTopic/getCarModelList")
    suspend fun getCarModelList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<SpecialCarListBean>>

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
    ): CommonResponse<String>

    /**
     * 获取推荐购入口
     */
    @POST("base/config/getConfigValueByKey")
    suspend fun getTuijianGou(
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
     * 获取基本配置
     * 图片域名
     */
    @POST("/base/config/getConfigValueByKey")
    suspend fun getAppMourningMode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Int>

    /**
     * 用户签到
     */
    @POST("user/signIn")
    suspend fun daySign(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<DaySignBean>
    /**
     * 用户7天签到详情
     */
    @POST("user/sevenDaySignDetail")
    suspend fun day7Sign(
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
     * 登录提示修改头像
     */
    @POST("login/loginTipChangeAvatar")
    suspend fun loginTipChangeAvatar(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 修改头像并解除绑定关系
     */
    @POST("login/updateUserAndRemoveOauth")
    suspend fun updateUserAndRemoveOauth(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 获取个人信息
     */
    @POST("user/myInfo")
    suspend fun queryUserInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UserInfoBean>

    /**
     * 获取h5临时授权Code
     */
    @POST("/idp/getTempCode")
    suspend fun getH5AccessCode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AdBean>

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
    @POST("base/region/cmc/getAllProvinceAndCityRegion")
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
    @POST("con/community/circleJoin")
    suspend fun queryMineJoinCircleList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


    //社区-圈子-我的圈子-星标/取消星标
    @POST("/con/community/circleStar")
    suspend fun circleStar(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<*>

    //我创建的圈子
    @POST("con/circle/getCreateCircles")
    suspend fun queryMineCreateCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


    //我管理的圈子
    @POST("con/community/circleMan")
    suspend fun queryMineMangerCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleListBean>


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
    /**
     *
     * */
    @POST("/highlights/callBackOuterChain")
    suspend fun addactbrid(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<ActBean>>
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
//    @POST("highlights/myPublishes")
    @POST("/highlights/v2/myPublishes")
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
//    @POST("highlights/myCollect")
    @POST("/highlights/v2/myCollect")
    suspend fun queryMineCollectAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>

    //我的足迹活动  highlights/myFootprint
//    @POST("highlights/myFootprint")
    @POST("/highlights/v2/myFootprint")
    suspend fun queryMineFootAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>

    //我参与的活动
//    @POST("highlights/myJoin")
    @POST("/highlights/v2/myJoin")
    suspend fun queryMineJoinAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AccBean>
    //结束活动投票
    @POST("/highlights/v2/endedActivity")
    suspend fun endedActivity(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    //活动修改页面信息，不通过时获取
    @POST("/highlights/v2/activityInfo4Update")
    suspend fun activityInfo4Update(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UpdateActivityV2Req>

    //投票修改页面信息，不通过时获取
    @POST("/highlights/v2/voteInfo4Update")
    suspend fun voteInfo4Update(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<UpdateVoteReq>

    //修改活动
    @POST("/highlights/v2/updateActivity")
    suspend fun updateActivity(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    //修改投票
    @POST("/highlights/v2/updateVote")
    suspend fun updateVote(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>



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

    @POST("/ser/carAuth/cmcImageUpload")
    suspend fun cmcImageUpload(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CmcUrl>

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
    ): CommonResponse<MutableList<BindCarBean>>

    @POST("ser/carAuth/confirmBindCar")
    suspend fun confirmBindCar(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("ser/carAuth/batchConfirmBindCar")
    suspend fun confirmBindCarList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    // 优惠券列表
    @POST("/mall/coupon/receiveList")
    suspend fun receiveList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<CouponsItemBean>>


    // 领取优惠券
    @POST("/mall/coupon/receive")
    suspend fun receiveCoupons(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<*>

    /**
     * 根据类名称获取枚举
     * */
    @POST("/base/dict/getEnum")
    suspend fun dictGetEnum(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<BackEnumBean>>

    /**
     *  我的优惠券配置
     * */
    @POST("base/config/getConfigValueByKey")
    suspend fun myCov(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<MyFastInData>>

    /**
     *  登录成功后获取跳转路由
     * */
    @POST("/userLoginJump/getJump")
    suspend fun loginJump(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<JumpDataBean>

    /**
     *  表示已经跳转过？？？？？？？？？？
     * */
    @POST("/userLoginJump/changeJumpStatus")
    suspend fun changeJumpStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<*>


    @POST("con/circle/carRecommend")
    suspend fun carRecommend(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<MineRecommendCircle>>

    /**
     * 获取订单类型
     * */
    @POST("user/navigaMenu/getUserOrderType")
    suspend fun getOrderKey(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MutableList<MenuBeanItem>>

    @POST("con/circle/myCircles")
    suspend fun myCircles(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<NewCircleBean>>

    // 参与的话题。
    @POST("con/topic/myTopics")
    suspend fun myTopics(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<Topic>>

    // 发起的话题。
    @POST("/con/topic/initiateTopicList")
    suspend fun initiateTopicList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<Topic>>

    //点赞的帖子
    @POST("con/posts/myLikedPosts")
    suspend fun myLikedPosts(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<PostDataBean>>

    //领取交车礼积分
    @POST("/carDeliveryGift/receiveGift")
    suspend fun receiveGift(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<*>

    //待领取交车礼积分列表
    @POST("/carDeliveryGift/waitReceiveList")
    suspend fun waitReceiveList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<WaitReceiveBean>>

    // 领取优惠券
    @POST("/ser/carAuth/myBindCarList")
    suspend fun myBindCarList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<MyBindCarListBean>>

    // 获取福域相册
    @POST("/highlights/v2/d4tPhotos")
    suspend fun getFordPhotos(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FordPhotosBean>

    //获取协议id
    @POST("/con/agreementHub/bizCodes")
    suspend fun bizCode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<BizCodeBean>

    //协议记录提交
    @POST("/con/agreementHub/addRecords")
    suspend fun addRecord(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *   点赞资讯？
     *  /con/article/actionLike
     * */
    @POST("/con/article/actionLike")
    suspend fun actionLike(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 帖子点赞
     */
    @POST("con/posts/actionLike")
    suspend fun actionLikePost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    @POST("/con/agreementHub/bizCode")
    suspend fun getBiz(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<BizCodeBean>

    @POST("/user/getCmcUserStatus")
    suspend fun getCmcUserStatus(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    @POST("base/config/getConfigValueByKey")
    suspend fun queryCmcStatePhone(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CmcStatePhoneBean>

    @POST("con/circle/onlyAuthJoinCheck")
    suspend fun onlyAuthJoinCheck(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<JoinCircleCheckBean>

    /**
     * 爱车-提车日记
     */
    @POST("con/posts/getPosts")
    suspend fun getPosts(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>

    //爱车-购车引导
    @POST("con/specialTopic/detail")
    suspend fun getSpecialTopicList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SpecialDetailData>

    //是否开启智能验证吗
    @POST("base/config/getConfigValueByKey")
    suspend fun smartCode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SmartCodeBean>

    //二次验证智能验证吗
    @POST("/h5/login/getSmsCode_V2")
    suspend fun getSmsCodeV2(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    //查询app底部导航icon配置列表
    @POST("/base/appNavigateIcon/getConfig")
    suspend fun appNavigateIcon(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<AppNavigateBean>

    /**
     * 获取话题列表
     */
    @POST("con/topic/getSugesstionTopics")
    suspend fun getSugesstionTopics(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HotPicBean>

    /**
     * 热门话题
     */
    @POST("/con/community/recommend")
    suspend fun communityTopic(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleMainBean>

    /**
     *   资讯评论
     * */
    @POST("/con/article/addComment")
    suspend fun addCommentNews(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *  帖子评论
     */
    @POST("con/posts/addComment")
    suspend fun addPostsComment(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *  我的足迹-删除
     */
    @POST("/userVisitHistory/deleteByHistoryIdsAndType")
    suspend fun deleteByHistoryIdsAndType(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 退出圈子
     */
    @POST("con/circle/quitCircle")
    suspend fun quitCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 保留圈子弹窗
     */
    @POST("con/movecircle/showwindow")
    suspend fun showWindow(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Boolean>

    /**
     * 福币临期展示和详情接口
     */
    @POST("integralExpireNotice/getIntegralExpireDetails")
    suspend fun getIntegralExpireDetails(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<JFExpireBean>
}
