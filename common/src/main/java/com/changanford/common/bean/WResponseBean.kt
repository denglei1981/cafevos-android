package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2021/9/30 0030
 * @Description : ResponseBean
 */
data class WResponseBean(var isSuccess:Boolean=false, var code:Int?=0, var msg:String?="", var tag:Any?=null)
