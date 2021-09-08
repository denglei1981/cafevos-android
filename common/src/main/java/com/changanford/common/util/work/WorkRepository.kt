package com.changanford.common.util.work

import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.changanford.common.MyApp

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.work.WorkRepository
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/23 10:34
 * @Description: 　
 * *********************************************************************************
 */
suspend inline fun <reified T:CoroutineWorker> doOneWork() {
    val workQueue = OneTimeWorkRequestBuilder<T>().build()
    val workManager = WorkManager.getInstance(MyApp.mContext).enqueue(workQueue)
    workManager.result
}