package com.changanford.common.net

import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.FetchNetwork
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 16:01
 * @Description: 　处理网络请求的异常情况
 * *********************************************************************************
 */
suspend fun <T> fetchRequest(
    showLoading: Boolean = false,
    request: suspend ApiClient.() -> CommonResponse<T>
): CommonResponse<T> {
    var dialog: LoadingDialog? = null
    if (showLoading) {
        withContext(Dispatchers.Main) {
            dialog = LoadingDialog(BaseApplication.curActivity)
            dialog?.show()
        }
    }
    var response: CommonResponse<T> = CommonResponse(data = null, msg = "初始化", code = 0)
    response = try {
        request(ApiClient)
    } catch (e: Exception) {
        CommonResponse(data = null, msg = e.message ?: "报错", code = 1)
    } finally {//处理某些特殊情况
        if (showLoading) {
            withContext(Dispatchers.Main) {
                dialog?.dismiss()
            }
        }
    }
    return response
}
