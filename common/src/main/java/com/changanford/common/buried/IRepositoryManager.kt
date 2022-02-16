package com.changanford.common.buried

import androidx.annotation.NonNull
import com.changanford.common.net.NetWorkApi

/**
 * @Author: hpb
 * @Date: 2020/4/24
 * @Des: 管理网络请求层, 以及数据缓存层
 */
interface IRepositoryManager {

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     */
    fun <T : Any> obtainService(@NonNull service: Class<T>): T

    /**
     * 直接获取基础接口类
     */
    fun getApiService(): NetWorkApi

}