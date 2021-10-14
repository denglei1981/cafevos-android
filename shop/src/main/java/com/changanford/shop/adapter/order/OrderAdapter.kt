package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemOrdersGoodsBinding
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.view.TypefaceTextView
import java.text.SimpleDateFormat


class OrderAdapter(private val orderType:Int=-1,var nowTime:Long?=0): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    //orderType -1所有订单 0 商品、1购车 2 试驾
    private val orderTypes= arrayOf("商品订单","1购车订单","试驾订单","","")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            item.orderStatusTxt=getOrderStatus(item.orderStatus)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            dataBinding.tvTotleIntegral.setHtmlTxt(context.getString(R.string.str_Xfb,item.fbCost),"#00095B")
            dataBinding.inGoodsInfo.apply {
                model=item
                GlideUtils.loadBD(GlideUtils.handleImgUrl(item.skuImg),imgGoodsCover)
            }
            setOrderType(dataBinding.tvOrderType,item)
            //取消订单
            dataBinding.tvBtnCancel.setOnClickListener {
                val pop=PublicPop(context)
                pop.showPopupWindow(context.getString(R.string.prompt_cancelOrder),null,null,object :PublicPop.OnPopClickListener{
                    override fun onLeftClick() {
                        pop.dismiss()
                    }
                    override fun onRightClick() {
                        pop.dismiss()
                    }
                })
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    private val sf = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")
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
                    //支付结束时间=服务器时间+支付剩余时间
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
     * 订单状态(WAIT_PAY 待付款,WAIT_SEND 待发货,WAIT_RECEIVE 待收货,FINISH 已完成,CLOSED 已关闭)
     * */
    private fun getOrderStatus(orderStatus:String):String{
       return when(orderStatus){
           "WAIT_PAY"->"待付款"
           "WAIT_SEND"->"待发货"
           "WAIT_RECEIVE"->"待收货"
           "FINISH"->"已完成"
           "CLOSED"->"已关闭"
           else ->"未知"
        }
    }
}