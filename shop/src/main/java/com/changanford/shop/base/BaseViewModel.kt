package com.changanford.shop.base

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.net.*
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.api.ShopNetWorkApi
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/30 0030
 * @Description : BaseViewModel
 */
typealias Block<T> = suspend () -> T

open class BaseViewModel : ViewModel() {
    protected var pageSize=20
    protected val body = MyApp.mContext.createHashMap()
    val shopApiService: ShopNetWorkApi by lazy {
        ApiClient.retrofit.create(ShopNetWorkApi::class.java)
    }
    val responseData: MutableLiveData<ResponseBean> = MutableLiveData()
    /**
     * 是否登录
     */
    fun isLogin():Boolean{
        val isNotEmpty= MConstant.token.isNotEmpty()
        if(!isNotEmpty)JumpUtils.instans?.jump(100)
        return isNotEmpty
    }
    fun <T> launch(showLoading: Boolean = false, block: Block<CommonResponse<T>>) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                block.invoke()
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