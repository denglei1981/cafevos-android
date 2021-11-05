package com.changanford.common.basic

/**
 * 用来封装业务错误信息
 *
 * @author lw
 * @date 2021-11-04
 */
class ApiException(val errorMessage: String, val errorCode: Int) : Throwable()