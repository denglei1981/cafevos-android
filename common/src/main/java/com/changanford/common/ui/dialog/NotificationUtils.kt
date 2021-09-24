package com.changanford.common.ui.dialog

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.widget.dialog.NotificationUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/6/16 13:33
 * @Description: 　系统栏消息弹框
 * *********************************************************************************
 */
class NotificationUtils {

    companion object {
        var channelId = "channelId222"
        var channelName = "This is a channel Name"
        var channelDesc = "This is a channel Description"
        var id = 1
        fun createNotifier(context: Context, title: String, content: String, icon: Int) {
            var manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var build = NotificationCompat.Builder(context, channelId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = channelDesc
                manager.createNotificationChannel(channel)
                build.setChannelId(channelId)
                build.setDefaults(Notification.FLAG_AUTO_CANCEL)
                build.setDefaults(Notification.DEFAULT_SOUND)
            }
            var notifier: Notification = build
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .build()
            manager.notify(id, notifier)
        }
        fun cancelNotifier(context: Context,mId:Int){
            var manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(id)
        }
    }

}