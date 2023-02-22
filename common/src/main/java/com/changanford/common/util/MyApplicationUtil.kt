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
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.notification.BasicCustomPushNotification
import com.alibaba.sdk.android.push.notification.CustomNotificationBuilder
import com.alibaba.sdk.android.push.register.MeizuRegister
import com.alibaba.sdk.android.push.register.MiPushRegister
import com.alibaba.sdk.android.push.register.OppoRegister
import com.alibaba.sdk.android.push.register.VivoRegister
import com.baidu.location.LocationClient
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.common.BaiduMapSDKException
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.sharelib.ModuleConfigureConstant
import com.changanford.common.sharelib.manager.ShareManager
import com.growingio.android.sdk.autotrack.CdpAutotrackConfiguration
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.giokit.GioKit
import com.lansosdk.videoeditor.LanSoEditor
import com.lansosdk.videoeditor.LanSongFileUtil
import com.tencent.bugly.crashreport.CrashReport

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.evos.MyApplicationUtil
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/12/21 11:03 AM
 * @Description: 　
 * *********************************************************************************
 */
object MyApplicationUtil {
    lateinit var applicationContext: BaseApplication
    fun init(application: BaseApplication) {
        applicationContext = application
    }

    fun init() {
        //Arouter Initial
        if (MConstant.isCanQeck) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(applicationContext)
        LanSoEditor.initSDK(applicationContext, "ft")
        LanSongFileUtil.setFileDir(MConstant.ftFilesDir)
        //阿里云push初始化
        PushServiceFactory.init(applicationContext)
        initBaiduSdk()
        // 在SDK初始化时捕获抛出的异常
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(applicationContext)
        } catch (e: BaiduMapSDKException) {
            //
        }
//        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        initCloudChannel(applicationContext)
        initThirdPush()
        initshare()
        applicationContext.initGio()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initBugfy()
        }

    }

    private fun initBaiduSdk() {
        /**
         * 隐私政策统一接口：：该接口必须在调用SDK初始化接口之前设置
         * 设为false不同意隐私政策：不支持发起检索、路线规划等数据请求，SDK抛出异常；
         * 设为true同意隐私政策：支持发起检索、路线规划等数据请求
         */
        SDKInitializer.setAgreePrivacy(applicationContext, true)
        LocationClient.setAgreePrivacy(true)
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
        var versionName = DeviceUtils.getversionName()
        if (MConstant.isDebug) {
            versionName += ":Test"
        }
        CrashReport.setAppVersion(context, versionName)
        CrashReport.initCrashReport(context, "61884b6924", false)
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
        MiPushRegister.register(applicationContext, "2882303761520063552", "5362006310552");
// 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(applicationContext);
// OPPO通道注册
        OppoRegister.register(
            applicationContext,
            "23f903e9037946efaef146204b6224cf",
            "1cd761f24f6c4ca8ac5e9aca51a270d1"
        ); // appKey/appSecret在OPPO开发者平台获取
// 魅族通道注册
        MeizuRegister.register(
            applicationContext,
            "144598",
            "160d69f44b5c4f7582fd5cb37b318f5f"
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
                val deviceid = pushService.deviceId
                deviceid?.let {
                    SPUtils.setParam(applicationContext, MConstant.PUSH_ID, deviceid)
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
        notification.statusBarDrawable = R.mipmap.fordicon;//设置状态栏图标
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
        ShareManager.initQqSdk(ConfigUtils.QQAPPID, "福域")
    }

}