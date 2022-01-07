package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : NewCircleBean
 */
data class NewCircleBean(val id:String?="0",val circleId:String?="0",val cityName:String?=null,val pic:String?=null)

data class NewCirceTagBean(var id:String?=null,var isCheck:Boolean?=false,var tagName:String?=null)
