package com.changanford.home.util
import android.text.TextUtils
import com.changanford.common.MyApp
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.toast

object  LoginUtil {

    fun isLongAndBindPhone(isSkipBindMobile:Boolean=true):Boolean{
        return isLoginss()&&isBindPhone(isSkipBindMobile)
    }

    fun isLoginss():Boolean{
        return if(TextUtils.isEmpty(MConstant.token)){
            startARouter(ARouterMyPath.SignUI)
            false
        }else{
            true
        }
    }
    fun isBindPhone(isSkipBindMobile:Boolean=true):Boolean{
        val user = UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().getNoLiveDataUser()
        user?.let {
            if (it.bindMobileJumpType == LiveDataBusKey.MINE_SIGN_OTHER_CODE) {
                if (isSkipBindMobile) {
                    "请先绑定手机号".toast()
                    JumpUtils.instans?.jump(18)
                    return false
                }
            }
                           return true


        }
        return false
    }

}