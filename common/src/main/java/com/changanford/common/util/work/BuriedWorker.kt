package com.changanford.common.util.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.work.BuiredWorker
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 17:10
 * @Description:
 * *********************************************************************************
 */
class BuriedWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
    appContext,
    params
) {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}