package com.changanford.common.util

import android.content.Context
import android.net.ConnectivityManager
import com.changanford.common.MyApp


/**
 * @Author: hpb
 * @Date: 2020/4/29
 * @Des: 网络相关
 */
object NetworkUtils {

    fun isConnected(): Boolean {
        val mConnectivityManager = MyApp.mContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable
        }
        return false
    }
}