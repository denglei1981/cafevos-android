package com.changanford.my.bean

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 *  文件名：RootTaskBean
 *  创建者: zcy
 *  创建日期：2021/9/13 15:41
 *  描述: TODO
 *  修改描述：TODO
 */
class RootTaskBean(override val childNode: MutableList<BaseNode>, val title: String) :
    BaseExpandNode() {

}

class ItemTaskBean(override val childNode: MutableList<BaseNode>? = null) : BaseNode() {

}

data class TaskBean(val title: String)

val getTaskBean: ArrayList<TaskBean> =
    arrayListOf(
        TaskBean("消耗积分"),
        TaskBean("消耗积分"),
        TaskBean("消耗积分"),
        TaskBean("消耗积分"),
        TaskBean("消耗积分")
    )