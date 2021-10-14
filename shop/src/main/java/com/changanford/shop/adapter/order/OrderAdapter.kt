package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemOrdersGoodsBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.ui.order.OrderEvaluationActivity
import com.changanford.shop.ui.order.PayConfirmActivity
import com.changanford.shop.view.TypefaceTextView
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat


class OrderAdapter(private val orderType:Int=-1,var nowTime:Long?=0,val viewModel: OrderViewModel?=null): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    //orderType -1所有订单 0 商品、1购车 2 试驾
    private val orderTypes= arrayOf("商品订单","购车订单","试驾订单","","")
    @SuppressLint("SimpleDateFormat")
    private val sf = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            if(TextUtils.isEmpty(item.orderStatusName))item.orderStatusName=getOrderStatus(item.orderStatus,item.evalStatus)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            updateBtnUI(dataBinding,item)
            dataBinding.tvTotleIntegral.setHtmlTxt(context.getString(R.string.str_Xfb,item.fbCost),"#00095B")
            dataBinding.inGoodsInfo.apply {
                model=item
                GlideUtils.loadBD(GlideUtils.handleImgUrl(item.skuImg),imgGoodsCover)
            }
            setOrderType(dataBinding.tvOrderType,item)
        }
    }
    private fun updateBtnUI(dataBinding:ItemOrdersGoodsBinding,item: OrderItemBean){
        dataBinding.btnCancel.visibility=View.GONE
        if(-1!=orderType){ //聚合订单将不展示操作按钮
            val evalStatus=item.evalStatus
            if(null!=evalStatus&&"WAIT_EVAL"==evalStatus){//待评价
                dataBinding.btnConfirm.apply {
                    visibility=View.VISIBLE
                    setText(R.string.str_eval)
                    setOnClickListener { OrderEvaluationActivity.start(context,item.orderNo) }
                }
            }else{
                when(item.orderStatus){
                    //待付款->可立即支付、取消支付
                    "WAIT_PAY"-> {
                        dataBinding.btnConfirm.apply {
                            visibility=View.VISIBLE
                            setText(R.string.str_immediatePayment)
                            setOnClickListener { toPay(item) }
                        }
                        dataBinding.btnCancel.apply {
                            visibility=View.VISIBLE
                            //取消订单
                            setOnClickListener {cancelOrder(item)}
                        }
                    }
                    //待发货
                    "WAIT_SEND"-> {}
                    //待收货->可确认收货
                    "WAIT_RECEIVE"-> {
                        dataBinding.btnConfirm.apply {
                            visibility=View.VISIBLE
                            setText(R.string.str_confirmGoods)
                            setOnClickListener {confirmGoods(item)}
                        }
                    }
                    //已完成,已关闭->可再次购买
                    "FINISH","CLOSED"->{
                        dataBinding.btnConfirm.apply {
                            visibility=View.VISIBLE
                            setText(R.string.str_onceAgainToBuy)
                            setOnClickListener {onceAgainToBuy(item)}
                        }
                    }
                    //未知
                    else ->{
                        dataBinding.btnCancel.visibility=View.GONE
                        dataBinding.btnConfirm.visibility=View.INVISIBLE
                    }
                }
            }
        }
    }
    /**
     * 评价状态
     * [evalStatus]WAIT_EVAL 待评价 、WAIT_CHECK 待审核 、ON_SHELVE 已上架 、UNDER_SHELVE 已下架 、CHECK_FAILURE 审核不通过
    * */
    private fun getEvalStatus(evalStatus:String){}

    private fun setOrderType(tv:TypefaceTextView,item: OrderItemBean){
        val orderStatus=item.orderStatus
        tv.apply {
            when {
                -1==orderType -> {
                    text=orderTypes[item.orderType]
                    setTextColor(ContextCompat.getColor(context,R.color.picture_color_66))
                    visibility = View.VISIBLE
                }
                "WAIT_PAY"==orderStatus -> {
                    //可支付结束时间=服务器当前时间+可支付剩余时间
                    val payEndTime=(nowTime?:System.currentTimeMillis())+item.waitPayCountDown*1000
                    text=sf.format(payEndTime)
                    setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
                    visibility = View.VISIBLE
                }
                else -> text=""
            }
        }


    }
    /**
     * 再次购买->创建订单页面
    * */
    private fun onceAgainToBuy(item: OrderItemBean){
//        val detailBean=GoodsDetailBean()
//        OrderConfirmActivity.start(context,"goodsInfo")
    }
    /**
     * 订单状态(WAIT_PAY 待付款,WAIT_SEND 待发货,WAIT_RECEIVE 待收货,FINISH 已完成,CLOSED 已关闭)
     * */
    private fun getOrderStatus(orderStatus:String,evalStatus:String?):String{
        return if(evalStatus!=null&&"WAIT_EVAL"==evalStatus)"待评价" else {
            when(orderStatus){
                "WAIT_PAY"->"待付款"
                "WAIT_SEND"->"待发货"
                "WAIT_RECEIVE"->"待收货"
                "FINISH"->"已完成"
                "CLOSED"->"已关闭"
                else ->"未知"
            }
        }
    }
    /**
     * 确认收货
     * */
    private fun confirmGoods(item: OrderItemBean){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.str_confirmReceiptGoods),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() {
                    dismiss()
                }
                override fun onRightClick() {
                    viewModel?.confirmReceipt(item.orderNo,object :OnPerformListener{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            ToastUtils.showShortToast(R.string.str_goodsSuccessfully,context)
                            item.orderStatus="FINISH"
                            this@OrderAdapter.notifyDataSetChanged()
                            dismiss()
                        }
                    })
                }
            })
        }
    }
    /**
     * 去支付
    * */
    private fun toPay(item: OrderItemBean){
        PayConfirmActivity.start(context,Gson().toJson(OrderInfoBean(item.orderNo,item.fbCost)))
    }
    /**
     * 取消订单
    * */
    private fun cancelOrder(item: OrderItemBean){
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
                            this@OrderAdapter.notifyDataSetChanged()
                            dismiss()
                        }
                    })
                }
            })
        }
    }
}