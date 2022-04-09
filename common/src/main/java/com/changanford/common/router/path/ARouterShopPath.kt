package com.changanford.common.router.path

/**
 * @Author hpb
 * @Date 2020/4/30 12:04
 * @Des shop相关路由
 */
object ARouterShopPath {

    ////////////////////  Activity路由  /////////////////
    const val ShopGoodsActivity = "/shop/GoodsDetailsActivity"//商品详情
    const val GoodsKillAreaActivity = "/shop/GoodsKillAreaActivity"//限时秒杀
    const val GoodsEvaluateActivity = "/shop/GoodsEvaluateActivity"//商品评价
    const val AllOrderActivity = "/shop/AllOrderActivity"//所有订单
    const val OrderConfirmActivity = "/shop/OrderConfirmActivity"//确认订单
    const val OrderGoodsActivity = "/shop/OrdersGoodsActivity"//商品订单列表
    const val OrderDetailActivity = "/shop/OrderDetailsActivity"//订单详情
    const val OrderEvaluationActivity = "/shop/OrderEvaluationActivity"//订单评价
    const val PostEvaluationActivity = "/shop/PostEvaluationActivity"//发布评价、追评
    const val PayConfirmActivity = "/shop/PayConfirmActivity"//支付确认
    const val RecommendActivity = "/shop/RecommendActivity"//商品推荐
    const val IntegralDetailsActivity = "/shop/IntegralDetailsActivity"//积分明细

    const val ShoppingCartActivity = "/shop/ShoppingCartActivity" // 购物车

    const val UseCouponsActivity = "/shop/UseCouponsActivity"  // 使用优惠券
    const val ChooseCouponsActivity = "/shop/ChooseCouponsActivity"  // 选择优惠券
    const val CouponActivity = "/shop/CouponActivity"  // 优惠券跳转

    const val AfterSaleActivity = "/shop/AfterSaleActivity" // 商品售后

    const val InvoiceActivity="/shop/InvoiceActivity" // 申请发票

    const val RefundNotShippedActivity="/shop/RefundNotShippedActivity" // 申请退款

    const val InvoiceLookActivity ="/shop/InvoiceLookActivity" // 查看发票详情

    const val RefundProgressActivity ="/shop/RefundProgressActivity" // 退款进度
    ////////////////////  Fragment路由  /////////////////

    ////////////////////  其他类路由  /////////////////
}