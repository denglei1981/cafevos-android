package com.changanford.common.util

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
    const val IMGURLTAG = "image_url_tag"
    const val COOKIE = false
    const val isAppAlive = true

    var pubKey = ""
    var token = ""
    var imgcdn = ""
    var userId = ""

    var totalWebNum = 0//AgentWebActivity的个数
    //app更新
    var isDownloading = false//是否下载
    var newApk = false//是否有新版本
    var newApkUrl = ""//新版本的链接

    //ARouter拦截登录
    const val LOGIN_INTERCEPT = "intercept_login"
    //ARouter登录拦截地址wifi
    const val LOGIN_INTERCEPT_PATH = "intercept_login_path"
}