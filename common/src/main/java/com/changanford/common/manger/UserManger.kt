package com.changanford.common.manger

import androidx.lifecycle.LiveData
import com.changanford.common.MyApp
import com.changanford.common.bean.LoginBean
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
        USER_LOGIN_SUCCESS, USER_LOGIN_OUT, USE_LOGIN_FAIL
    }

    fun isLogin(): Boolean = MConstant.token.isNotEmpty()

    fun saveUserInfo(loginBean: LoginBean) {
        //把用户基本信息写入数据库
        loginBean?.let {
            var sysUserInfoBean =
                SysUserInfoBean(loginBean.userId, loginBean.phone, loginBean.token)
            UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao()
                .insert(sysUserInfoBean)
        }
    }

    fun deleteUserInfo() {
        UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().deleteAll()
    }


    fun getSysUserInfo(): LiveData<SysUserInfoBean> {
        return UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().getUser()
    }

}

