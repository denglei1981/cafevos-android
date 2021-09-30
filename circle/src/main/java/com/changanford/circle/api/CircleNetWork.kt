package com.changanford.circle.api

import com.changanford.circle.bean.*
import com.changanford.common.bean.CircleListBean
import com.changanford.common.bean.PostBean
import com.changanford.common.net.CommonResponse
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
interface CircleNetWork {

    /**
     * 获取全部圈子
     */
    @POST("con/circle/getAllCircles")
    suspend fun getAllCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 社区：获取 最热、 最新、精华 推荐 帖子
     */
    @POST("con/posts/getPosts")
    suspend fun getPosts(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>

    /**
     * 获取话题列表
     */
    @POST("con/topic/getSugesstionTopics")
    suspend fun getSugesstionTopics(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HotPicBean>

    /**
     * 获取话题详情
     */
    @POST("con/topic/getTopicInfo")
    suspend fun getSugesstionTopicDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SugesstionTopicDetailBean>

    /**
     * 获取圈子
     */
    @POST("con/circle/getAllTypeCircles")
    suspend fun getAllTypeCircles(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<ChoseCircleBean>>

    /**
     * 查询圈子详情
     */
    @POST("con/circle/getCircleInfo")
    suspend fun queryCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleDetailBean>

    /**
     * 帖子详情
     */
    @POST("con/posts/postsDetail")
    suspend fun getPostsDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostsDetailBean>

    /**
     * 社区 and 我的圈子 ：圈子成员
     */
    @POST("con/circle/getCircleUsers")
    suspend fun getCircleUsers(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<CircleMemberBean>>
}