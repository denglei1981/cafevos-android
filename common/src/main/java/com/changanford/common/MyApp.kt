package com.changanford.common

import android.content.Context
import com.changanford.common.basic.BaseApplication
import com.changanford.common.manger.UserManger
import com.changanford.common.util.MConstant
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
class MyApp : BaseApplication() {
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
}