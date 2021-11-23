package com.changanford.common

import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.changanford.common.basic.BaseApplication
import com.changanford.common.loadsir.EmptyCallback
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
import com.kingja.loadsir.core.LoadSir
import com.lansosdk.videoeditor.LanSoEditor
import com.lansosdk.videoeditor.LanSongFileUtil
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
        CrashProtect().doProtect(this)
        KeyboardVisibilityObserver.getInstance().init(this)
        LanSoEditor.initSDK(this, "ft")
        LanSongFileUtil.setFileDir(MConstant.ftFilesDir)
        UserManger.getSysUserInfo()?.let {
            MConstant.userId = it.uid
            MConstant.token = SPUtils.getToken()
            MConstant.mine_phone = "${it.mobile}"
        }
        initLoadSir()// 初始化界面管理类。
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    private fun initLoadSir() {
        LoadSir.beginBuilder()
            .addCallback(ErrorCallback()) //添加各种状态页
            .addCallback(EmptyCallback())
            .addCallback(LoadingCallback())
            .addCallback(TimeoutCallback())
            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()
    }

}