package com.changanford.common.buried.exception

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: 自定义异样
 */
class ApiException(val throwable: Throwable, val code: Int) : Exception() {
    lateinit var msg: String
}