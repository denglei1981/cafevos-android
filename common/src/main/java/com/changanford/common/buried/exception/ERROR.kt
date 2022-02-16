package com.hpb.mvvm.ccc.net.exception

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: 自定义报错规则
 */
object ERROR {
    /**
     * 未知错误
     */
    const val UNKNOWN = 1000;

    /**
     * 解析错误
     */
    const val PARSE_ERROR = 1001;

    /**
     * 网络错误
     */
    const val NETWORK_ERROR = 1002;

    /**
     * 协议出错
     */
    const val HTTP_ERROR = 1003;

    /**
     * 证书出错
     */
    const val SSL_ERROR = 1005;

    /**
     * 连接超时
     */
    const val TIMEOUT_ERROR = 1006;
}