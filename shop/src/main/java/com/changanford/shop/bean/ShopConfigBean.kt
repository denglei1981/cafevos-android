package com.changanford.shop.bean

/**
 * @author: niubobo
 * @date: 2024/5/15
 * @description：
 */

//seckill_area_off:秒杀区域开关
//rank_off:榜单区域开关
//pro_detail_roll:商品详情评价滚动区配置
//  content:滚动区内容
//  protocol_code:商品详情评价滚动区协议code
//order_list_roll:订单列表滚动区
//  content:滚动区内容
//  protocol_code:订单列表滚动区协议code
//evaluate_conf: 评价配置
//  evaluate_tip:评价提示内容
//  commit_pic_num:发放福币要求图片数量
//  commit_word_num:发放福币要求字数
//  send_fb:发放福币数
data class ShopConfigBean(
    val evaluate_conf: EvaluateConf?,//评价配置
    val order_list_roll: OrderListRoll?,//订单列表滚动区
    val pro_detail_roll: ProDetailRoll?,//商品详情评价滚动区配置
    val rank_off: Boolean?,//榜单区域开关
    val seckill_area_off: Boolean?//秒杀区域开关
)

data class EvaluateConf(
    val commit_pic_num: Int?,
    val commit_word_num: Int?,
    val evaluate_tip: String?,
    val send_fb: Int?
)

data class OrderListRoll(
    val content: String?,
    val protocol_code: String?
)

data class ProDetailRoll(
    val content: String?,
    val protocol_code: String?
)