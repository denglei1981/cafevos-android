package com.changanford.common.buried

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: 服务器错误
 */
class ServerException(val code: Int, val msg: String) : RuntimeException()