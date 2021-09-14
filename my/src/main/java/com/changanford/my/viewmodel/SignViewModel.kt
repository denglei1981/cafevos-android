package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changanford.common.MyApp
import com.changanford.common.bean.LoginBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.UserManger
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.USER_LOGIN_STATUS
import com.changanford.common.util.room.UserDatabase

/**
 *  文件名：SignViewModel
 *  创建者: zcy
 *  创建日期：2021/9/9 16:50
 *  描述: TODO
 *  修改描述：TODO
 */
class SignViewModel : ViewModel() {

    var smsSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }

    suspend fun getSmsCode(mobile: String) {
        var sms = fetchRequest {
            var body = HashMap<String, Any>()
            body["phone"] = mobile
            var rKey = getRandomKey()
            apiService.sendFordSmsCode(body.header(rKey), body.body(rKey))
        }
        smsSuccess.postValue(sms.code == 0)
    }

    suspend fun smsLogin(mobile: String, sms: String, pushId: String) {
        var login = fetchRequest {
            var body = HashMap<String, String>()
            body["phone"] = mobile
            body["smsCode"] = sms
            body["pushId"] = pushId
            var rkey = getRandomKey()
            apiService.smsCodeSign(body.header(rkey), body.body(rkey))
        }
        if (login.code == 0) {
            login.data?.let {
                loginSuccess(it)
            }
        }
    }

    suspend fun otherLogin(type: String, code: String, pushId: String) {
        var other = fetchRequest {
            var body = HashMap<String, String>()
            body["type"] = type
            body["code"] = code
            body["pushId"] = pushId
            var rkey = getRandomKey()
            apiService.otherOauthSign(body.header(rkey), body.body(rkey))
        }
        if (other.code == 0) {
            other.data?.let {
                loginSuccess(it)
            }
        }
    }

    suspend fun getUserInfo() {
        if (UserManger.isLogin()) {
            var user = fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryUserInfo(body.header(rkey), body.body(rkey))
            }
            if (user.code == 0) {
                saveUserInfo(user.data)
            } else {
                saveUserInfo(null)
            }
        } else {
            saveUserInfo(null)
        }
    }


    private fun saveUserInfo(userInfoBean: UserInfoBean?) {
        UserManger.updateUserInfo(userInfoBean)
    }

    /**
     * 登录成功
     */
    private suspend fun loginSuccess(loginBean: LoginBean) {
        loginBean?.let {
            UserManger.saveUserInfo(loginBean)
            MConstant.token = it.token
            SPUtils.putToken(it.token)
            getUserInfo()
        }
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .postValue(UserManger.UserLoginStatus.USER_LOGIN_SUCCESS)
    }

    /**
     * 退出登录
     */
    fun loginOut() {
        UserManger.deleteUserInfo()
        MConstant.token = ""
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .postValue(UserManger.UserLoginStatus.USER_LOGIN_OUT)
    }

}