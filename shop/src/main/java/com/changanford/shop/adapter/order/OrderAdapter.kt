package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderBriefBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.ui.order.OrderEvaluationActivity
import com.changanford.shop.view.TypefaceTextView
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat


class OrderAdapter(private val orderType:Int=-1,var nowTime:Long?=0,val viewModel: OrderViewModel?=null): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    //orderType -1所有订单 0 商品、1购车 2 试驾
    private val control by lazy { OrderControl(context,viewModel) }
    private val orderTypes= arrayOf("未知0","购车订单","试驾订单","商品订单","未知4","未知5","未知6","")
    @SuppressLint("SimpleDateFormat")
    private val sf = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataFormat(item)
            if(TextUtils.isEmpty(item.orderStatusName))item.orderStatusName=viewModel?.getOrderStatus(item.orderStatus,item.evalStatus)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            updateBtnUI(dataBinding,item)
            dataBinding.tvTotleIntegral.setHtmlTxt(context.getString(R.string.str_Xfb,item.fbCost),"#00095B")
            dataBinding.inGoodsInfo.apply {
                model=item
                GlideUtils.loadBD(GlideUtils.handleImgUrl(item.skuImg),imgGoodsCover)
                tvOrderType.apply {
                    visibility = when {
                        "YES"==item.seckill -> {//秒杀
                            setText(R.string.str_seckill)
                            View.VISIBLE
                        }
                        "YES"==item.haggleOrder -> {//砍价
                            setText(R.string.str_bargaining)
                            View.VISIBLE
                        }
                        else -> View.GONE
                    }
                }
                recyclerView.layoutManager=FlowLayoutManager(context,false)
                recyclerView.adapter=OrderGoodsAttributeAdapter().apply {
                    val specifications=item.specifications.split(",").filter { ""!=it }
                    setList(specifications)
                }
            }
            setOrderType(dataBinding.tvOrderType,item)

        }
    }
    /**
     * 数据格式化（主要针对聚合列表和商品列表数据格式不统一的问题）
    * */
    private fun dataFormat(item: OrderItemBean){
        if(-1==orderType){
            val orderBriefBean= Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
            item.apply {
                spuName=skuName
                skuImg=orderImg
                buyNum=orderBriefBean.buyNum
                payType=orderBriefBean.payType
                fbCost=orderBriefBean.fbCost
            }
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
                            setOnClickListener { control.toPay(item) }
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
                            setOnClickListener {control.onceAgainToBuy(item)}
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
                    val payEndTime=(nowTime?:System.currentTimeMillis())+(item.waitPayCountDown?:0)*1000
                    text=sf.format(payEndTime)
                    setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
                    visibility = View.VISIBLE
                }
                else -> text=""
            }
        }


    }

    /**
     * 确认收货
     * */
    private fun confirmGoods(item: OrderItemBean){
        control.confirmGoods(item,object :OnPerformListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * 取消订单
    * */
    private fun cancelOrder(item: OrderItemBean){
        control.cancelOrder(item,object :OnPerformListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.notifyDataSetChanged()
            }
        })
    }
}