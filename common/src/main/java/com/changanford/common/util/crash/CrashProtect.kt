package com.changanford.common.util.crash

import android.Manifest
import android.app.Application
import android.os.Environment
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.FileHelper
import com.changanford.common.util.MConstant
import com.changanford.common.util.TimeUtils
import com.qw.soul.permission.SoulPermission
import java.io.File

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.CrashProtect
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 4:28 PM
 * @Description: 为了让程序出现异常而不崩溃，处理了主线程事件异常，activity启动异常，子线程异常忽略操作。参考代码：https://blog.csdn.net/long8313002/article/details/108422991
 * *********************************************************************************
 */
class CrashProtect {
    var protectStrategyList = arrayListOf<IProtect>()

    init {
        protectStrategyList.add(IProtectApp())
        protectStrategyList.add(IProtectActivityStartUp())
        protectStrategyList.add(IProtectThread())
    }

    fun doProtect(app: Application) {
        protectStrategyList.forEach {
            it.protect(app)
        }
    }

    fun writeLog(e: Throwable) {
        if (MConstant.isCanQeck && SoulPermission.getInstance()
                .checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).isGranted
        ) {
            var msg = e?.toString()
            var phone =
                DeviceUtils.getversionName() + "_" + DeviceUtils.getManuFacture() + "_" + DeviceUtils.getDeviceModel() + "_Android" + DeviceUtils.getDeviceVersion()
            if (e.stackTrace != null && e.stackTrace.isNotEmpty()) {
                for (i in e.stackTrace.indices) {
                    msg = msg.plus("\n" + e.stackTrace[i])
                }
            }
//            var buridData = "\n" + TimeUtils.getsystime() + ":\n" + phone + "\n" + msg
//            BuriedUtil.instant?.crash_report(DeviceUtils.getversionName(),buridData)
            if (MConstant.isCanQeck) {//加入自定义全局异常捕获，保存本地
                FileHelper.saveStringToFile(
                    phone + "\n" + msg,
                    File(
                        Environment.getExternalStorageDirectory()
                            .absolutePath + "/fordlog/crash.txt"
                    )
                )
            }
        }
    }
}