package com.changanford.common.net

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
     * 获取验证码
     */
    @POST("login/getUNISmsCode")
    suspend fun sendFordSmsCode(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>
}