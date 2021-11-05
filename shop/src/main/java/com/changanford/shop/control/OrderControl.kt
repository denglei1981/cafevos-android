package com.changanford.shop.control

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.databinding.InItemOrderGoodsBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
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
     * 绑定订单商品基础信息
     * */
    fun bindingGoodsInfo(dataBinding:InItemOrderGoodsBinding,item: OrderItemBean){
        dataBinding.apply {
//            val preferentialFbOfUnitPrice=item.preferentialFbOfUnitPrice
//            if(null==preferentialFbOfUnitPrice)item.preferentialFbOfUnitPrice=item.fbOfUnitPrice
            //砍价订单
            if("2"==item.busSourse&&!TextUtils.isEmpty(item.hagglePrice)){
                item.fbOfUnitPrice=item.hagglePrice!!
            }
            imgGoodsCover.load(item.skuImg)
            tvOrderType.apply {
                visibility = when(item.busSourse) {
                    "1" -> {//秒杀
                        setText(R.string.str_seckill)
                        View.VISIBLE
                    }
                    "2" -> {//砍价
                        setText(R.string.str_bargaining)
                        View.VISIBLE
                    }
                    else -> View.GONE
                }
            }
            recyclerView.apply {
                if(!TextUtils.isEmpty(item.specifications)){
                    visibility= View.VISIBLE
                    layoutManager= FlowLayoutManager(context,false,true)
                    adapter= OrderGoodsAttributeAdapter().apply {
                        val specifications=item.specifications.split(",").filter { ""!=it }
                        setList(specifications)
                    }
                }else{
                    visibility= View.GONE
                }
            }
            model=item
        }
    }
    /**
     * 去支付
     * */
    fun toPay(item: OrderItemBean){
        PayConfirmActivity.start(Gson().toJson(OrderInfoBean(item.orderNo,item.fbCost)))
    }
    /**
     * 再次购买->商品详情
     * */
    fun onceAgainToBuy(item: OrderItemBean){
        item.apply {
            if("2"==busSourse){
                //砍价
                JumpUtils.instans?.jump(1,String.format(MConstant.H5_SHOP_BARGAINING,mallMallHaggleSpuId,mallMallHaggleActivityId))
            }else  GoodsDetailsActivity.start(mallMallSpuId)
        }

//        val skuCodeTxt= item.specifications.split(",").filter { ""!=it }
//        val busSourse=item.busSourse
//        val spuPageType=if("2"==busSourse)"2" else if("1"==busSourse)"SECKILL" else if("1"==item.discount)"MEMBER_DISCOUNT" else "NOMROL"
//        val goodsBean= GoodsDetailBean(spuId = item.mallMallSpuId,spuName =item.spuName, buyNum = item.buyNum.toInt(),fbPrice = item.fbOfUnitPrice,
//            freightPrice = item.freightPrice?:"0.00",preferentialFb = item.preferentialFb,acountFb = (item.totalIntegral?:"0").toInt(),skuCode = item.skuCode,
//            skuCodeTxts = skuCodeTxt, addressInfo = item.addressInfo,addressId = item.addressId,skuId = item.mallMallSkuId,skuImg = item.skuImg,source = "3",
//            spuPageType =spuPageType,orginPrice = item.fbOfUnitPrice)
//        OrderConfirmActivity.start(Gson().toJson(goodsBean))
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
                            item.orderStatusName=""
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
                            item.orderStatusName=""
                            item.orderStatus="CLOSED"
                            listener?.onFinish(0)
                            dismiss()
                        }
                    })
                }
            })
        }
    }
    /**
     * 申请退货
    * */
    fun applyRefund(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.str_areYouApplyReturn),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() { dismiss() }
                override fun onRightClick() {
                    viewModel?.applyRefund(item.orderNo,object:OnPerformListener{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            item.orderStatusName=""
                            item.orderStatus="RTING"//退货中
                            listener?.onFinish(0)
                            dismiss()
                            ToastUtils.reToast(R.string.str_applyRefundComplete)
//                            PublicPop(context).apply {
//                                showPopupWindow(context.getString(R.string.str_applyRefundComplete),context.getString(R.string.str_iKnow),object :
//                                    PublicPop.OnPopClickListener{
//                                    override fun onLeftClick() { dismiss() }
//                                    override fun onRightClick() { dismiss() }
//                                })
//                            }
                        }
                    })
                }
            })
        }
    }
}