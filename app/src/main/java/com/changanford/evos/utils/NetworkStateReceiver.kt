package com.changanford.evos.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import java.lang.StringBuilder


class NetworkStateReceiver: BroadcastReceiver() {
    var firstTime = true
    override fun onReceive(context: Context, intent: Intent?) {
        println("网络状态发生变化")
        if (firstTime){
            firstTime = false
            return
        }
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            val wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            //获取移动数据连接的信息
            val dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (wifiNetworkInfo!!.isConnected && dataNetworkInfo!!.isConnected) {
                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show()
            } else if (wifiNetworkInfo!!.isConnected && !dataNetworkInfo!!.isConnected) {
                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show()
            } else if (!wifiNetworkInfo!!.isConnected && dataNetworkInfo!!.isConnected) {
                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show()
            }
//API大于23时使用下面的方式进行网络监听
        } else {
            println("API level 大于23")
            //获得ConnectivityManager对象
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //获取所有网络连接的信息
            val networks: Array<Network> = connMgr.allNetworks
            //用于存放网络连接信息
            var sb = "未连接网络"
            //通过循环将网络信息逐个取出来
            for (i in networks.indices) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                val networkInfo: NetworkInfo? = connMgr.getNetworkInfo(networks[i])
                if (networkInfo?.typeName.equals("WIFI")&&networkInfo?.isConnected == true){
                    sb = "wifi已连接"
                    break
                }else if (networkInfo?.typeName.equals("MOBILE")&&networkInfo?.isConnected==true){
                    sb ="数据流量已连接"
                }
            }
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}