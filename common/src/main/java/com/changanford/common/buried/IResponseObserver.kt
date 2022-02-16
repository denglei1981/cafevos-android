package com.changanford.common.buried

import com.changanford.common.buried.exception.ApiException

/**
 * @Author: hpb
 * @Date: 2020/4/26
 * @Des: 网络请求监听
 */
internal interface IResponseObserver<T> {

    /**
     * 成功
     */
    fun onSuccess(response: T)

    /**
     * 失败
     */
    fun onFail(e: ApiException)
}