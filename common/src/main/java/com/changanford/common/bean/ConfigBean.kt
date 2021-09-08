package com.changanford.common.bean

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/16 15:30
 * @Description: 　app打开时请求后台配置信息，包括图片域名等
 * *********************************************************************************
 */
class ConfigBean(
    /**
     * 图片域名
     */
    val imgCdn: String,
    val pointmallServiceTerms: String,  //长安引力服务条款
    val floatBt :FloatBt,
    /**
     * Uni中间页数据
     */
    val topMainImg: String?,//顶部图片
    val goodservice: ArrayList<GoodServiceBean>,//优质服务

    val topads: ArrayList<GoodServiceBean>, //订车顶部入口
    val bottomads: ArrayList<GoodServiceBean>, //订车底部入口
    val unishopCartUrl:String     //购物车地址
) {
    class FloatBt(
        val img:String,
        val show:Boolean,
        val jumpData: AdBean
    )
    class GoodServiceBean(
        val id: String,
        val name: String,
        val subName: String,
        val img: String,
        val jumpDataType: Int,
        val jumpDataValue: String
    )
}