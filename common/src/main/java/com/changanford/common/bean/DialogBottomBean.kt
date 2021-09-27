package com.changanford.common.bean

/**
 * Created by Eaves on 2019/9/23
 * 文件名：DialogBottomBean
 * 描述 ： 底部弹框item
 * 更新 ：
 */
//    var typeViewId //显示布局样式 1：竖排 1 2：横排 默认3
//    var resId //资源文件id icon
//    var title //显示标题
data class DialogBottomBean(
    var id: Int,
    var title: String,
    var resId: Int = 0,
    var typeViewId: Int = 1
)