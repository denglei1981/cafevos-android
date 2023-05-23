package com.changanford.common.net

object StatusCode {
    /**
     * 成功
     */
    const val SUCCESS = 0

    /**
     * 失败
     */
    const val ERROR = 500

    /**
     * 业务500 赋值为SERVICE_ERROR 走异常处理 如果需要单独处理业务上的500请判断这个code值
     */
    const val SERVICE_ERROR = 1000003

    /**
     * 未登录
     */
    const val UN_LOGIN = 401

    /**
     * 跳转到找不到页面
     */
    const val REDIRECT_NOT_FOUND_PAGE = 100001

    /**
     * 请求成功，data数据为null,或者返回的列表数据为null
     */
    const val DATA_IS_NULL_ERROR = 100002

    /**
     * 下单地址错误
     */
    const val ERROR_ADDRESS = 1000
}