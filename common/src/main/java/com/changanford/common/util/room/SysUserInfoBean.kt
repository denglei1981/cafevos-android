package com.changanford.common.util.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 *  文件名：UniUserInfoBean
 *  创建者: zcy
 *  创建日期：2020/5/12 16:15
 *  描述: 用户信息
 *  修改描述：TODO
 */
@Entity(tableName = "table_sys_uni_user", primaryKeys = ["uni_user_id"])
class SysUserInfoBean constructor() {

    constructor(uid: String, mobile: String?, token: String) : this() {
        this.uid = uid
        this.mobile = mobile
        this.token = token
    }

    constructor(uid: String, mobile: String?) : this() {
        this.uid = uid
        this.mobile = mobile
    }

    constructor(mobile: String) : this() {
        this.mobile = mobile
    }

    @NonNull
    @ColumnInfo(name = "uni_user_id")
    lateinit var uid: String

    @ColumnInfo(name = "user_mobile")
    var mobile: String? = ""

    @ColumnInfo(name = "user_integral")
    var integral: Double = 0.0

    @ColumnInfo(name = "user_token")
    var token: String? = ""

    @ColumnInfo(name = "user_json")
    var userJson: String? = ""

    @ColumnInfo(name = "mine_bind_mobile_jump_data")
    var bindMobileJumpType: Int = 0

    fun isLogin(): Boolean? = token?.isNotEmpty()


    override fun toString(): String {
        return "uid=${uid};mobile=${mobile};integral=${integral};token=${token};userJson=${userJson};isLogin=${isLogin()}"
    }
}