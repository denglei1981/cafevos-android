package com.changanford.common.buried

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @Author: hpb
 * @Date: 2020/4/24
 * @Des: 拦截切换BaseUrl
 */
class BaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        //获取到请求
        val request = chain.request()
        val baseUrl = request.headers(RetrofitClient.BASE_URL_NAME).let { list ->
            if (list.size > 1) {
                throw IllegalArgumentException("Retrofit Header ‘${RetrofitClient.BASE_URL_NAME}’ 只能有一个!!!")
            }
            request.header(RetrofitClient.BASE_URL_NAME)
        }

        if (!baseUrl.isNullOrEmpty()) {
            val newBuilder = request.newBuilder().apply {
                removeHeader(RetrofitClient.BASE_URL_NAME)
            }
            val baseHttpUrl = baseUrl.toHttpUrl()
            val newUrl = request.url.newBuilder()
                .scheme(baseHttpUrl.scheme)
                .host(baseHttpUrl.host)
                .port(baseHttpUrl.port)
                .build();

            return chain.proceed(newBuilder.url(newUrl).build())
        }

        return chain.proceed(request)
    }

}