package com.changanford.my.viewmodel

import androidx.lifecycle.ViewModel
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.AuthCarStatus

/**
 *  文件名：CarViewModel
 *  创建者: zcy
 *  创建日期：2021/9/15 9:22
 *  描述: TODO
 *  修改描述：TODO
 */
class CarViewModel : ViewModel() {


    suspend fun getAuthStatus(status: AuthCarStatus) {
        var car = fetchRequest {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            apiService.getAuthStatus(body.header(rkey), body.body(rkey))
        }
    }

    suspend fun queryAuthCarAndIncallList(status: AuthCarStatus) {
        var car = fetchRequest {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            apiService.queryAuthCarAndIncallList(body.header(rkey), body.body(rkey))
        }
    }
}