package com.changanford.common.bean

/**
 *  文件名：HobbyBean
 *  创建者: zcy
 *  创建日期：2020/5/15 20:48
 *  描述: TODO
 *  修改描述：TODO
 */

class HobbyBean : ArrayList<HobbyBeanItem>()

data class HobbyBeanItem(
    val hobbyIcon: String,
    val hobbyId: Int,
    val hobbyTypeName: String,
    val list: List<HobbyItem>
)

data class HobbyItem(
    val createTime: Long,
    val hobbyIcon: String,
    val hobbyId: Int,
    val hobbyName: String,
    val parentHobbyId: Int,
    val status: Int
)

/**
 * 兴趣爱好回调
 */
data class RetrunLike(var ids: String, var names: String)