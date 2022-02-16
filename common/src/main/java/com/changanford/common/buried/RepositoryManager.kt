package com.changanford.common.buried

import com.changanford.common.net.NetWorkApi

/**
 * @Author: hpb
 * @Date: 2020/4/24
 * @Des: 管理网络请求层, 以及数据缓存层
 */
object RepositoryManager : IRepositoryManager {

    /**
     * 存储不同Service接口
     */
    private val hashMap: HashMap<String, Any> = HashMap()

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     */
    override fun <T : Any> obtainService(service: Class<T>): T {
        val canonicalName = service.canonicalName
        if (!hashMap.containsKey(canonicalName)) {
            val retrofitService = RetrofitClient.getInstance().retrofit.create(service)
            if (canonicalName != null) {
                hashMap[canonicalName] = retrofitService!!
            }
            return retrofitService
        }
        return hashMap[canonicalName] as T
    }

    /**
     * 直接获取ApiService基础接口类
     */
    override fun getApiService(): NetWorkApi {
        return obtainService(NetWorkApi::class.java)
    }

}