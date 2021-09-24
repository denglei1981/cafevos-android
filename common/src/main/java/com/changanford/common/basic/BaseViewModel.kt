package com.changanford.common.basic

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.fetchRequest
import com.changanford.common.util.MConstant
import kotlinx.coroutines.launch

typealias Block<T> = suspend () -> T

open class BaseViewModel : ViewModel() {


    /**
     * 是否登录，token不null:true登录，
     */
    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun <T> launch(showLoading: Boolean = false,block: Block<CommonResponse<T>>) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                block.invoke()
            }
        }
    }
}