package com.changanford.my.bean

/**
 *  文件名：UniAuthImageBean
 *  创建者: zcy
 *  创建日期：2020/5/20 21:04
 *  描述: TODO
 *  修改描述：TODO
 */
//图片位置（1名称资料证明 2个人展示照片）
data class UniAuthImageBean(val imgPosition: Int, val imgUrls: ArrayList<String>)
