package com.changanford.common.bean

/**
 * @Author : wenke
 * @Time : 2021/9/28
 * @Description : 商城
 */
data class GoodsBean(val id:Int=0,var title:String){
    var states:Int=0
}

data class GoodsItemBean(val goodsId:String)