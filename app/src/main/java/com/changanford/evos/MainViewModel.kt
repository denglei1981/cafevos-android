package com.changanford.evos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.User
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.utilext.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.evos.MainViewModel
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 10:53
 * @Description: 网络请求示例
 * *********************************************************************************
 */
class MainViewModel : ViewModel() {
    var user = MutableLiveData<List<User>>()

    /**
     * 使用LiveData
     */
    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            var request = fetchRequest {
                var map = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.getUserData(map.header(rkey), map.body(rkey))
            }
            if (request.code == 0) {//处理成功和失败
                val content = request.data
                content?.apply {
                    user.postValue(this)
                }
            } else {
                request.msg ?: "tag".logE()
            }
        }
    }





}
