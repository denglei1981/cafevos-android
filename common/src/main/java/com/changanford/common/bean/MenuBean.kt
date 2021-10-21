package com.changanford.common.bean
import com.chad.library.adapter.base.entity.MultiItemEntity

data class MenuBeanItem(
    val createBy: Int,
    val createTime: String,
    val icon: String,
    val jumpDataType: Int,
    val jumpDataValue: String,
    val menuId: Int,
    val menuName: String,
    val menuType: String,
    val parentId: Int,
    val remark: String,
    val sort: Int,
    val status: Int,
    val topIconType: Int,
    val topIconUrl: String,
    val updateTime: String
)


/**
 *  文件名：Menu
 *  创建者: zcy
 *  创建日期：2021/9/10 15:40
 *  描述: TODO
 *  修改描述：TODO
 */
const val MINE_ITEM_TITLE = 1 //标题
const val MINE_ITEM_MENU = 2 //常用菜单


data class MineMenuMultiEntity(
    override val itemType: Int,
    val menuName: String,
    val icon: String,
    val spanSize: Int,
    val routeUrl: String
) : MultiItemEntity {
    constructor(itemType: Int, menuName: String, routeUrl: String = "", spanSize: Int = 1) : this(
        itemType,
        menuName,
        "",
        spanSize = spanSize,
        routeUrl = routeUrl
    )
}