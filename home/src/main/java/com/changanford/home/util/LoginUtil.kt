package com.changanford.home.util
import android.text.TextUtils
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant

object  LoginUtil {
    fun isLogin():Boolean{
        return if(TextUtils.isEmpty(MConstant.token)){
            startARouter(ARouterMyPath.SignUI)
            false
        }else{
            true
        }
    }

}