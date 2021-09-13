package com.changanford.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import com.changanford.common.utilext.toastShow


/**
 * @Author: lcw
 * @Date: 2020/11/18
 * @Des:
 */
object JumpMap {
    /**
     * 打开高德地图（公交出行，起点位置使用地图当前位置）
     *
     * t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
     *
     * @param dLat  终点纬度
     * @param dLon  终点经度
     * @param dName 终点名称
     */
    fun openGaoDeMap(context: Activity, dLat: Double, dLon: Double, dName: String) {
        if (checkMapAppsIsExist(context, "com.autonavi.minimap")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.autonavi.minimap")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data =
                Uri.parse("androidamap://route?sourceApplication=长安引力&sName=我的位置&dlat=$dLat&dlon=$dLon&dname=$dName&dev=0&m=0&t=0")
            context.startActivity(intent)
        } else {
            toastShow("高德地图未安装")
        }
    }

    /**
     * 打开百度地图（公交出行，起点位置使用地图当前位置）
     * mode = transit（公交）、driving（驾车）、walking（步行）和riding（骑行）. 默认:driving
     * 当 mode=transit 时 ： sy = 0：推荐路线 、 2：少换乘 、 3：少步行 、 4：不坐地铁 、 5：时间短 、 6：地铁优先
     * @param dLat  终点纬度
     * @param dLon  终点经度
     * @param dName 终点名称
     */
    fun openBaiduMap(context: Activity, dLat: Double, dLon: Double, dName: String) {
        if (checkMapAppsIsExist(context, "com.baidu.BaiduMap")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                "baidumap://map/direction?origin=我的位置&destination=name:"
                        + dName
                        + "|latlng:" + dLat + "," + dLon
                        + "&mode=driving&sy=3&index=0&target=1"
            )
            context.startActivity(intent)
        } else {
            toastShow("百度地图未安装")
        }
    }

    /**
     * 打开腾讯地图（公交出行，起点位置使用地图当前位置）
     *
     * 公交：type=bus，policy有以下取值
     * 0：较快捷 、 1：少换乘 、 2：少步行 、 3：不坐地铁
     * 驾车：type=drive，policy有以下取值
     * 0：较快捷 、 1：无高速 、 2：距离短
     * policy的取值缺省为0
     *
     * @param dlat  终点纬度
     * @param dlon  终点经度
     * @param dname 终点名称
     */
    fun openTencent(
        context: Context,
        dlat: Double,
        dlon: Double,
        dname: String
    ) {
        if (checkMapAppsIsExist(context, "com.tencent.map")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                "qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0"
                        + "&to=" + dname
                        + "&tocoord=" + dlat + "," + dlon
                        + "&type=drive"
                        + "&policy=1&referer=myapp"
            )
            context.startActivity(intent)
        } else {
            toastShow("腾讯地图未安装")
        }
    }

    /**
     * 检测地图应用是否安装
     *
     * @param context
     * @param packagename
     * @return
     */
    private fun checkMapAppsIsExist(context: Context, packagename: String): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = context.packageManager.getPackageInfo(packagename, 0)
        } catch (e: Exception) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }
}