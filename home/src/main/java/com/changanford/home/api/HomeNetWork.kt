package com.changanford.home.api

import com.changanford.common.net.CommonResponse
import com.changanford.home.bean.*

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
     * */
    @POST("con/specialTopic/detail")
    suspend fun getSpecialTopicList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SpecialListMainBean>

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
//    @POST("/userFans/userFollowOrCanaleFollow")
//    suspend fun getToggleFocus(
//        @HeaderMap headMap: Map<String, String>,
//        @Body requestBody: RequestBody
//    ):CommonResponse

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

}