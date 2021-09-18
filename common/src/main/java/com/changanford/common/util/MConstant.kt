package com.changanford.common.util

import com.changanford.common.MyApp

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.Constants
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 15:43
 * @Description: 　
 * *********************************************************************************
 */
object MConstant {
    const val BASE_URL = "https://csapi.uniplanet.cn"
    const val isDebug = true
    const val LOGIN_TOKEN = "LOGIN_TOKEN"
    const val APP_MD5_KEY = "J5i6UkJi8voBEEyE1g5q"
    const val COOKIE = false
    val rootPath = MyApp.mContext.getExternalFilesDir("")?.absolutePath
    var pubKey = ""
    var token: String = ""
    var imgcdn = ""
    var userId = ""

    var totalWebNum = 0//AgentWebActivity的个数

    const val WXAPPID = "wx134a7f5ed01da769" //微信appid

    const val QQAPPID = "1109690244" //QQ
    var NUM = "" //


    //ARouter拦截登录
    const val LOGIN_INTERCEPT = "intercept_login"

    //ARouter登录拦截地址wifi
    const val LOGIN_INTERCEPT_PATH = "intercept_login_path"
}