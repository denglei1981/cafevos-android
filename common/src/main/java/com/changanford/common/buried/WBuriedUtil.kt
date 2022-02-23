package com.changanford.common.buried

import com.alibaba.fastjson.JSON
import com.changanford.common.util.MConstant

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.utils.BuriedUtil
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/28 10:05
 * @Description: 　埋点工具
 * *********************************************************************************
 */

object WBuriedUtil {
    /**
     * 数据埋点
     */
    private fun buried(actName: String="", actionType: String="", targetId: String="", targetName: String="", pageStayTime: String="",extend:String="") {
        val buriedBean = BuriedBean()
        val data = buriedBean.getData(
            record = BuriedBean.BuriedRecord(
                actName, actionType, targetName, targetId, MConstant.userId, pageStayTime, extend = extend
            )
        )
        MBuriedWorkerManager.instant?.buried(JSON.toJSONString(data))
    }
    //=======================================商城 START
    /**
     *用户点击Banner图片时触发
     * */
    fun clickShopBanner(bannerName:String?){
        buried("app商城_推荐banner","app_mall_recommend_banner",extend = "{\"banner_name\": \"$bannerName\"}")
    }
    /**
     *用户点击限时秒杀商品图文区域触发
    * */
    fun clickShopKill(goodsName:String,price:String){
        buried("app商城_限时秒杀","app_mall_seckill",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\"}")
    }
    /**
     *用户点击立即前往按钮时触发
     * */
    fun clickShopIntegral(){
        buried("app商城_赚取积分_立即前往","app_mall_get_integral_link")
    }
    /**
     *用户点击商品分类菜单栏时触发
     * */
    fun clickShopType(goodsCategory:String){
        buried("app商城_商品分类_菜单栏","app_mall_goods_category_menu", extend = "{\"goods_category\": \"$goodsCategory\"}")
    }
    /**
     *用户点击商品图文区域banner时触发
     * */
    fun clickShopItem(goodsName:String,price:String){
        buried("app商城_商品详情_banner","app_mall_goods_details_banner",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\"}")
    }


    //商城==============订单-START
    /**
     *用户点击确认收货按钮时触发
     * */
    fun clickShopOrderTakeDelivery(orderNo:String,goodsName:String?,price:String){
        buried("app商城_商品订单_确认收货","app_mall_order_Confirm ",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\",\"order_id\": \"$orderNo\"}")
    }
    /**
     *用户点击评价按钮时触发
     * */
    fun clickShopOrderComment(orderNo:String,goodsName:String?,price:String){
        buried("app商城_商品订单_评价","app_mall_order_comment ",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\",\"order_id\": \"$orderNo\"}")
    }
    /**
     *用户点击取消订单按钮时触发
     * */
    fun clickShopOrderCancel(orderNo:String,goodsName:String?,price:String){
        buried("app商城_商品订单_取消订单","app_mall_order_cancel ",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\",\"order_id\": \"$orderNo\"}")
    }
    /**
     *用户点击立即支付按钮时触发
     * */
    fun clickShopOrderPay(orderNo:String,goodsName:String?,price:String){
        buried("app商城_商品订单_立即支付","app_mall_order_pay ",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\",\"order_id\": \"$orderNo\"}")
    }
    /**
     *用户点击再次购买按钮时触发
     * */
    fun clickShopOrderBuy(orderNo:String,goodsName:String?,price:String){
        buried("app商城_商品订单_再次购买","app_mall_order_buyagain ",extend = "{\"goods_name\": \"$goodsName\",\"price\": \"$price\",\"order_id\": \"$orderNo\"}")
    }
    //商城==============订单-END
    //=======================================商城END

    //=======================================社区
    /**
     * 用户点击顶部圈子图标时触发
     * [circleName]圈子名称
    * */
    fun clickCircleTop(circleName: String?){
        buried("app社区_圈子","app_community_Moments", extend = "{\"Moments_type\": \"$circleName\"}")
    }
    /**
     * 用户点击申请加入时触发
     * */
    fun clickCircleJoin(){
        buried("app社区_我的圈子_申请加入","app_community_mymoments_join", extend = "{\"Moments_list\": \"Moments_list\"}")
    }
    /**
     * 用户点击热门车型圈时触发
     * [circleName]圈子名称
     * */
    fun clickCircleHot(circleName: String?){
        buried("app社区_热门车型圈","app_community_hot_Moments", extend = "{\"Moments_type\": \"$circleName\"}")
    }
    /**
     * 用户点击立即订购时触发
     * [circleName]圈子名称
     * */
    fun clickCircleYouLike(circleName: String?){
        buried("app社区_猜你喜欢","app_community_guess", extend = "{\"Moments_type\": \"$circleName\"}")
    }

    //===========================================爱车
    /**
     * 用户点击立即订购时触发
     * [modelName]车型名称
     * */
    fun clickCarOrder(modelName: String?){
        buried("app爱车首页_立即订购_点击","app_car_homepage_ordercar", extend = "{\"Model_name\": \"$modelName\"}")
    }
    /**
     * 用户点击赏车之旅时触发
     * [modelName]车型名称
     * */
    fun clickCarEnjoy(modelName: String?){
        buried("app爱车首页_赏车之旅_点击","app_car_homepage_browsecar", extend = "{\"Model_name\": \"$modelName\"}")
    }
    /**
     * 用户点击购车服务按钮时触发
     * [saleServiceName]购车服务按钮名称
     * */
    fun clickCarBuyService(saleServiceName: String?){
        buried("app爱车首页_购车服务_点击","app_car_homepage_sale_service", extend = "{\"sale_service_name\": \"$saleServiceName\"}")
    }
    /**
     * 用户点击体验售后服务按钮时触发
     * [afterSaleServiceName]售后服务按钮名称
     * */
    fun clickCarAfterSalesService(afterSaleServiceName: String?){
        buried("app爱车首页_售后服务_点击","app_car_homepage_aftersale_service", extend = "{\"aftersale_service_name\": \"$afterSaleServiceName\"}")
    }
    /**
     * 用户点击经销商banner时触发
     * [dealerName]经销商名称
     * */
    fun clickCarDealer(dealerName: String?){
        buried("app爱车首页_经销商_点击","app_car_homepage_dealer", extend = "{\"dealer_name\": \"$dealerName\"}")
    }
    /**
     * 用户点击导航按钮时触发
     * */
    fun clickCarAfterSalesNavigate(){
        buried("app爱车首页_导航_点击","app_car_homepage_navigate")
    }
    /**
     * 用户点击去认证车主按钮时触发
     * */
    fun clickCarCertification(){
        buried("app爱车首页_去认证车主_点击","app_car_homepage_Certified_owner")
    }
}