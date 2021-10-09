package com.changanford.shop.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.fetchRequest
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
     * 是否登录，token不null:true登录，
     */
    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun <T> launch(showLoading: Boolean = false, block: Block<CommonResponse<T>>) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                block.invoke()

            }
        }
    }
}