package com.changanford.home.api

import com.changanford.common.bean.*
import com.changanford.common.net.CommonResponse
import com.changanford.home.bean.*
import com.changanford.home.bean.ListMainBean
import com.changanford.home.data.EnumBean
import com.changanford.home.data.TwoAdData
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.NewsExpandData
import com.changanford.home.news.data.SpecialDetailData
import io.reactivex.Observable

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
interface HomeNetWork {

    /**
     * 专题列表
     */
    @POST("con/specialTopic/list")
    suspend fun getSpecialList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SpecialListMainBean>


    /**
     * /con/specialTopic/detail 专题详情。。
     * con/specialTopic/detail
     * */
    @POST("con/specialTopic/detail")
    suspend fun getSpecialTopicList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SpecialDetailData>

    /**
     *  发现资讯首页。。
     * */
    @POST("con/article/discoverArticleList")
    suspend fun getFindNews(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewsListMainBean>


    /**
     *
     * 随机推荐10个大咖
     * /user/member/recommendBigCoffee
     * */
    @POST("/user/member/recommendBigCoffee")
    suspend fun getRecommendBigShot(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<BigShotRecommendBean>>


    /**
     * /user/toggleFocus
     *  关注用户/取消。
     * */
    @POST("/userFans/userFollowOrCanaleFollow")
    suspend fun followOrCancelUser(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * /con/posts/postsList
     *
     * 大咖: 帖子列表
     * */

    @POST("/con/posts/postsList")
    suspend fun getBigShotPostsList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<BigShotPostBean>>

    /**
     *  /con/article/details
     *  资讯详情。
     * */
    @POST("/con/article/details")
    suspend fun getArticleDetails(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewsDetailData>

    /**
     * 评论点赞
     */
    @POST("con/comment/actionLike")
    suspend fun commentLike(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *  获取评论
     *
     *
     * */

    @POST("/con/comment/commentList")
    suspend fun getCommentList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<CommentListBean>>

    /**
     *  资讯附加信息
     *
     * */
    @POST("/con/artAdditional/get")
    suspend fun getArtAdditional(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewsExpandData>


    /**
     *  添加评论
     * */
    @POST("/con/article/addComment")
    suspend fun addCommentNews(
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
    suspend fun actionPostLike(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 搜索 热门搜索关键词
     * */
    @POST("/con/search/hots")
    suspend fun searchHots(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<SearchKeyBean>>


    /**
     * 搜索自动填充关键词？
     * */
    @POST("/con/search/ac")
    suspend fun searchAc(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<SearchKeyBean>>

    /**
     *搜索作者
     * */
    @POST("/con/search/s")
    suspend fun searchS(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<AuthorBaseVo>>


    /**
     *搜索问答
     * */
    @POST("/con/search/s")
    suspend fun searchAsk(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<AskListMainData>>

    /**
     * 搜索搜索资讯
     */
    @POST("con/search/s")
    suspend fun getSearchNewsList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<InfoDataBean>>

    /**
     * 搜索搜索帖子
     */
    @POST("con/search/s")
    suspend fun getSearchPostList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<PostDataBean>>


    /**
     * 搜索搜索商城
     */
    @POST("con/search/s")
    suspend fun getSearchShopList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<SearchShopBean>>

    /**
     * 搜索搜索活动
     */
    @POST("con/search/s")
    suspend fun getSearchDoingList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<ActBean>>


    /**
     *
     * 搜索关键字
     * */
    @POST("/con/posts/keyWords")
    suspend fun searchKeyWords(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>


    /***
     * /highlights/getHighlights
     *  查询活动。
     * */
    @POST("/highlights/getHighlights")
    suspend fun getHighlights(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<ActBean>>

    /***
     *
     *  活动列表新22-0819。
     * */
    @POST("/highlights/v2/getHighlights")
    suspend fun getHighlightsV2(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<ActBean>>


    /**
     *
     * */
    @POST("/highlights/callBackOuterChain")
    suspend fun addactbrid(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ListMainBean<ActBean>>

    /**
     * 活动头部轮播。
     * */
    @POST("/con/ads/list")
    suspend fun adsLists(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<CircleHeadBean>>

    @POST("con/ads/bathList")
    suspend fun getTwoBanner(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<TwoAdData>

    @POST("con/ads/list")
    suspend fun getRecommendBanner(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<AdBean>>

    @POST("/con/ads/listV2")
    suspend fun getFastEnter(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FastBeanData>


    /**
     * 枚举字典
     */
    @POST("/base/dict/getEnum")
    suspend fun getEnum(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<EnumBean>>

    /**
     *  {
    "collectionContentId": 0,
    "collectionType": 0
    }	收藏类型 1 资讯 2 帖子 3 活动
     */
    @POST("con/collection/post")
    suspend fun collectionApi(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    //分享成功回调
    @POST("/con/share/callback")
    suspend fun ShareBack(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>


    /**
     *举报原因
     */
    @POST("con/reason/tipOffReason")
    suspend fun getTipOffReason(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<String>>

    /**
     * 资讯举报
     */
    @POST("/con/article/addTipOffs")
    suspend fun reportPost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *不喜欢原因
     */
    @POST("con/reason/dislikeReason")
    suspend fun getDislikeReason(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<String>>

    /**
     * 帖子不喜欢
     */
    @POST("con/posts/addDislike")
    suspend fun dislikePost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    @POST("/user/getIndexPerms")
    suspend fun getIndexPerms(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<String>>

    /**
     *判断用户是否有可领取的微客服小程序积分
     * */
    @POST("/userIntegralImport/isGetIntegral")
    suspend fun isGetIntegral(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<FBBean>

    /**
     *领取微客服小程序积分
     * */
    @POST("/userIntegralImport/doGetIntegral")
    suspend fun doGetIntegral(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<WResponseBean>


    @POST("ser/carAuth/confirmBindCar")
    suspend fun confirmBindCar(
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

    //待领取交车礼积分列表
    @POST("/carDeliveryGift/waitReceiveList")
    suspend fun waitReceiveList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<WaitReceiveBean>>

    // 首页广告弹窗
    @POST("/con/ads/newEstTwoList")
    suspend fun newEstOne(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewEstOneBean>

    //首页广告弹窗配置
    @POST("/con/ads/popRule")
    suspend fun popRule(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<NewEstRuleBean>
}