package com.changanford.shop.control

import android.annotation.SuppressLint
import android.content.Context
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.ui.order.PayConfirmActivity
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson

/**
 * @Author : wenke
 * @Time : 2021/10/15 0015
 * @Description : OrderControl
 */
class OrderControl(val context: Context,val viewModel: OrderViewModel?) {
    /**
     * 去支付
     * */
    fun toPay(item: OrderItemBean){
        PayConfirmActivity.start(context, Gson().toJson(OrderInfoBean(item.orderNo,item.fbCost)))
    }
    /**
     * 再次购买->创建订单页面
     * */
    fun onceAgainToBuy(item: OrderItemBean){
        val skuCodeTxt= item.specifications.split(",").filter { ""!=it }
        val goodsBean= GoodsDetailBean(spuId = item.mallMallSpuId,spuName =item.spuName, buyNum = item.buyNum.toInt(),fbPrice = item.fbOfUnitPrice,
            freightPrice = item.freightPrice,preferentialFb = item.preferentialFb,acountFb = (item.totalIntegral?:"0").toInt(),skuCode = item.skuCode,
            skuCodeTxts = skuCodeTxt, addressInfo = item.addressInfo,addressId = item.addressId,skuId = item.mallMallSkuId,skuImg = item.skuImg,)
        OrderConfirmActivity.start(Gson().toJson(goodsBean))
    }
    /**
     * 确认收货
     * */
    fun confirmGoods(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.str_confirmReceiptGoods),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() {
                    dismiss()
                }
                override fun onRightClick() {
                    viewModel?.confirmReceipt(item.orderNo,object : OnPerformListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            ToastUtils.showShortToast(R.string.str_goodsSuccessfully,context)
                            item.orderStatus="FINISH"
                            listener?.onFinish(0)
                            dismiss()
                        }
                    })
                }
            })
        }
    }

    /**
     * 取消订单
     * */
    fun cancelOrder(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.prompt_cancelOrder),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() { dismiss() }
                override fun onRightClick() {
                    viewModel?.orderCancel(item.orderNo,object:OnPerformListener{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            ToastUtils.showShortToast(R.string.str_orderCancelledSuccessfully,context)
                            item.orderStatus="CLOSED"
                            listener?.onFinish(0)
                            dismiss()
                        }
                    })
                }
            })
        }
    }
}