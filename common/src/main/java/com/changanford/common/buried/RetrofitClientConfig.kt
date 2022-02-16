package com.changanford.common.buried

import okhttp3.Interceptor

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: Retrofit + OkHttp相关配置
 */
class RetrofitClientConfig private constructor(builder: Builder) {

    internal var baseUrl: String? = null
    internal var interceptors: ArrayList<Interceptor>? = null


    init {
        baseUrl = builder.baseUrl
        interceptors = builder.interceptors
    }


    /**
     * 创建者模式
     */
    class Builder {

        internal var baseUrl: String? = null
        internal var interceptors: ArrayList<Interceptor>? = null

        /**
         * 服务器根地址
         */
        fun url(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        /**
         * 添加拦截器
         */
        fun addInterceptor(interceptor: Interceptor): Builder {
            if (interceptors == null) {
                interceptors = ArrayList()
            }
            interceptors!!.add(interceptor)
            return this
        }

        fun build() = RetrofitClientConfig(this)

    }

}