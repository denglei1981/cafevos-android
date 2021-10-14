package com.changanford.common.bean

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.bean.User
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 14:54
 * @Description: 　
 * *********************************************************************************
 */
data class User(
    var id: Int = 0,
    val name: String = "",
    val sotr: String? = null
)


data class LoginBean(
    val openid: String,
    val phone: String = "",
    val roleSession: String,
    val token: String,
    val userId: String,
    val isNewUser: Int
)