package com.changanford.common.buried

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.changanford.common.BuildConfig
import com.changanford.common.buried.exception.ApiException
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.getBuriedHeader
import com.changanford.common.net.getBuriedRequestBody
import com.tencent.mm.opensdk.utils.Log

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.other.repository.BuridWorker
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/27 19:10
 * @Description: 　埋点服务
 * *********************************************************************************
 */
class MBuriedWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val body = inputData.getString("body")
        if (BuildConfig.DEBUG) Log.e("okhttp","body:$body")
        RepositoryManager.obtainService(NetWorkApi::class.java).buried(
            getBuriedHeader(body),
            getBuriedRequestBody(body)
        )
            .compose(ResponseTransformer())
            .subscribe(object : ResponseObserver<BaseBean<String>>(null, true) {

                override fun onFail(e: ApiException) {
//                    Log.e(TAG, e.msg)
                }

                override fun onSuccess(response: BaseBean<String>) {
//                    Log.e(TAG, response.msg)
                }
            })

        return Result.success()
    }
}