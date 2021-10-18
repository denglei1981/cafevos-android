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
 * 商品详情：`/goodsDetail?goodsId=123`
圈子详情： `/circleDetail?circleId=94`
话题详情： `/topicDetail?topicId=178`
帖子详情(图文postsId=8528853，图片，视频postsId=8452106):  `/postDetail?postsId=8528853`
资讯详情（图文?artId=830，图片，视频826）: `/articleDetail?artId=830`
专题详情: `/specialDetail?spId=660`
任务规则说明: `/taskExplain`
关于成长值: `/growthValue`
注册: `/register`
隐私协议: `/privacy`
注册协议: `/regTerms`
注销协议: `/cancellation`
 * *********************************************************************************
 */
object MConstant {
    //    const val BASE_URL = "https://csapi.uniplanet.cn"
    const val BASE_URL = "https://evosapiqa.changanford.cn"
    const val isDebug = true
    const val LOGIN_TOKEN = "LOGIN_TOKEN"
    const val APP_MD5_KEY = "J5i6UkJi8voBEEyE1g5q"
    const val IMGURLTAG = "image_url_tag"
    const val LOCATION_BD = "location_bd"

    const val COOKIE = false
    const val isAppAlive = true
    var isPopAgreement = true//是否要弹隐私弹框，true未同意协议

    val rootPath by lazy {
        MyApp.mContext.getExternalFilesDir("")?.absolutePath
    }

    val ftFilesDir by lazy {
        rootPath + File.separator + "android" + File.separator + "ftfilesdir" + File.separator
    }

    val saveIMGpath by lazy {
        rootPath + File.separator + "android" + File.separator + "ftfilesdir" + File.separator + System.currentTimeMillis() + ".jpg"
    }

    val H5_BASE_URL_CSCIR by lazy {
        "https://evosh5qa.changanford.cn/common/#"
    }

    /**
     * 刷新用户消息  true刷新
     */
    const val REFRESH_USER_INFO = "mine:refresh_user_info"  //


    var pubKey = ""
    var token: String = ""
    var imgcdn = ""
    var userId = ""

    var totalWebNum = 0//AgentWebActivity的个数

    //app更新
    var isDownloading = false//是否下载
    var newApk = false//是否有新版本
    var newApkUrl = ""//新版本的链接
    var mine_phone = ""

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
    const val UmengKey = ""

    var H5_privacy = "${H5_BASE_URL_CSCIR}/privacy"//隐私协议
    var H5_regTerms = "${H5_BASE_URL_CSCIR}/regTerms" //注册协议

    /**
     * 任务说明
     */
    var H5_TASK_RULE = "${H5_BASE_URL_CSCIR}/taskExplain"

    /**
     * https://cscir.uniplanet.cn/quanzi/#/regTerms
     * 注册协议
     */
    var H5_REGISTER_AGREEMENT = H5_regTerms

    /**
     * 用户隐私
     */
    var H5_USER_AGREEMENT = H5_privacy

    /**
     * 注销协议
     */
    var H5_CANCEL_ACCOUNT = "${H5_BASE_URL_CSCIR}/cancellation"

    /**
     * 积分规则
     */
    var H5_MINE_INTEGRAL = "${H5_BASE_URL_CSCIR}/taskExplain"

    var H5_MINE_GROW_UP = "${H5_BASE_URL_CSCIR}/growthValue"

}