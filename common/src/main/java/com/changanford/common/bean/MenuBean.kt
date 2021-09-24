package com.changanford.common.bean

class MenuBean : ArrayList<MenuBeanItem>()

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