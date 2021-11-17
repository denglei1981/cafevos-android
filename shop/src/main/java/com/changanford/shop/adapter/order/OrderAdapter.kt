package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderBriefBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.SnapshotOfAttrOption
import com.changanford.shop.R
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.ui.order.OrderEvaluationActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.view.TypefaceTextView
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat


class OrderAdapter(var orderSource:Int=-2,var nowTime:Long?=0,val viewModel: OrderViewModel?=null): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    //orderSource -2所有订单
    private val control by lazy { OrderControl(context,viewModel) }
    private val orderTypes= arrayOf("未知0","试驾订单","购车订单","商品订单","未知4","未知5","未知6","未知7","未知8")
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val position=holder.absoluteAdapterPosition
            dataFormat(dataBinding,item)
            if(TextUtils.isEmpty(item.orderStatusName))item.orderStatusName=viewModel?.getOrderStatus(item.orderStatus,item.evalStatus)
            dataBinding.model=item
            dataBinding.executePendingBindings()
            updateBtnUI(position,dataBinding,item)
            dataBinding.tvTotleIntegral.setHtmlTxt(context.getString(R.string.str_Xfb,item.fbCost),"#00095B")
            control.bindingGoodsInfo(dataBinding.inGoodsInfo,item)
            setOrderType(dataBinding.tvOrderType,item)
        }
    }
    /**
     * 数据格式化（主要针对聚合列表和商品列表数据格式不统一的问题）
    * */
    private fun dataFormat(dataBinding:ItemOrdersGoodsBinding,item: OrderItemBean){
        dataBinding.tvTotleIntegral.visibility=View.VISIBLE
        dataBinding.inGoodsInfo.apply {
            tvCarInfo.visibility=View.GONE
            tvGoodsNumber.visibility=View.VISIBLE
            tvIntegral.visibility=View.VISIBLE
        }
        if(-2==orderSource){
            item.apply {
                spuName=skuName
                skuImg=orderImg
            }
            when(item.orderType){
                1->{//试驾
                    dataBinding.inGoodsInfo.apply {
                        tvGoodsNumber.visibility=View.GONE
                        recyclerView.visibility=View.GONE
                        tvIntegral.visibility=View.GONE
                        tvCarInfo.visibility = View.VISIBLE
                        tvCarInfo.text=item.orderBrief
                    }
                    dataBinding.tvTotleIntegral.visibility=View.GONE
                }
                2->{//购车 - orderBrief数据结构待定
                    dataBinding.inGoodsInfo.apply {
                        tvGoodsNumber.visibility=View.GONE
                        recyclerView.visibility=View.GONE
                        tvIntegral.visibility=View.GONE
                        tvCarInfo.visibility = View.VISIBLE
                        tvCarInfo.text=item.orderBrief
                    }
                    dataBinding.tvTotleIntegral.visibility=View.GONE
                }
                3,4->{//商品、众筹
                    val orderBriefBean= Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
                    var specifications=""
                    orderBriefBean.snapshotOfAttrOption?.let {
                        val attrOption: List<SnapshotOfAttrOption> = Gson().fromJson(it, object : TypeToken<List<SnapshotOfAttrOption?>?>() {}.type)
                        for(item in attrOption){
                            specifications+="${item.optionName},"
                        }
                    }
                    //单价
                    val fbOfUnitPrice=orderBriefBean.fbOfUnitPrice?:(orderBriefBean.fbCost.toFloat()/orderBriefBean.buyNum.toInt())
                    item.apply {
                        this.buyNum=orderBriefBean.buyNum
                        payType=orderBriefBean.payType
                        this.fbCost="${WCommonUtil.getHeatNum(orderBriefBean.fbCost,0)}"
                        this.fbOfUnitPrice="${WCommonUtil.getHeatNum("$fbOfUnitPrice",0)}"
                        this.specifications=specifications
                        this.orginPrice=orderBriefBean.orginPrice
                        this.busSourse= orderBriefBean.busSourse
                        this.hagglePrice=orderBriefBean.hagglePrice
                    }
                }
            }

        }
    }
    private fun updateBtnUI(position:Int,dataBinding:ItemOrdersGoodsBinding,item: OrderItemBean){
        dataBinding.btnCancel.visibility=View.GONE
        if(-2!=orderSource){ //聚合订单将不展示操作按钮
            val evalStatus=item.evalStatus
            val orderStatus=item.orderStatus
            if("FINISH"==orderStatus&&null!=evalStatus&&"WAIT_EVAL"==evalStatus){//待评价
                dataBinding.btnConfirm.apply {
                    visibility=View.VISIBLE
                    setText(R.string.str_eval)
                    setOnClickListener { OrderEvaluationActivity.start(context,item.orderNo) }
                }
            }else{
                when(orderStatus){
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
                            setOnClickListener {cancelOrder(position,item)}
                        }
                    }
                    //待发货
                    "WAIT_SEND"-> {
                        dataBinding.btnConfirm.visibility=View.INVISIBLE
                    }
                    //待收货->可确认收货
                    "WAIT_RECEIVE"-> {
                        dataBinding.btnConfirm.apply {
                            visibility=View.VISIBLE
                            setText(R.string.str_confirmGoods)
                            setOnClickListener {confirmGoods(position,item)}
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
                -2==orderSource -> {
                    text=item.orderTypeName?:orderTypes[item.orderType]
                    setTextColor(ContextCompat.getColor(context,R.color.picture_color_66))
                    visibility = View.VISIBLE
                }
                "WAIT_PAY"==orderStatus -> {
                    //可支付结束时间=服务器当前时间+可支付剩余时间
//                    val payEndTime=(nowTime?:System.currentTimeMillis())+(item.waitPayCountDown?:0)*1000
                    text=simpleDateFormat.format(item.waitPayDuration)
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
    private fun confirmGoods(position:Int,item: OrderItemBean){
        control.confirmGoods(item,object :OnPerformListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.apply {
                    if(2==orderSource){
                        data.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * 取消订单
    * */
    private fun cancelOrder(position:Int,item: OrderItemBean){
        control.cancelOrder(item,object :OnPerformListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.apply {
                    if(0==orderSource){
                        data.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    notifyDataSetChanged()
                }

            }
        })
    }
}