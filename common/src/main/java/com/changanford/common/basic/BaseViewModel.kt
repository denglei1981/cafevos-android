package com.changanford.common.basic

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

typealias Block<T> = suspend () -> T
typealias Error = suspend (e: Throwable) -> Unit

open class BaseViewModel : ViewModel() {
    protected var pageSize = 20
    protected val body = MyApp.mContext.createHashMap()

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
                } catch (e: Throwable) {
                    error?.invoke(e)
                    CommonResponse(data = null, msg = e.message ?: "报错", code = 1)
                }
            }
        }
    }


    // 根据 枚举类型 获取 枚举 然后翻译成中文
    fun StatusEnum(enumClassName: String, english: String, tvTips: TextView) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["className"] = enumClassName
            ApiClient.createApi<NetWorkApi>()
                .dictGetEnum(body.header(rKey), body.body(rKey))
                .onSuccess {
                    it?.forEach { en ->
                        if (en.code == english) {
                            tvTips.text = en.message
                        }
                    }
                }
                .onWithMsgFailure {
                }
        })
    }

}