package com.changanford.common.net

import android.util.Log
import com.alipay.android.phone.mrpc.core.HttpException
import com.changanford.common.basic.ApiException
import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.LoadingDialog
import com.changanford.common.util.MConstant.isCanQeck
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.FetchNetwork
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 16:01
 * @Description: 　处理网络请求的异常情况
 * *********************************************************************************
 */
suspend fun <T> fetchRequest(
    showLoading: Boolean = false,
    request: suspend ApiClient.() -> CommonResponse<T>
): CommonResponse<T> {
    var dialog: LoadingDialog? = null
    if (showLoading) {
        withContext(Dispatchers.Main) {
            dialog = LoadingDialog(BaseApplication.curActivity)
            dialog?.show()
        }
    }
    return try {
        request(ApiClient)
    } catch (e: Throwable) {
        if (isCanQeck){
            Log.e("fetchRequest:",e.message.toString())
        }
        val apiException = getApiException(e)
        CommonResponse(data = null, msg = apiException.errorMessage ?: "报错", code = 1)
    } finally {//处理某些特殊情况
        if (showLoading) {
            withContext(Dispatchers.Main) {
                if (dialog?.window?.isActive == true) {
                    dialog?.dismiss()
                }
            }
        }
    }
}

/**
 * 请求成功的处理
 */
fun <T> CommonResponse<T>.onSuccess(block: (T?) -> Unit): CommonResponse<T> {
    if (this.code == 0) {//TODO 后面统一处理
        block(this.data)
    }
    return this
}

/**
 * 请求失败或异常的处理
 */
fun <T> CommonResponse<T>.onFailure(block: (T?) -> Unit): CommonResponse<T> {
    if (this.code != 0) {
        //TODO 做一些统一的处理
        block(this.data)
    }
    return this
}

/**
 * 请求失败或异常的处理
 */
fun <T> CommonResponse<T>.onWithCodeFailure(block: (Int) -> Unit): CommonResponse<T> {
    //TODO 做一些统一的处理
    if (this.code != 0) {
        block(this.code)
    }
    return this
}

fun <T> CommonResponse<T>.onWithMsgFailure(block: (msg: String?) -> Unit): CommonResponse<T> {
    if (this.code != 0) {
        //TODO 做一些统一的处理
        block(this.msg)
    }
    return this
}

private fun getApiException(e: Throwable): ApiException {
    return when (e) {
        is UnknownHostException -> {
            ApiException("您的网络不稳定,请刷新重试~", -100)
        }
        is JSONException -> {//|| e is JsonParseException
            ApiException("数据异常", -100)
        }
        is SocketTimeoutException -> {
            ApiException("连接超时", -100)
        }
        is ConnectException -> {
            ApiException("连接错误", -100)
        }
        is HttpException -> {
            ApiException("http code ${e.code}", -100)
        }
        is ApiException -> {
            e
        }
        /**
         * 如果协程还在运行，个别机型退出当前界面时，viewModel会通过抛出CancellationException，
         * 强行结束协程，与java中InterruptException类似，所以不必理会,只需将toast隐藏即可
         */
        is CancellationException -> {
            ApiException("", -10)
        }
        else -> {
            ApiException("未知错误", -100)
        }
    }
}
