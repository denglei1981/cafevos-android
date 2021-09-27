package com.changanford.common.util
import com.changanford.common.MyApp
import java.io.File

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
//    const val BASE_URL = "https://csapi.uniplanet.cn"
    const val BASE_URL = "https://evosapiqa.changanford.cn"
    const val isDebug = true
    const val LOGIN_TOKEN = "LOGIN_TOKEN"
    const val APP_MD5_KEY = "J5i6UkJi8voBEEyE1g5q"
    const val IMGURLTAG = "image_url_tag"
    const val COOKIE = false
    const val isAppAlive = true

    val rootPath by lazy{
        MyApp.mContext.getExternalFilesDir("")?.absolutePath
    }

    val ftFilesDir by lazy{
        rootPath+ File.separator + "android" + File.separator + "ftfilesdir" + File.separator
    }

    val saveIMGpath by lazy {
        rootPath+ File.separator + "android" + File.separator + "ftfilesdir" + File.separator+System.currentTimeMillis()+".jpg"
    }

    val H5_BASE_URL_CSCIR by lazy {
        "https://cir.uni.changan.com.cn"
    }


    var pubKey = ""
    var token: String = ""
    var imgcdn = ""
    var userId = ""

    var totalWebNum = 0//AgentWebActivity的个数
    //app更新
    var isDownloading = false//是否下载
    var newApk = false//是否有新版本
    var newApkUrl = ""//新版本的链接

    const val WXAPPID = "wx134a7f5ed01da769" //微信appid

    const val QQAPPID = "1109690244" //QQ
    var NUM = "" //

    /**
     * 路由设置为100的，路由拦截登录
     */
    const val ROUTER_LOGIN_CODE: Int = 100


    //ARouter拦截登录
    const val LOGIN_INTERCEPT = "intercept_login"

    //ARouter登录拦截地址wifi
    const val LOGIN_INTERCEPT_PATH = "intercept_login_path"

    const val PUSH_ID = "sys:pushid"

    var H5_privacy = "https://cir.uni.changan.com.cn/quanzi/#/privacy"//隐私协议
    var H5_regTerms = "https://cir.uni.changan.com.cn/quanzi/#/regTerms" //注册协议
    /**
     * https://cscir.uniplanet.cn/quanzi/#/regTerms
     * 引力域注册协议
     */
    var H5_REGISTER_AGREEMENT = "${H5_BASE_URL_CSCIR}/quanzi/#/regTerms"

    /**
     * 用户隐私
     */
    var H5_USER_AGREEMENT = "${H5_BASE_URL_CSCIR}/quanzi/#/privacy"

    /**
     * 注销协议
     */
    var H5_CANCEL_ACCOUNT = "${H5_BASE_URL_CSCIR}/quanzi/#/cancellation"


}