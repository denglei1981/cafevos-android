package com.changanford.common.util

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.dialog.NotificationUtils
import com.changanford.common.util.DeviceUtils.getPackageName
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import java.util.*
import kotlin.concurrent.thread


/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.utils.APKDownload
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/6/12 09:52
 * @Description: 　下载更新
 * *********************************************************************************
 */
class APKDownload {
    private var downloadId: Long = 0
    private lateinit var downloadManager: DownloadManager
    private var downloadProgress: DownloadProgress? = null
    lateinit var myTimer: Timer
    var query: DownloadManager.Query = DownloadManager.Query()
    fun download(url: String, downloadProgress: DownloadProgress?) {
        this.downloadProgress = downloadProgress
        SoulPermission.getInstance()
            .checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                object : CheckRequestPermissionListener {

                    override fun onPermissionOk(permission: Permission?) {
                        toDown(url)
                    }

                    override fun onPermissionDenied(permission: Permission?) {
                    }

                })
    }

    private fun toDown(url: String) {
        NotificationUtils.createNotifier(
            BaseApplication.INSTANT,
            "福域",
            "正在下载更新",
            R.mipmap.ic_launcher
        )
        var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedOverRoaming(true)//是否允许漫游
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)//消息提示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("福域")
        request.setDescription("正在下载新包")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "uni"
        )
        thread(start = true) {
            downloadManager =
                BaseApplication.INSTANT.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)
            query.setFilterById(downloadId)
            BaseApplication.INSTANT.registerReceiver(
                broadcastReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            updateViews()
        }
        MConstant.isDownloading = true
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DownloadManager.ACTION_NOTIFICATION_CLICKED == intent?.action) {
                installAPK()
            }
            if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {
                checkState()
            }
        }

    }

    @Synchronized
    private fun checkState() {
        var cursor = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            try {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_PAUSED -> {
                    }
                    DownloadManager.STATUS_PENDING -> {
                    }
                    DownloadManager.STATUS_RUNNING -> {
                    }
                    DownloadManager.STATUS_SUCCESSFUL ->                     //下载完成安装APK
                    {
                        MConstant.isDownloading = false
                        NotificationUtils.cancelNotifier(BaseApplication.INSTANT, 1)
                        installAPK()
                        BaseApplication.INSTANT.unregisterReceiver(broadcastReceiver)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        MConstant.isDownloading = false
                        Toast.makeText(
                            BaseApplication.INSTANT,
                            "下载失败",
                            Toast.LENGTH_SHORT
                        ).show()
                        BaseApplication.INSTANT.unregisterReceiver(broadcastReceiver)
                        myTimer?.cancel()
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        cursor?.close()
    }

    fun installAPK() {
        var downloadUri = downloadManager.getUriForDownloadedFile(downloadId)
        if (downloadUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (BaseApplication.INSTANT.packageManager.canRequestPackageInstalls()) {
                    doInstall(downloadUri)
                } else {
                    startInstallPermissionSettingActivity()
                }
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                val intent = Intent(Intent.ACTION_VIEW)
//                val file = downloadUri.toFile()
//                // 由于没有在Activity环境下启动Activity,设置下面的标签
//                // 由于没有在Activity环境下启动Activity,设置下面的标签
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                //参数1:上下文, 参数2:Provider主机地址 和配置文件中保持一致,参数3:共享的文件
//                //参数1:上下文, 参数2:Provider主机地址 和配置文件中保持一致,参数3:共享的文件
//                val apkUri: Uri = FileProvider.getUriForFile(
//                    context,
//                    "com.hpb.mvvm.fileprovider",
//                    file
//                )
//                //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                doInstall(downloadUri)
            }

        }
    }

    private fun startInstallPermissionSettingActivity() { //注意这个是8.0新API
        val packageURI = Uri.parse("package:" + getPackageName(BaseApplication.INSTANT))
        var intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.curActivity.startActivity(intent);
    }


    private fun doInstall(downloadUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(downloadUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        BaseApplication.INSTANT.startActivity(intent)
    }

    /**
     * 获取下载进度
     */
    private fun updateViews() {
        myTimer = Timer()
        var query: DownloadManager.Query = DownloadManager.Query()
        query.setFilterById(downloadId)
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    var cursor = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        // 已下载的字节大小
                        var downloadedSoFar =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        // 下载文件的总字节大小
                        var totalSize =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (totalSize.toInt()!=0){
                            var progress = (downloadedSoFar * 100 / totalSize).toInt()
                            downloadProgress?.sendProgress(progress)
                            if (progress == 100) {
                                installAPK()
                                cursor?.close()
                                myTimer.cancel()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }, 0, 100);
    }

    /**
     * 取消当前下载
     */
    fun cancel() {
        downloadManager?.remove(downloadId)
        MConstant.isDownloading = false
        NotificationUtils.cancelNotifier(BaseApplication.INSTANT, 1)
        BaseApplication.INSTANT.unregisterReceiver(broadcastReceiver)
        myTimer?.cancel()
    }
}

interface DownloadProgress {
    fun sendProgress(progress: Int)
}
