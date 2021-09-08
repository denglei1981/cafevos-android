package com.changanford.common.net

import com.changanford.common.util.MConstant.BASE_URL
import com.changanford.common.util.MConstant.isDebug
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.APIClient
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 14:34
 * @Description: 　网络请求客户端，改变user-agent
 * *********************************************************************************
 */
object ApiClient {

    private const val TIME_OUT = 30L
    private val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }
    private val client: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain
                        .request()
                        .newBuilder()
                        .header("User-Agent", "android-OStyle")
                        .build()
                )
            }
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .apply {
                if (isDebug) {
                    this.addInterceptor(HttpLoggingInterceptor().apply {
                        this.level = HttpLoggingInterceptor.Level.BODY
                    })
                }
                this.addInterceptor(DataEncryptInterceptor())
            }.build()

    }
    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    val apiService: NetWorkApi by lazy {
        retrofit.create(NetWorkApi::class.java)
    }

}