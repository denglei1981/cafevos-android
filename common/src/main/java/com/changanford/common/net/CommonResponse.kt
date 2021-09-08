package com.changanford.common.net

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.BaseResponse
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 15:55
 * @Description: 　请求返回基类
 * *********************************************************************************
 */
class CommonResponse<T>(
    var code: Int = 0,
    val message: String? = "",
    var msg: String? = null,
    var data: T? = null,
    var timestamp: String? = null,
    val msgId: String? = "",
    val encr: Boolean? = false,
)