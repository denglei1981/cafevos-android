package com.changanford.home.api

import com.changanford.common.net.CommonResponse
import com.changanford.home.bean.*
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.SpecialDetailData

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

    /**
     *  /con/article/details
     *  资讯详情。
     * */
    @POST("/con/article/details")
    suspend fun  getArticleDetails(@HeaderMap headMap: Map<String, String>,
                                   @Body requestBody: RequestBody):CommonResponse<NewsDetailData>


    /**
     *  获取评论
     *
     *  /con/artAdditional/get
     * */

    @POST("/con/comment/commentList")
    suspend fun  getCommentList(@HeaderMap headMap: Map<String, String>,
                            @Body requestBody: RequestBody):CommonResponse<ListMainBean<CommentListBean>>



    /**
     *  添加评论
     * */
    @POST("/con/article/addComment")
    suspend fun  addCommentNews(@HeaderMap headMap: Map<String, String>,
                                @Body requestBody: RequestBody):CommonResponse<Any>




}