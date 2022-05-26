package com.changanford.my.bean

import com.changanford.common.bean.MenuBeanItem

/**
 *  文件名：MangerCircle
 *  创建者: zcy
 *  创建日期：2021/9/27 13:47
 *  描述: TODO
 *  修改描述：TODO
 */
data class MangerCircleCheck(var index: Int, var isShow: Boolean)

data class BindingCar(var confirm: Int, var vin: String) {


}

data class MineMenuData(var title: String, var list: MutableList<MenuBeanItem>)