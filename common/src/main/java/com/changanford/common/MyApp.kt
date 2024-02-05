package com.changanford.common

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.changanford.common.basic.BaseApplication
import com.changanford.common.constant.HawkKey
import com.changanford.common.loadsir.EmptyCallback
import com.changanford.common.loadsir.EmptySearchCallback
import com.changanford.common.loadsir.ErrorCallback
import com.changanford.common.loadsir.LoadingCallback
import com.changanford.common.loadsir.TimeoutCallback
import com.changanford.common.manger.UserManger
import com.changanford.common.util.KeyboardVisibilityObserver
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.util.crash.CrashProtect
import com.changanford.common.widget.smart.MyFooterView
import com.changanford.common.widget.smart.MyHeaderView
import com.changanford.common.wutil.ForegroundCallbacks
import com.changanford.common.wutil.SharedPermissionUtils
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.kingja.loadsir.core.LoadSir
import com.lansosdk.videoeditor.LanSoEditor
import com.lansosdk.videoeditor.LanSongFileUtil
import com.orhanobut.hawk.Hawk
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.MyApp
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 17:08
 * @Description: 　
 * *********************************************************************************
 */
class MyApp : BaseApplication(), CameraXConfig.Provider {
    companion object {
        lateinit var mContext: Context

        //静态代码块
        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator(fun(
                context: Context,
                layout: RefreshLayout
            ): MyHeaderView {
                layout.setPrimaryColorsId(R.color.colorAccent, R.color.color_ee);//全局设置主题颜色
//                layout.setEnableLoadMore(false)//禁用加载更多
                layout.setReboundDuration(300)
                return MyHeaderView(context)
            })

            SmartRefreshLayout.setDefaultRefreshFooterCreator(fun(
                context: Context,
                layout: RefreshLayout
            ): MyFooterView {
                layout.setPrimaryColorsId(R.color.colorAccent, R.color.color_ee);//全局设置主题颜色
                layout.setReboundDuration(300)
                return MyFooterView(context)
            })

        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        Hawk.init(this).build()
        SharedPermissionUtils.init(this)
        if (!BuildConfig.DEBUG) CrashProtect().doProtect(this)
        KeyboardVisibilityObserver.getInstance().init(this)
        if (!(SPUtils.getParam(this, "isPopAgreement", true) as Boolean)) {
            LanSoEditor.initSDK(this, "ft")
            LanSongFileUtil.setFileDir(MConstant.ftFilesDir)
        }
        UserManger.getSysUserInfo()?.let {
            MConstant.userId = it.uid
            MConstant.token = SPUtils.getToken()
            MConstant.mine_phone = "${it.mobile}"
        }
        initLoadSir()// 初始化界面管理类。
        isRunInBackGround()
        ForegroundCallbacks.init(this)
        val cmcOpenId = Hawk.get<String>(HawkKey.CMC_OPEN_ID)
        cmcOpenId?.let {
            GrowingAutotracker.get().setLoginUserId(it)
        }
//        DebuggerUtils.checkDebuggableInNotDebugModel(this)
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    private fun initLoadSir() {
        LoadSir.beginBuilder()
            .addCallback(ErrorCallback()) //添加各种状态页
            .addCallback(EmptyCallback())
            .addCallback(EmptySearchCallback())
            .addCallback(LoadingCallback())
            .addCallback(TimeoutCallback())
            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()
    }

    /**
     * app是否处于后台H
     */
    @JvmField
    var isRunBack = false

    /**
     * 判断app是否处于后台 0：后台 ；1：前台
     */
    @JvmField
    var mFinalCount = 0

    /**
     * 判断app是否处于后台
     *
     * @return
     */
    private fun isRunInBackGround() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                mFinalCount++
                if (mFinalCount == 1 && isRunBack) { //说明从后台回到了前台
                    isRunBack = false
                }
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                mFinalCount--
                //如果mFinalCount ==0，说明是前台到后台
                if (mFinalCount == 0) { //说明从前台回到了后台
                    isRunBack = true
//                    WidgetTimerUtils.updateDistance(this@MyApplication)
                }
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {
            }

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}