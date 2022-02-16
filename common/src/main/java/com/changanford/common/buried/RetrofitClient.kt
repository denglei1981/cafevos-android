package com.changanford.common.buried

import com.changanford.common.basic.BaseApplication
import com.changanford.common.net.DataEncryptInterceptor
import com.changanford.common.util.MConstant
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: Retrofit + OkHttp实现类
 */
class RetrofitClient private constructor() {

    private lateinit var httpClient: OkHttpClient
    lateinit var retrofit: Retrofit
        private set

    init {
        initHttpClient()
        initRetrofit()
    }

    /**
     * 初始化OkHttp相关配置
     */
    private fun initHttpClient() {
        httpClient = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)//连接
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)//读
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)//写
            .cache(
                Cache(
                    BaseApplication.INSTANT.cacheDir,
                    HTTP_RESPONSE_DISK_CACHE_MAX_SIZE
                )
            )//缓存目录及大小
            .addInterceptor(BaseUrlInterceptor())//拦截切换BaseUrl
            .addInterceptor(DataEncryptInterceptor())//数据解析
            .addInterceptor(HttpLoggingInterceptor().setLevel(if (MConstant.isCanQeck) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE))
            .apply {
                retrofitClientConfig?.let { config ->
                    if (config.interceptors != null) {
                        for (inter in config.interceptors!!) {
                            addInterceptor(inter)//添加拦截器
                        }
                    }
                }
            }
            .build()
    }

    /**
     * 初始化Retrofit相关配置
     */
    private fun initRetrofit() {
        val gsonBuilder = GsonBuilder().apply {
            registerTypeAdapter(Int::class.java, IntTypeAdapter())
            registerTypeAdapter(String::class.java, StringTypeAdapter())
            registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
            registerTypeAdapter(Number::class.java, NumberTypeAdapter())
            registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            registerTypeAdapter(Long::class.java, LongTypeAdapter())
        }
        retrofit = Retrofit.Builder()
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//RxJava
            .addConverterFactory(ScalarsConverterFactory.create())//Java基础类转换器
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))//使用Gson转换器
            .apply {
                var url: String? = retrofitClientConfig?.baseUrl
                baseUrl(url ?: "https://api.github.com/")
            }.build()
    }

    companion object {

        private const val TIME_OUT = 30L //超时：秒
        private const val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 1024L * 1024L * 100//缓存大小：100M
        const val BASE_URL_NAME = "BaseUrl-Name"//Header切换BaseUrl字段名
        internal val httpUrlMap: HashMap<String, HttpUrl> = HashMap()
        private var INSTANCE: RetrofitClient? = null
        private var retrofitClientConfig: RetrofitClientConfig? = null//Retrofit + OkHttp相关配置

        /**
         * 单例
         */
        @JvmStatic
        fun getInstance() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: RetrofitClient().also { INSTANCE = it }
        }

        /**
         * 设置相关配置
         */
        fun setRetrofitClientConfig(retrofitClientConfig: RetrofitClientConfig) {
            this.retrofitClientConfig = retrofitClientConfig
        }
    }

}