package com.changanford.common.bean

import com.changanford.common.util.MConstant

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionBean
 */
data class QuestionInfoBean(val id:String?=null,
                            val pic:String?=null,
                            val imgs:List<String>?=null,
                            val adoptNum: Int = 0,
                            val adoptRankNum: String? = null,
                            val anserNum: Int = 0,
                            val anserRankNum: String? = null,
                            val introduction: String? = null,
                            val questionNum: Int = 0,
                            val user: QuestionUserInfo = QuestionUserInfo(),
                            var tabs:List<String>?=null,
){
    /**
     * 是否是自己
    * */
    fun isOneself():Boolean{
       return MConstant.userId==user.userId
    }
    /**
     * 提问人身份 0 普通  1 技师  2车主
    * */
    fun getIdentity():Int{
        return when(user.identity){
            "NORMAL"->0
            "TECHNICIAN"->1
            "CAR_OWNER"->2
            else ->0
        }
    }
}
data class QuestionUserInfo(
    val avater: String? = null,
    val conQaTechnicianId: String? = null,
    val conQaUjId: String? = null,
    val identity: String = "",
    val modelCode: Any? = null,
    val modelName: Any? = null,
    val nickName: String? = null,
    val userId: String? = null
)