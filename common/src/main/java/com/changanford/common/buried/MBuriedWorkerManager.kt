package com.changanford.common.buried

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.changanford.common.basic.BaseApplication

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.other.repository.BuridworkerManager
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/27 19:37
 * @Description: 埋点管理
 * *********************************************************************************
 */
class MBuriedWorkerManager {

    companion object {
        var workerManager: WorkManager = WorkManager.getInstance(BaseApplication.INSTANT)
        var instant: MBuriedWorkerManager? = null
            get() {
                if (field == null) {
                    field = MBuriedWorkerManager()
                }
                return field
            }
    }

    /**
     * 埋点
     * 输入埋点请求的输入body
     */
    fun buried(body: String) {
        val data = Data.Builder().putString("body", body).build()
        val request = OneTimeWorkRequestBuilder<MBuriedWorker>().setInputData(data)
        workerManager.enqueue(request.build())
    }

    /**
     * 下载图片
     */
    fun downLoadPic(lifecycleOwner: LifecycleOwner,urlPath:String){
        val data = Data.Builder().putString("urlPath", urlPath).build()
        val request = OneTimeWorkRequestBuilder<MDownLoadPic>().setInputData(data)
        val workRequest = request.build()
        workerManager.enqueue(workRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.id).observe(lifecycleOwner, Observer {
            if (it != null && it.state.isFinished) {
                var result = it.outputData.getString("urlPath")
//                Log.e("Manager",result?.let { result }?:"保存失败")
            }
        })
    }
}