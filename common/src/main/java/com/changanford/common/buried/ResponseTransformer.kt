package com.changanford.common.buried

import com.changanford.common.net.StatusCode
import com.hpb.mvvm.ccc.net.exception.ExceptionHandler
import com.hpb.mvvm.ccc.net.exception.ServerException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * @Author: hpb
 * @Date: 2020/4/24
 * @Des: 请求响应变换类 -- 进行数据判断，线程切换，异常处理，重新请求
 */
private const val MAX_RETRY_NUM = 2L //当请求失败时需要重新订阅的次数

class ResponseTransformer<Stream> : ObservableTransformer<Stream, Stream> {

    override fun apply(upstream: Observable<Stream>): ObservableSource<Stream> =
        upstream.map { response ->
            //判断是否为基础数据类型，是否成功
            if (response is BaseBean<*>
                && response.code != StatusCode.SUCCESS
            ) {
                //抛出自定义接口异常
                throw ServerException(response.code, response.msg)
            }
            response
        }.retry(MAX_RETRY_NUM) { e ->
            ExceptionHandler.isRetry(e)//判断是否需要重新请求
        }.subscribeOn(Schedulers.io())//IO线程
            .observeOn(AndroidSchedulers.mainThread())//主线程
            // 当捕获到错误时，抛出自定义异常结束
            .onErrorResumeNext(Function { throwable ->
                Observable.error(ExceptionHandler.handlerException(throwable))
            })


}