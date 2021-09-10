package com.changanford.my.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 *  文件名：Menu
 *  创建者: zcy
 *  创建日期：2021/9/10 15:40
 *  描述: TODO
 *  修改描述：TODO
 */
const val MINE_ITEM_TITLE = 1 //标题
const val MINE_ITEM_MENU = 2 //常用菜单

var getMenus: ArrayList<MineMenuMultiEntity> = arrayListOf(
    MineMenuMultiEntity(MINE_ITEM_TITLE, "我的日常任务", spanSize = 4),
    MineMenuMultiEntity(MINE_ITEM_MENU, "任务中心"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的钉钉"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的圈子"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的任务"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的日常"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的日常任务"),
    MineMenuMultiEntity(MINE_ITEM_MENU, "我的日常任务"),
)

data class MineMenuMultiEntity(
    override val itemType: Int,
    val menuName: String,
    val icon: String,
    val spanSize: Int
) : MultiItemEntity {
    constructor(itemType: Int, menuName: String, spanSize: Int = 1) : this(
        itemType,
        menuName,
        "",
        spanSize
    )
}


