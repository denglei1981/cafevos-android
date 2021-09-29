package com.changanford.my.viewmodel

import androidx.lifecycle.ViewModel
import com.changanford.common.MyApp
import com.changanford.common.util.room.UserDatabase

/**
 *  文件名：CarAuthViewModel
 *  创建者: zcy
 *  创建日期：2021/9/28 9:15
 *  描述: TODO
 *  修改描述：TODO
 */
class CarAuthViewModel : ViewModel() {

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }

}