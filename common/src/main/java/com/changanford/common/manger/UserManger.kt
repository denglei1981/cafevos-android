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
        USER_LOGIN_SUCCESS, USER_LOGIN_OUT, USE_UNBIND_MOBILE, USE_CANCEL_BIND_MOBILE, USE_BIND_MOBILE_SUCCESS
    }

    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun saveUserInfo(loginBean: LoginBean) {
        //把用户基本信息写入数据库
        loginBean?.let {
            var sysUserInfoBean =
                SysUserInfoBean(
                    it.userId,
                    when {
                        it.phone.isNullOrEmpty() -> ""
                        else -> {
                            it.phone
                        }
                    },
                    it.token
                )
            it.jumpData?.let {
                sysUserInfoBean.bindMobileJumpType = it.jumpDataType
            }
            UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
                .insert(sysUserInfoBean)
        }
    }

    fun updateUserInfo(userInfoBean: UserInfoBean?) {
        userInfoBean?.let {
            var database = UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
            var sysUserInfoBean = SysUserInfoBean(it.userId, "")
            try {
                sysUserInfoBean = database.getNoLiveDataUser()
                sysUserInfoBean.mobile = when {
                    it.phone.isNotEmpty() -> {
                        it.phone
                    }
                    else -> {
                        it.mobile
                    }
                }
                it.ext?.totalIntegral?.let {
                    sysUserInfoBean.integral = it.toDouble()
                }
                sysUserInfoBean.userJson = JSON.toJSONString(it)
            } catch (e: Exception) {
                sysUserInfoBean.mobile = when {
                    it?.phone?.isNotEmpty()==true -> {
                        it.phone
                    }
                    else -> {
                        it.mobile
                    }
                }
                it.ext?.totalIntegral?.let {
                    sysUserInfoBean.integral = it.toDouble()
                }
                sysUserInfoBean.userJson = JSON.toJSONString(it)
            }
            database.insert(sysUserInfoBean)
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

