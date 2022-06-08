package com.changanford.my.request

import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

class PersonCenterViewModel: BaseViewModel() {

    /**
     * 获取用户信息
     */
    fun queryOtherInfo(userId: String, result: (CommonResponse<UserInfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                val body = HashMap<String, Any>()
                body["userId"] = userId
                val rkey = getRandomKey()
                apiService.queryOtherInfo(body.header(rkey), body.body(rkey))
            })
        }
    }
}