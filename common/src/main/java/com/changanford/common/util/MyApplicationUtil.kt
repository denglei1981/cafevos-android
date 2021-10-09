package com.changanford.common.util

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.notification.BasicCustomPushNotification
import com.alibaba.sdk.android.push.notification.CustomNotificationBuilder
import com.alibaba.sdk.android.push.register.MeizuRegister
import com.alibaba.sdk.android.push.register.MiPushRegister
import com.alibaba.sdk.android.push.register.OppoRegister
import com.alibaba.sdk.android.push.register.VivoRegister
import com.changanford.common.R
import com.changanford.common.sharelib.ModuleConfigureConstant
import com.changanford.common.sharelib.manager.ShareManager
import com.tencent.bugly.crashreport.CrashReport

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changan.uni.MyApplicationUtil
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/12/21 11:03 AM
 * @Description: 　
 * *********************************************************************************
 */
object MyApplicationUtil {
    lateinit var applicationContext : Application
    fun init(application:Application) {
        applicationContext = application
    }

    fun init(){
        initCloudChannel(applicationContext)
        initThirdPush()
        initshare()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initBugfy()
        }

    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initBugfy() {
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName: String =
            MultiDexApplication.getProcessName()
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
//         初始化Bugly
        var versionName = "${BuildConfig.VERSION_NAME}"
        if (MConstant.isDebug) {
            versionName += ":Test"
        }
//        CrashReport.setAppVersion(context, versionName)
//        CrashReport.initCrashReport(context, "xxxxxxxx", false)
        val token: String = MConstant.token
        if (!TextUtils.isEmpty(token)) {
            CrashReport.setUserId(token)
        }
    }

    /**
     * 其他通道初始化
     * 辅助通道注册务必在Application中执行且放在推送SDK初始化代码之后，否则可能导致辅助通道注册失败
     */
    private fun initThirdPush() {
// 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(applicationContext, "2882303761518426654", "5851842653654");
// 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(applicationContext);
// OPPO通道注册
        OppoRegister.register(
            applicationContext,
            "2f8426722b6949cc9d604c9ae592b731",
            "c433b849b9b24887b0cca87db231a011"
        ); // appKey/appSecret在OPPO开发者平台获取
// 魅族通道注册
        MeizuRegister.register(
            applicationContext,
            "130281",
            "b637cb29244847ee8b276c9003a45fe7"
        ); // appId/appkey在魅族开发者平台获取
// VIVO通道注册
        VivoRegister.register(applicationContext);
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private fun initCloudChannel(applicationContext: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // 通知渠道的id
            val id = "1"
            // 用户可以看到的通知渠道的名字.
            val name: CharSequence = "notification channel"
            // 用户可以看到的通知渠道的描述
            val description = "notification description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            // 配置通知渠道的属性
            mChannel.description = description
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel)
        }


        PushServiceFactory.init(applicationContext)
        val pushService = PushServiceFactory.getCloudPushService()
        pushService.register(applicationContext, object : CommonCallback {
            override fun onSuccess(response: String) {
                Log.d("MyApplication", "init cloudchannel success")
                var deviceid = pushService.deviceId
                deviceid?.let {
                    SPUtils.setParam(applicationContext, MConstant.PUSH_ID, deviceid)
                    Log.d("12121", deviceid)
                }
            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                Log.d(
                    "MyApplication",
                    "init cloudchannel failed -- errorcode:$errorCode -- errorMessage:$errorMessage"
                )
            }
        })

        /**
         * 设置基础自定义样式通知示例
         * 1. 详细API介绍请参考:https://help.aliyun.com/document_detail/30066.html#h3-3-4-basiccustompushnotification-api
         */
        var notification = BasicCustomPushNotification();
        notification.remindType = BasicCustomPushNotification.REMIND_TYPE_SOUND;//设置提醒方式为声音
        notification.statusBarDrawable = R.mipmap.ic_launcher;//设置状态栏图标
        var res = CustomNotificationBuilder.getInstance()
            .setCustomNotification(1, notification);//注册该通知,并设置ID为1

//        var advancedCustomPushNotification =  AdvancedCustomPushNotification(R.layout.notifview, R.id.m_icon, R.id.m_title, R.id.m_text);
//        advancedCustomPushNotification.icon = R.mipmap.ic_launcher;
//        advancedCustomPushNotification.statusBarDrawable = R.mipmap.ic_launcher;//设置状态栏图标
//        advancedCustomPushNotification.remindType = BasicCustomPushNotification.REMIND_TYPE_SOUND;//设置提醒方式为声音
//        var res = CustomNotificationBuilder.getInstance()
//            .setCustomNotification(1, advancedCustomPushNotification);//注册该通知,并设置ID为1
    }
    private fun initshare() {
        ShareManager.initWxShareSdk(ConfigUtils.WXAPPID)
        ShareManager.initSinaSdk(ConfigUtils.WEIBOAPPKEY, ModuleConfigureConstant.REDIRECT_URL, "")
        ShareManager.initQqSdk(ConfigUtils.QQAPPID, "UNI")
    }
}