package com.changanford.common.manger

import com.alibaba.fastjson.JSON
import com.changanford.common.MyApp
import com.changanford.common.bean.LoginBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.util.MConstant

import com.changanford.common.util.room.SysUserInfoBean
import com.changanford.common.util.room.UserDatabase

/**
 *  文件名：UserManger
 *  创建者: zcy
 *  创建日期：2021/9/10 10:39
 *  描述: 用户基本信息管理
 *  修改描述：TODO
 */
object UserManger {
    /**
     * 用户登录状态
     */
    public enum class UserLoginStatus {
        USER_LOGIN_SUCCESS, USER_LOGIN_OUT, USE_UNBIND_MOBILE, USE_CANCEL_BIND_MOBILE
    }

    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun saveUserInfo(loginBean: LoginBean) {
        //把用户基本信息写入数据库
        loginBean?.let {
            var sysUserInfoBean =
                SysUserInfoBean(
                    it.userId,
                    if (it.phone?.isNullOrEmpty()) "" else it.phone,
                    it.token
                )
            UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
                .insert(sysUserInfoBean)
        }
    }

    fun updateUserInfo(userInfoBean: UserInfoBean?) {
        userInfoBean?.let {
            var userDatabase = UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
            userDatabase.updateMobile(
                it.userId, when {
                    it.phone.isNotEmpty() -> {
                        it.phone
                    }
                    else -> {
                        it.mobile
                    }
                }
            )
            userDatabase.updateIntegral(it.userId, "${it.integralDecimal}")
            userDatabase.updateUserJson(it.userId, JSON.toJSONString(it))
        }
    }


    fun deleteUserInfo() {
        UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().deleteAll()
    }


    /**
     * 没有liveData监听
     */
    fun getSysUserInfo(): SysUserInfoBean {
        return UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
            .getNoLiveDataUser()
    }

}

