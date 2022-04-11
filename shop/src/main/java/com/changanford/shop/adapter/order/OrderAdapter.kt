package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderBriefBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.OrderSkuItem
import com.changanford.common.bean.SnapshotOfAttrOption
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding
import com.changanford.shop.ui.order.PostEvaluationActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.view.TypefaceTextView
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat


class OrderAdapter(var orderSource:Int=-2,var nowTime:Long?=0,val viewModel: OrderViewModel?=null): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    private val btnWidth by lazy { (ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,55f))/4 }
    private val dp30 by lazy { ScreenUtils.dp2px(context,30f) }
    //orderSource -2所有订单
    private val control by lazy { OrderControl(context,viewModel) }
    private val orderTypes= arrayOf("未知0","试驾订单","购车订单","商品订单","众筹订单","未知5","未知6","未知7","未知8")
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, itemData: OrderItemBean) {
        holder.dataBinding?.apply{
            val position=holder.absoluteAdapterPosition
            initBtn(this)
            val item=dataFormat(this,itemData)
            tvTotal.visibility=tvTotalPrice.visibility
            inGoodsInfo.tvTotalNum.visibility=tvTotalPrice.visibility
            if(TextUtils.isEmpty(item.orderStatusName))item.orderStatusName=viewModel?.getOrderStatus(item.orderStatus,item.evalStatus)
            this.model=item
            this.executePendingBindings()
            updateBtnUI(position,this,item)
            control.bindingGoodsInfo(this.inGoodsInfo,item)
            setOrderType(this.tvOrderType,item)
            bindTotalPrice(tvTotalPrice,item)
        }
    }
    private fun bindTotalPrice(tv: TypefaceTextView,item: OrderItemBean){
        if(tv.visibility==View.VISIBLE){
            item.apply {
                var drawableStart=if(!TextUtils.isEmpty(fb)&&fb!!.toFloat()>0f)ContextCompat.getDrawable(context,R.mipmap.ic_shop_fb_42) else null
                val endStr=if(!TextUtils.isEmpty(rmb)&&rmb!!.toFloat()>0)"￥$rmb" else ""
                tv.text=if(drawableStart!=null&&!TextUtils.isEmpty(endStr))"$fb+$endStr" else if(TextUtils.isEmpty(endStr))"$fb" else endStr
                if(TextUtils.isEmpty(endStr))drawableStart=ContextCompat.getDrawable(context,R.mipmap.ic_shop_fb_42)
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart,null,null,null)
            }
        }
    }
    private fun initBtn(dataBinding:ItemOrdersGoodsBinding){
        dataBinding.apply {
          btnConfirm.layoutParams.apply {
                width=btnWidth
                height=dp30
                btnConfirm.layoutParams=this
            }
            btnCancel.layoutParams.apply {
                width=btnWidth
                height=dp30
                btnCancel.layoutParams=this
            }
            btnLogistics.layoutParams.apply {
                width=btnWidth
                height=dp30
                btnLogistics.layoutParams=this
            }
            btnInvoice.layoutParams.apply {
                width=btnWidth
                height=dp30
                btnInvoice.layoutParams=this
            }
        }
    }
    /**
     * 数据格式化（主要针对聚合列表和商品列表数据格式不统一的问题）
    * */
    private fun dataFormat(dataBinding:ItemOrdersGoodsBinding,item: OrderItemBean):OrderItemBean{
        dataBinding.tvTotalPrice.visibility=View.VISIBLE
        dataBinding.inGoodsInfo.apply {
            tvCarInfo.visibility=View.GONE
//            tvGoodsNumber.visibility=View.VISIBLE
        }
        if(-2==orderSource){
            item.apply {
                spuName=skuName
                skuImg=orderImg
            }
            when(item.orderType){
                1->{//试驾
                    dataBinding.inGoodsInfo.apply {
                        recyclerView.visibility=View.GONE
                        tvCarInfo.visibility = View.VISIBLE
                        tvCarInfo.text=item.orderBrief
                    }
                    dataBinding.tvTotalPrice.visibility=View.GONE
                }
                2->{//购车 - orderBrief数据结构待定
                    dataBinding.inGoodsInfo.apply {
                        recyclerView.visibility=View.GONE
                        tvCarInfo.visibility = View.VISIBLE
                        tvCarInfo.text=item.orderBrief
                    }
                    dataBinding.tvTotalPrice.visibility=View.GONE
                }
                3->{//商品
                    val newBean= Gson().fromJson(item.orderBrief, OrderItemBean::class.java)
                    if(newBean.isNewOrder=="YES"){//113以后的订单
                        item.apply {
                            this.busSourse = newBean.busSourse
                            this.hagglePrice = newBean.hagglePrice
                            this.skuOrderVOList = newBean.skuOrderVOList
                            this.mallMallOrderId = newBean.mallMallOrderId
                            this.rmb = newBean.rmb
                            this.fb = newBean.fb
                            this.totalNum = newBean.totalNum
                        }
                    }else{
                        val orderBriefBean= Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
                        var specifications=""
                        val snapshotOfAttrOption=orderBriefBean.snapshotOfAttrOption
                        if(!TextUtils.isEmpty(snapshotOfAttrOption)){
                            val attrOption: List<SnapshotOfAttrOption> = Gson().fromJson(snapshotOfAttrOption, object : TypeToken<List<SnapshotOfAttrOption?>?>() {}.type)
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
                            this.rmb=getRMB(this.fbCost,"")
                            this.fb= this.fbCost
                            this.totalNum=orderBriefBean.buyNum
                            val skuItem=OrderSkuItem(skuImg=skuImg, specifications = specifications,spuName=skuName)
                            this.skuOrderVOList= arrayListOf(skuItem)
                        }
                    }
                }
                4->{//活动订单-众筹
                    val orderBriefBean= Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
                    item.apply {
                        this.buyNum=orderBriefBean.num
                        this.fbCost=orderBriefBean.totalPrice
                        this.fbOfUnitPrice=orderBriefBean.price
                    }
                }
            }
        }
        return item
    }
    private fun updateBtnUI(position:Int,dataBinding:ItemOrdersGoodsBinding,item: OrderItemBean){
        dataBinding.btnCancel.visibility=View.GONE
        if(-2!=orderSource){ //聚合订单将不展示操作按钮
            val evalStatus=item.evalStatus
            val orderStatus=item.orderStatus
            if("FINISH"==orderStatus&&null!=evalStatus&&"WAIT_EVAL"==evalStatus){//待评价
                dataBinding.apply {
                    btnCancel.apply {//申请售后
                        visibility=View.VISIBLE
                        setText(R.string.str_applyRefund)
                        setOnClickListener {

                        }
                    }
                    btnLogistics.apply {//查看物流
                        visibility=View.VISIBLE
                        setOnClickListener {

                        }
                    }
                    btnInvoice.apply {//申请发票
                        visibility=View.VISIBLE
                        setOnClickListener {

                        }
                    }
                    btnConfirm.apply {//评价
                        visibility=View.VISIBLE
                        setText(R.string.str_eval)
                        setOnClickListener {
                            item.apply {
                                WBuriedUtil.clickShopOrderComment(orderNo,spuName,rmb?:fb)
                            }
                            PostEvaluationActivity.start(item.orderNo)
                        }
                        setBackgroundResource(R.drawable.bord_00095b_15dp)
                    }
                }
            }else{
                when(orderStatus){
                    //待付款->可立即支付、取消支付
                    "WAIT_PAY"-> {
                        dataBinding.btnLogistics.visibility=View.GONE
                        dataBinding.btnInvoice.visibility=View.GONE
                        dataBinding.btnConfirm.apply {
                            visibility=View.VISIBLE
                            setText(R.string.str_immediatePayment)
                            setOnClickListener {
                                item.apply {
                                    WBuriedUtil.clickShopOrderPay(orderNo,spuName,rmb?:fb)
                                }
                                control.toPay(item)
                            }
                        }
                        dataBinding.btnCancel.apply {
                            visibility=View.VISIBLE
                            //取消订单
                            setOnClickListener {cancelOrder(position,item)}
                        }
                    }
                    //待发货
                    "WAIT_SEND"-> {
                        dataBinding.apply {
                            btnCancel.visibility=View.GONE
                            btnLogistics.visibility=View.GONE
                            btnInvoice.apply {//申请发票
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnConfirm.apply {//申请退款
                                visibility=View.VISIBLE
                                setText(R.string.str_applyARefund)
                                setBackgroundResource(R.drawable.bord_99_15dp)
                                setOnClickListener {

                                }
                            }
                        }
                    }
                    //退款中
                    "WAIT_SYS_REGIST"-> {
                        dataBinding.apply {
                            btnCancel.visibility=View.GONE
                            btnLogistics.visibility=View.GONE
                            btnInvoice.visibility=View.GONE
                            btnConfirm.visibility=View.INVISIBLE
                        }
                    }
                    //待收货->可确认收货
                    "WAIT_RECEIVE"-> {
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility=View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {

                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnInvoice.apply {//申请发票
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnConfirm.apply {//确认收货
                                visibility=View.VISIBLE
                                setText(R.string.str_confirmGoods)
                                setOnClickListener {
                                    confirmGoods(position,item)
                                }
                                setBackgroundResource(R.drawable.bord_00095b_15dp)
                            }
                        }
                    }
                    //已完成->可再次购买
                    "FINISH"->{
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility=View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {

                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnInvoice.apply {//申请发票
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                        }
                        if("2"!=item.busSourse){
                            dataBinding.btnConfirm.apply {
                                visibility=View.VISIBLE
                                setText(R.string.str_onceAgainToBuy)
                                setBackgroundResource(R.drawable.bord_00095b_15dp)
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderBuy(orderNo,spuName,rmb?:fb)
                                    }
                                    control.onceAgainToBuy(item)
                                }
                            }
                        }else dataBinding.btnConfirm.visibility=View.INVISIBLE
                    }
                    //已关闭->可再次购买
                    "CLOSED"->{
                        dataBinding.btnLogistics.visibility=View.GONE
                        dataBinding.btnInvoice.visibility=View.GONE
                        dataBinding.btnCancel.visibility=View.GONE
                        if("2"!=item.busSourse){
                            dataBinding.btnConfirm.apply {
                                visibility=View.VISIBLE
                                setText(R.string.str_onceAgainToBuy)
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderBuy(orderNo,spuName,rmb)
                                    }
                                    control.onceAgainToBuy(item)
                                }
                            }
                        }else dataBinding.btnConfirm.visibility=View.INVISIBLE
                    }
                    //待评价
                    "WAIT_EVAL"->{
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility=View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {

                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnInvoice.apply {//申请发票
                                visibility=View.VISIBLE
                                setOnClickListener {

                                }
                            }
                            btnConfirm.apply {//评价
                                visibility=View.VISIBLE
                                setText(R.string.str_eval)
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderComment(orderNo,spuName,rmb?:fb)
                                    }
                                    PostEvaluationActivity.start(item.orderNo)
                                }
                                setBackgroundResource(R.drawable.bord_00095b_15dp)
                            }
                        }
                    }
                    //未知
                    else ->{
                        dataBinding.btnLogistics.visibility=View.GONE
                        dataBinding.btnInvoice.visibility=View.GONE
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
                    setTextColor(ContextCompat.getColor(context,R.color.color_74889D))
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
        item.apply {
            WBuriedUtil.clickShopOrderTakeDelivery(orderNo,spuName,rmb?:fb)
        }
        control.confirmGoods(item,object : OnPerformListener {
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
        item.apply {
            WBuriedUtil.clickShopOrderCancel(orderNo,spuName,rmb?:fb)
        }
        control.cancelOrder(item,object : OnPerformListener {
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