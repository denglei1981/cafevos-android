package com.changanford.common.bean
import com.chad.library.adapter.base.entity.MultiItemEntity

data class MenuBeanItem(
    var createBy: Int=0,
    var createTime: String="",
    var icon: String="",
    var jumpDataType: Int=0,
    var jumpDataValue: String="",
    var menuId: Int=0,
    var menuName: String="",
    var menuType: String="",
    var parentId: Int=0,
    var remark: String="",
    var sort: Int=0,
    var status: Int=0,
    var topIconType: Int=0,
    var topIconUrl: String="",
    var updateTime: String="",
    var drawInt:Int=0
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