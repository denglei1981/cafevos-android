package com.changanford.common.buried

import android.util.Log
import com.changanford.common.buried.exception.ApiException
import com.changanford.common.net.StatusCode
import com.changanford.common.util.NetworkUtils
import com.hpb.mvvm.ccc.net.exception.ERROR
import com.hpb.mvvm.ccc.net.exception.ServerException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: 请求响应
 */
private const val NETWORK_CONNECT_MSG = "网络异常，请检查网络"

abstract class ResponseObserver<T> constructor(
    private val vm: BaseViewModel?,
    private val isShowLoading: Boolean
) : Observer<T>, IResponseObserver<T> {

    constructor(vm: BaseViewModel?) : this(vm, false)
    constructor() : this(null)

    override fun onSubscribe(d: Disposable) {
        vm?.addDispose(d)//将请求添加到ViewModel进行管理
        if (!NetworkUtils.isConnected()) {//没有网络
            onError(ServerException(ERROR.NETWORK_ERROR, NETWORK_CONNECT_MSG)) //手动调用网络错误
        } else if (isShowLoading) {
            vm?.showLoadingDialog()
        }
    }

    /**
     * 请求成功
     */
    override fun onNext(t: T) {
        onSuccess(t)
    }

    /**
     * 请求失败
     */
    override fun onError(e: Throwable) {
        Log.e("okhttp", "onError:${e.message}")
        if (e is ApiException) {
            Log.e("okhttp", "onError>>code:${e.code}>>>msg:${e.msg}")
            if (e.code != StatusCode.REDIRECT_NOT_FOUND_PAGE) {
                onFail(e)
            }

        }
        vm?.dismissLoadingDialog()
    }

    /**
     * 请求完成
     */
    override fun onComplete() {
        vm?.dismissLoadingDialog()
    }

}