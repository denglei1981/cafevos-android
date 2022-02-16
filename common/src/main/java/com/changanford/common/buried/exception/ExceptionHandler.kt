package com.hpb.mvvm.ccc.net.exception

import android.net.ParseException
import com.changanford.common.buried.exception.ApiException
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: 异常监听，将异常转换成自定义的ApiException异常
 */
object ExceptionHandler {

    //对应HTTP的状态码
    private const val UNAUTHORIZED = 401;
    private const val FORBIDDEN = 403;
    private const val NOT_FOUND = 404;
    private const val REQUEST_TIMEOUT = 408;
    private const val INTERNAL_SERVER_ERROR = 500;
    private const val BAD_GATEWAY = 502;
    private const val SERVICE_UNAVAILABLE = 503;
    private const val GATEWAY_TIMEOUT = 504;
    private lateinit var apiExc: ApiException

    /**
     * 根据Throwable新建ApiException
     */
    fun handlerException(e: Throwable): ApiException {
        if (e is HttpException) {
            apiExc = ApiException(e, ERROR.HTTP_ERROR)
            apiExc.msg = when (e.code()) {
                UNAUTHORIZED -> "请求未授权"
                FORBIDDEN -> "服务器拒绝请求"
                NOT_FOUND -> "找不到相关网页"
                REQUEST_TIMEOUT -> "请求超时"
                INTERNAL_SERVER_ERROR -> "服务器错误"
                BAD_GATEWAY -> "错误网关"
                SERVICE_UNAVAILABLE -> "服务器不可用"
                GATEWAY_TIMEOUT -> "网关超时"
                else -> "网络错误"
            }.plus(" (${e.code()})")
        } else if (e is ServerException) {//服务器自定义错误
            apiExc = ApiException(e, e.code)
            apiExc.msg = e.msg
        } else if (e is JsonParseException
            || e is JSONException
            || e is ParseException
        ) {//数据解析错误
            apiExc = ApiException(e, ERROR.PARSE_ERROR)
            apiExc.msg = "解析错误${e.printStackTrace()}"
        } else if (e is ConnectTimeoutException) {
            apiExc = ApiException(e, ERROR.TIMEOUT_ERROR)
            apiExc.msg = "请求超时"
        } else if (e is SocketTimeoutException) {
            apiExc = ApiException(e, ERROR.TIMEOUT_ERROR)
            apiExc.msg = "响应超时"
        } else if (e is ConnectException) {
            apiExc = ApiException(e, ERROR.NETWORK_ERROR)
            apiExc.msg = "连接失败"
        } else if (e is SSLHandshakeException) {
            apiExc = ApiException(e, ERROR.SSL_ERROR)
            apiExc.msg = "证书验证失败"
        } else {
            apiExc = ApiException(e, ERROR.UNKNOWN)
            apiExc.msg = "请稍后再试"
        }
        return apiExc
    }

    /**
     * 根据Throwable判断是否需要重新请求接口
     */
    fun isRetry(e: Throwable): Boolean = when (e) {
        is ConnectTimeoutException -> true//请求超时
        is SocketTimeoutException -> true//响应超时
        is ConnectException -> true//连接失败
        else -> false
    }


}