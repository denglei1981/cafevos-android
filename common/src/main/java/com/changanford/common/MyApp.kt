package com.changanford.common

import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.basic.BaseApplication
import com.changanford.common.manger.UserManger
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.isDebug
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.app.PictureAppMaster
import com.luck.picture.lib.engine.PictureSelectorEngine
import com.changanford.common.widget.smart.MyFooterView
import com.changanford.common.widget.smart.MyHeaderView
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
        UserManger.getSysUserInfo()?.let {
            MConstant.token = "${it.token}"
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
      return  Camera2Config.defaultConfig()
    }


}