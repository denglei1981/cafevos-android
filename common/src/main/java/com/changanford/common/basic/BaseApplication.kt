package com.changanford.common.basic

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.impl.*
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.register.*
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.changanford.common.sharelib.ModuleConfigureConstant
import com.changanford.common.sharelib.manager.ShareManager
import com.changanford.common.util.ConfigUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MyApplicationUtil
import com.changanford.common.util.SPUtils
import com.changanford.common.utilext.logD
import kotlinx.coroutines.CoroutineScope


abstract class BaseApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        INSTANT = this
        //Arouter Initial
        if (MConstant.isDebug) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application
        //友盟预初始化,不会传数据给后台
//        UMConfigure.preInit(INSTANT,UmengKey,
//            DeviceUtils.getMetaData(INSTANT, "CHANNEL_VALUE"))
        //阿里云push初始化
        PushServiceFactory.init(this)
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        // 获取隐私政策签署状态
        if ((SPUtils.getParam(this, "isPopAgreement", true) as Boolean)) {
            // 没签，等签署之后再调用registerPush()
        } else {
            MyApplicationUtil.init(this)
            initCloudChannel(this)
            initshare()
            initUmeng()
        }
    }


    //友盟初始化
    private fun initUmeng() {
        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
//        UMConfigure.init(this, UmengKey, DeviceUtils.getMetaData(INSTANT, "CHANNEL_VALUE"), UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private fun initCloudChannel(applicationContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        initThirdPush()
        //此处以华为为例
        ThirdPushManager.registerImpl(HuaweiMsgParseImpl())
        ThirdPushManager.registerImpl(XiaoMiMsgParseImpl())
        ThirdPushManager.registerImpl(OppoMsgParseImpl())
        ThirdPushManager.registerImpl(VivoMsgParseImpl())
        ThirdPushManager.registerImpl(MeizuMsgParseImpl())

        val pushService = PushServiceFactory.getCloudPushService()
        pushService.register(applicationContext, object : CommonCallback {
            override fun onSuccess(response: String?) {
                "init cloudchannel success".logD()
                var deviceid = pushService.deviceId
                deviceid?.let {
                    SPUtils.setParam(applicationContext, MConstant.PUSH_ID, deviceid)
                    Log.d("12121", deviceid)
                }
            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                "init cloudchannel failed -- errorcode:$errorCode -- errorMessage:$errorMessage".logD()
            }
        })
    }

    private fun initThirdPush() {
// 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(applicationContext, "2882303761520063552", "5362006310552");
// 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(this);
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

    private fun initshare() {
        ShareManager.initWxShareSdk(ConfigUtils.WXAPPID)
        ShareManager.initSinaSdk(ConfigUtils.WEIBOAPPKEY, ModuleConfigureConstant.REDIRECT_URL, "")
        ShareManager.initQqSdk(ConfigUtils.QQAPPID, "福域")
    }

    override fun onTerminate() {
        super.onTerminate()
        //清理ARouter注册表
        ARouter.getInstance().destroy()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var INSTANT: Application
        lateinit var curActivity: Activity
        lateinit var currentViewModelScope  :CoroutineScope

    }
}