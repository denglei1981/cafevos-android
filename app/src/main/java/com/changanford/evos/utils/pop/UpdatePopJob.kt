package com.changanford.evos.utils.pop

import android.app.Activity
import android.content.Context
import android.os.Build
import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.dialog.UpdateAlertDialog
import com.changanford.common.ui.dialog.UpdatingAlertDialog
import com.changanford.common.util.APKDownload
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.DownloadProgress
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.toastShow
import com.changanford.evos.PopViewModel

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 版本更新job
 */
class UpdatePopJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    override fun handle(): Boolean {
        val info = popViewModel?.popBean?.value?.updateInfo
        return !(info == null || (info.versionNumber?.toInt()
            ?: 0) <= DeviceUtils.getVersionCode(context))
    }

    override fun launch(callback: () -> Unit) {
        val info = popViewModel?.popBean?.value?.updateInfo ?: return
        val dialog = UpdateAlertDialog(context)
        dialog.builder().setPositiveButton("立即更新") {
            toastShow("正在下载")
            if (!MConstant.isDownloading) {
                val updatingAlertDialog = UpdatingAlertDialog(context)
                val apkDownload = APKDownload()
                updatingAlertDialog.builder().setPositiveButton("取消下载") {
                    apkDownload.cancel()
                    if (info.isForceUpdate == 1) {
                        val activity = context as Activity
                        activity.finish()
                    } else {
                        updatingAlertDialog.dismiss()
                        PopHelper.updateDialog = null
                        callback.invoke()
                    }
                }.setTitle("新版本正在更新，请稍等").setCancelable(info.isForceUpdate != 1).show()
                apkDownload.download(info.downloadUrl ?: "", object : DownloadProgress {
                    override fun sendProgress(progress: Int) {
                        updatingAlertDialog.updateProgress(progress)
                        if (progress == 100) {
                            updatingAlertDialog.setPositiveButton("下载完成") {
                                apkDownload.installAPK()
                                if (info.isForceUpdate == 1) {
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//解决因为去点击权限后返回弹框消失的问题
                                        if (BaseApplication.INSTANT.packageManager.canRequestPackageInstalls()) {
                                            updatingAlertDialog.dismiss()
                                        }
                                    } else {
                                        updatingAlertDialog.dismiss()
                                    }
                                }
                            }
                        }
                    }

                })
            }
            if (info.isForceUpdate == 0) {
                dialog.dismiss()
            }
        }.setNegativeButton("暂不更新") {
            if (info.isForceUpdate == 1) {
                val activity = context as Activity
                activity.finish()
            }
            PopHelper.updateDialog = null
            dialog.dismiss()
            callback.invoke()
        }.setTitle(info.versionName ?: "更新")
            .run {
                setMsg(info.versionContent ?: "体验全新功能")
                setCancelable(false).show()
                PopHelper.updateDialog = this.dialog
            }
        info.downloadUrl?.let {
            MConstant.newApk = true
            MConstant.newApkUrl = it
        }
    }
}