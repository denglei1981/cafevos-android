package com.changanford.common.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.fetchRequest
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

typealias Block<T> = suspend () -> T
typealias Error = suspend (e: Exception) -> Unit

open class BaseViewModel : ViewModel() {


    /**
     * 是否登录，token不null:true登录，
     */
    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun <T> launch(
        showLoading: Boolean = false,
        block: Block<CommonResponse<T>>,
        error: Error? = null
    ) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                try {
                    block.invoke()
                } catch (e: Exception) {
                    error?.invoke(e)
                    CommonResponse(data = null, msg = e.message ?: "报错", code = 1)
                }

            }
        }
    }
}