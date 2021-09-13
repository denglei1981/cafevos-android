package com.changanford.common.basic

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.changanford.common.util.MConstant

/**
 * @Author hpb
 * @Date 2020/4/2 23:52
 * @Des 父类ViewModel
 */
open class BaseViewModel(val context: Context) : AndroidViewModel(BaseApplication.INSTANT){


    /**
     * 是否登录，token不null:true登录，
     */
    fun isLogin(): Boolean = MConstant.token.isNotEmpty()
}