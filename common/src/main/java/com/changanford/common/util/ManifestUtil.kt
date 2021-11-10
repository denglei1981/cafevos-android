package com.changanford.common.util

import android.content.Context
import android.text.TextUtils
import com.changanford.common.MyApp
import com.changanford.common.utilext.logE
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 *  文件名：ManifestUtil
 *  创建者: zcy
 *  创建日期：2021/11/10 15:08
 *  描述: TODO
 *  修改描述：TODO
 */
object ManifestUtil {

    const val START_FLAG = "META-INF/channel_"

    /**
     * 获取META-INFO下面的渠道
     *
     * @param context
     * @return
     */
    fun getChannel(context: Context): String {
        var channel = SPUtils.getParam(
            context, MConstant.FORD_CHANNEL, ""
        ) as String
        if (!TextUtils.isEmpty(channel)) {
            return channel
        }
        val appinfo = context.applicationInfo
        val sourceDir = appinfo.sourceDir
        var zipfile: ZipFile? = null
        try {
            zipfile = ZipFile(sourceDir)
            val entries: Enumeration<*> = zipfile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                val entryName = entry.name
                if (entryName.contains(START_FLAG)) {
                    channel = entryName.replace(START_FLAG.toRegex(), "")
                    "渠道>>$channel".logE()
                    SPUtils.setParam(context, MConstant.FORD_CHANNEL, channel)
                    return channel
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return DeviceUtils.getMetaData(
            MyApp.mContext, "CHANNEL_VALUE"
        )
    }
}