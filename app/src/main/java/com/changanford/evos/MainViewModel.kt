package com.changanford.evos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.User
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.UserManger
import com.changanford.common.manger.UserManger.saveUserInfo
import com.changanford.common.net.*
import com.changanford.common.utilext.logE
import com.changanford.common.wutil.WConstant
import com.changanford.evos.utils.pop.SingleJob
import com.changanford.my.utils.downLoginBg
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

    fun requestDownLogin() {
        viewModelScope.launch {
            fetchRequest {
                val body = java.util.HashMap<String, Any>()
                body["configKey"] = "login_background"
                body["obj"] = true
                val rkey = getRandomKey()
                apiService.loginBg(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.video?.apply {
                    downLoginBg(this)
                }
            }.onFailure {
            }
        }
    }

    /**
     * 获取问答 tagInfo
     * */
    fun getQuestionTagInfo() {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                body["dictType"] = "qa_question_type"
                val rKey = getRandomKey()
                apiService.getQuestionTagInfo(body.header(rKey), body.body(rKey)).onSuccess {
                    WConstant.questionTagList = it
                }
            }
        }
    }

    var userInfo: MutableLiveData<UserInfoBean> = MutableLiveData()

    fun getUserInfo() {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, String>()
                val rkey = getRandomKey()
                apiService.queryUserInfo(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    userInfo.postValue(it)
                }
            }
        }
    }
}
