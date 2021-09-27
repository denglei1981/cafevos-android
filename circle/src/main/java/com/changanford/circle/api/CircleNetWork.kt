package com.changanford.circle.api

import com.changanford.circle.bean.CircleMainBottomBean
import com.changanford.common.net.CommonResponse
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
    ): CommonResponse<CircleMainBottomBean>
}