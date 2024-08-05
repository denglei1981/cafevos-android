package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderBriefBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.OrderSkuItem
import com.changanford.common.bean.SnapshotOfAttrOption
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.util.JumpUtils
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


class OrderAdapter(
    var orderSource: Int = -2,
    var nowTime: Long? = 0,
    val viewModel: OrderViewModel? = null
) : BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods) {
    private val btnWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(
            context,
            55f
        )) / 4
    }
    private val dp30 by lazy { ScreenUtils.dp2px(context, 30f) }

    //orderSource -2所有订单
    private val control by lazy { OrderControl(context, viewModel) }
    private val orderTypes = arrayOf(
        "未知0",
        "试驾订单",
        "购车订单",
        "商品订单",
        "众筹订单",
        "未知5",
        "未知6",
        "未知7",
        "未知8"
    )

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("请在MM月dd日 HH:mm 前支付")

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>,
        itemData: OrderItemBean
    ) {
        holder.dataBinding?.apply {
            val position = holder.absoluteAdapterPosition
            initBtn(this)
            val item = dataFormat(this, itemData)
            inGoodsInfo.tvTotal.visibility = inGoodsInfo.tvTotalPrice.visibility
            inGoodsInfo.tvTotalNum.visibility = inGoodsInfo.tvTotalPrice.visibility
            if (TextUtils.isEmpty(item.orderStatusName)) item.orderStatusName =
                viewModel?.getOrderStatus(item.orderStatus, item.evalStatus)
            this.model = item
            this.executePendingBindings()
            updateBtnUI(position, this, item)
            control.bindingGoodsInfo(this.inGoodsInfo, item)
            setOrderType(this.tvOrderType, item)
            bindTotalPrice(inGoodsInfo.tvTotalPrice, item)
        }
    }

    private fun bindTotalPrice(tv: TypefaceTextView, item: OrderItemBean) {
        if (tv.visibility == View.VISIBLE) {
            item.apply {
                var drawableStart =
                    if (!TextUtils.isEmpty(fb) && fb!!.toFloat() > 0f) ContextCompat.getDrawable(
                        context,
                        R.mipmap.ic_shop_fb_42
                    ) else null
                val endStr = if (!TextUtils.isEmpty(rmb) && rmb!!.toFloat() > 0) "￥$rmb" else ""
                tv.text =
                    if (drawableStart != null && !TextUtils.isEmpty(endStr)) "$fb+$endStr" else if (TextUtils.isEmpty(
                            endStr
                        )
                    ) "$fb" else endStr
                if (TextUtils.isEmpty(endStr)) drawableStart =
                    ContextCompat.getDrawable(context, R.mipmap.ic_shop_fb_42)
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null)
            }
        }
    }

    private fun initBtn(dataBinding: ItemOrdersGoodsBinding) {
        dataBinding.apply {
            btnConfirm.layoutParams.apply {
                width = btnWidth
                height = dp30
                btnConfirm.layoutParams = this
            }
            btnCancel.layoutParams.apply {
                width = btnWidth
                height = dp30
                btnCancel.layoutParams = this
            }
            btnLogistics.layoutParams.apply {
                width = btnWidth
                height = dp30
                btnLogistics.layoutParams = this
            }
            btnInvoice.layoutParams.apply {
                width = btnWidth
                height = dp30
                btnInvoice.layoutParams = this
            }
        }
    }

    /**
     * 数据格式化（主要针对聚合列表和商品列表数据格式不统一的问题）
     * */
    private fun dataFormat(
        dataBinding: ItemOrdersGoodsBinding,
        item: OrderItemBean
    ): OrderItemBean {
        dataBinding.inGoodsInfo.tvTotalPrice.visibility = View.VISIBLE
        dataBinding.inGoodsInfo.apply {
            tvCarInfo.visibility = View.GONE
//            tvGoodsNumber.visibility=View.VISIBLE
        }
        if (-2 == orderSource) {
            item.apply {
                spuName = skuName
                skuImg = orderImg
            }
            when (item.orderType) {
                1, 2 -> {//试驾、购车
                    dataBinding.inGoodsInfo.apply {
                        recyclerView.visibility = View.GONE
                        tvCarInfo.visibility = View.VISIBLE
                        tvCarInfo.text = item.orderBrief
                    }
                    dataBinding.inGoodsInfo.tvTotalPrice.visibility = View.GONE
                }

                3 -> {//商品
                    val newBean = Gson().fromJson(item.orderBrief, OrderItemBean::class.java)
                    if (newBean.isNewOrder == "YES") {//113以后的订单
                        item.apply {
                            this.busSource = newBean.busSource
                            this.busSourse = newBean.busSourse
                            this.hagglePrice = newBean.hagglePrice
                            this.skuOrderVOList = newBean.skuOrderVOList
                            this.mallMallOrderId = newBean.mallMallOrderId
                            this.rmb = newBean.rmb
                            this.fb = "${
                                com.changanford.common.wutil.WCommonUtil.getHeatNumUP(
                                    newBean.fb,
                                    0
                                )
                            }"
                            this.totalNum = newBean.totalNum
                        }
                    } else {
                        val orderBriefBean =
                            Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
                        var specifications = ""
                        val snapshotOfAttrOption = orderBriefBean.snapshotOfAttrOption
                        if (!TextUtils.isEmpty(snapshotOfAttrOption)) {
                            val attrOption: List<SnapshotOfAttrOption> = Gson().fromJson(
                                snapshotOfAttrOption,
                                object : TypeToken<List<SnapshotOfAttrOption?>?>() {}.type
                            )
                            for (item in attrOption) {
                                specifications += "${item.optionName},"
                            }
                        }
                        //单价
                        val fbOfUnitPrice = orderBriefBean.fbOfUnitPrice
                            ?: (orderBriefBean.fbCost.toFloat() / orderBriefBean.buyNum.toInt())
                        item.apply {
                            this.buyNum = orderBriefBean.buyNum
                            payType = orderBriefBean.payType
                            this.fbCost = "${WCommonUtil.getHeatNum(orderBriefBean.fbCost, 0)}"
                            this.fbOfUnitPrice = "${WCommonUtil.getHeatNum("$fbOfUnitPrice", 0)}"
                            this.specifications = specifications
                            this.orginPrice = orderBriefBean.orginPrice
                            this.busSourse = orderBriefBean.busSourse
                            this.busSource = orderBriefBean.busSource
                            this.hagglePrice = orderBriefBean.hagglePrice
//                            this.rmb=getRMB(this.fbCost,"")
                            this.fb = this.fbCost
                            this.totalNum = orderBriefBean.buyNum
                            val skuItem = OrderSkuItem(
                                skuImg = skuImg,
                                specifications = specifications,
                                spuName = skuName
                            )
                            this.skuOrderVOList = arrayListOf(skuItem)
                        }
                    }
                }

                4 -> {//活动订单-众筹
                    val orderBriefBean =
                        Gson().fromJson(item.orderBrief, OrderBriefBean::class.java)
                    item.apply {
                        this.buyNum = orderBriefBean.num
                        this.fbCost = orderBriefBean.totalPrice
                        this.fbOfUnitPrice = orderBriefBean.price
                    }
                }
            }
        }
        return item
    }

    private fun updateBtnUI(
        position: Int,
        dataBinding: ItemOrdersGoodsBinding,
        item: OrderItemBean
    ) {
        dataBinding.btnCancel.visibility = View.GONE
        if (-2 != orderSource) { //聚合订单将不展示操作按钮
            val evalStatus = item.evalStatus
            val orderStatus = item.orderStatus
            dataBinding.viewN.isVisible = true
            dataBinding.composeView.isVisible = false
            if ("FINISH" == orderStatus && null != evalStatus && "WAIT_EVAL" == evalStatus) {//待评价
                dataBinding.apply {
                    btnCancel.apply {//申请售后
                        visibility = View.VISIBLE
                        setText(R.string.str_applyRefund)
                        setOnClickListener {
                            control.orderBtnClick(3, item)
                        }
                    }
                    btnLogistics.apply {//查看物流
                        visibility = View.VISIBLE
                        setOnClickListener {
                            control.orderBtnClick(2, item)
                        }
                    }
                    //申请发票
                    initBtnLogistics(btnInvoice, item)

                    btnConfirm.apply {//评价
                        visibility = View.VISIBLE
                        setText(R.string.str_eval)
                        setOnClickListener {
                            item.apply {
                                WBuriedUtil.clickShopOrderComment(orderNo, spuName, rmb ?: fb)
                            }
                            PostEvaluationActivity.start(item.orderNo)
                        }
                        setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
            } else {
                when (orderStatus) {
                    //待付款->可立即支付、取消支付
                    "WAIT_PAY" -> {
                        dataBinding.btnLogistics.visibility = View.GONE
                        dataBinding.btnInvoice.visibility = View.GONE
                        dataBinding.btnConfirm.apply {
                            visibility = View.VISIBLE
                            setText(R.string.str_immediatePayment)
                            setOnClickListener {
                                item.apply {
                                    WBuriedUtil.clickShopOrderPay(orderNo, spuName, rmb ?: fb)
                                }
                                control.toPay(item)
                            }
//                            setBackgroundResource(R.drawable.bord_00095b_15dp)
                            setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                            setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                        dataBinding.btnCancel.apply {
                            visibility = View.VISIBLE
                            setText(R.string.str_cancelOrder)
                            //取消订单
                            setOnClickListener { cancelOrder(position, item) }
                        }
                    }
                    //待发货
                    "WAIT_SEND" -> {
                        dataBinding.apply {
                            btnCancel.visibility = View.GONE
                            btnLogistics.visibility = View.GONE

                            //申请发票
                            initBtnLogistics(btnInvoice, item)

                            btnConfirm.apply {//申请退款
                                visibility = View.VISIBLE
                                setText(R.string.str_applyARefund)
                                setBackgroundResource(R.drawable.bg_bord_80a6_23)
                                setTextColor(ContextCompat.getColor(context, R.color.color_d916))
                                setOnClickListener {
                                    control.orderBtnClick(4, item)
                                }
                            }
                        }
                    }
                    //退款中
                    "WAIT_SYS_REGIST" -> {
                        dataBinding.apply {
                            btnCancel.visibility = View.GONE
                            btnLogistics.visibility = View.GONE
                            btnInvoice.visibility = View.GONE
                            btnConfirm.visibility = View.GONE
                            dataBinding.viewN.isVisible = false
                            composeView.isVisible = true
                            composeView.setContent {
                                ItemCompose(item)
                            }
                            btnCancel.apply {//售后详情
                                visibility = View.VISIBLE
                                setText(R.string.str_afterDetails)
                                setOnClickListener {
                                    item.apply {
                                        //整单退
                                        if (refundType == "ALL_ORDER") JumpUtils.instans?.jump(
                                            124,
                                            mallMallRefundId
                                        )
                                        //单SKU退
                                        else JumpUtils.instans?.jump(126, mallMallRefundId)

                                    }
                                }
                            }
                        }
                    }
                    //待收货->可确认收货
                    "WAIT_RECEIVE" -> {
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility = View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {
                                    control.orderBtnClick(3, item)
                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility = View.VISIBLE
                                setOnClickListener {
                                    control.orderBtnClick(2, item)
                                }
                            }
                            //申请发票
                            initBtnLogistics(btnInvoice, item)

                            btnConfirm.apply {//确认收货
                                visibility = View.VISIBLE
                                setText(R.string.str_confirmGoods)
                                setOnClickListener {
                                    confirmGoods(position, item)
                                }
                                setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                                setTextColor(ContextCompat.getColor(context, R.color.white))
                            }
                        }
                    }
                    //已完成->可再次购买
                    "FINISH" -> {
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility = View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {
                                    control.orderBtnClick(3, item)
                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility = View.VISIBLE
                                setOnClickListener {
                                    control.orderBtnClick(2, item)
                                }
                            }
                            //申请发票
                            initBtnLogistics(btnInvoice, item)
                        }
                        if ("2" != item.busSourse) {
                            dataBinding.btnConfirm.apply {
                                visibility = View.GONE
                                setText(R.string.str_onceAgainToBuy)
//                                setBackgroundResource(R.drawable.bord_00095b_15dp)
                                setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                                setTextColor(ContextCompat.getColor(context, R.color.white))
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderBuy(orderNo, spuName, rmb ?: fb)
                                    }
                                    control.onceAgainToBuy(item)
                                }
                            }
                        } else dataBinding.btnConfirm.visibility = View.GONE
                    }
                    //已关闭->可再次购买
                    "CLOSED" -> {
                        dataBinding.btnLogistics.visibility = View.GONE
                        dataBinding.btnInvoice.visibility = View.GONE
                        dataBinding.btnCancel.visibility = View.GONE
                        dataBinding.viewN.isVisible = false
                        if ("2" != item.busSourse) {
                            dataBinding.btnConfirm.apply {
                                visibility = View.GONE
                                setText(R.string.str_onceAgainToBuy)
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderBuy(orderNo, spuName, rmb)
                                    }
                                    control.onceAgainToBuy(item)
                                }
//                                setBackgroundResource(R.drawable.bord_00095b_15dp)
                                setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                                setTextColor(ContextCompat.getColor(context, R.color.white))
                            }
                        } else dataBinding.btnConfirm.visibility = View.GONE
                    }
                    //待评价
                    "WAIT_EVAL" -> {
                        dataBinding.apply {
                            btnCancel.apply {//申请售后
                                visibility = View.VISIBLE
                                setText(R.string.str_applyRefund)
                                setOnClickListener {
                                    control.orderBtnClick(3, item)
                                }
                            }
                            btnLogistics.apply {//查看物流
                                visibility = View.VISIBLE
                                setOnClickListener {
                                    control.orderBtnClick(2, item)
                                }
                            }
                            //申请发票
                            initBtnLogistics(btnInvoice, item)

                            btnConfirm.apply {//评价
                                visibility = View.VISIBLE
                                setText(R.string.str_eval)
                                setOnClickListener {
                                    item.apply {
                                        WBuriedUtil.clickShopOrderComment(
                                            orderNo,
                                            spuName,
                                            rmb ?: fb
                                        )
                                    }
                                    PostEvaluationActivity.start(item.orderNo)
                                }
                                setBackgroundResource(R.drawable.bg_shape_1700f4_23)
                                setTextColor(ContextCompat.getColor(context, R.color.white))
                            }
                        }
                    }
                    //未知
                    else -> {
                        dataBinding.btnLogistics.visibility = View.GONE
                        dataBinding.btnInvoice.visibility = View.GONE
                        dataBinding.btnCancel.visibility = View.GONE
                        dataBinding.btnConfirm.visibility = View.GONE
                        dataBinding.viewN.isVisible = false
                    }
                }
            }
            //维保订单没有查看物流
            if (item.busSource == "WB") dataBinding.btnLogistics.visibility = View.GONE
        }
    }

    /**
     * 申请发票-只有人民币支付才需要开票
     * */
    private fun initBtnLogistics(btnInvoice: AppCompatButton, item: OrderItemBean) {
        btnInvoice.apply {
            if ((item.rmb ?: "0").toFloat() > 0) {
                visibility = View.VISIBLE
                setText(if (item.invoiced == "NOT_BEGIN") R.string.str_applyInvoice else R.string.str_lookInvoice)
                setOnClickListener {
                    control.orderBtnClick(0, item)
                }
            } else visibility = View.GONE
        }
    }

    private fun setOrderType(tv: TypefaceTextView, item: OrderItemBean) {
        val orderStatus = item.orderStatus
        tv.apply {
            when {
                -2 == orderSource -> {
                    text = item.orderTypeName ?: orderTypes[item.orderType]
                    setTextColor(ContextCompat.getColor(context, R.color.color_74889D))
                    visibility = View.VISIBLE
                }

                "WAIT_PAY" == orderStatus -> {
                    //可支付结束时间=服务器当前时间+可支付剩余时间
//                    val payEndTime=(nowTime?:System.currentTimeMillis())+(item.waitPayCountDown?:0)*1000
                    text = simpleDateFormat.format((item.payTimeDeadline ?: "0").toLong())
                    setTextColor(ContextCompat.getColor(context, R.color.color_1700f4))
                    visibility = View.VISIBLE
                }

                else -> text = ""
            }
        }

    }

    /**
     * 确认收货
     * */
    private fun confirmGoods(position: Int, item: OrderItemBean) {
        item.apply {
            WBuriedUtil.clickShopOrderTakeDelivery(orderNo, spuName, rmb ?: fb)
        }
        control.confirmGoods(item, object : OnPerformListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.apply {
                    if (2 == orderSource) {
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
    private fun cancelOrder(position: Int, item: OrderItemBean) {
        item.apply {
            WBuriedUtil.clickShopOrderCancel(orderNo, spuName, rmb ?: fb)
        }
        control.cancelOrder(item, object : OnPerformListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish(code: Int) {
                this@OrderAdapter.apply {
                    if (0 == orderSource) {
                        data.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    notifyDataSetChanged()
                }

            }
        })
    }

    @Composable
    private fun ItemCompose(item: OrderItemBean) {
        item.apply {
            var fbPrice = fbRefund ?: fbRefundApply ?: "0"
            var rmbPrice = rmbRefund ?: rmbRefundApply ?: "0"
            if (rmbPrice.isNullOrEmpty()) {
                rmbPrice = "0"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "退款金额：", fontSize = 14.sp, color = colorResource(R.color.color_16))
                val addStr = if (fbPrice != "0" && rmbPrice.toFloat() > 0f) "+" else ""
                if (fbPrice == "0" && rmbPrice.toFloat() > 0f) fbPrice = ""
                if (fbPrice != "") {
                    Image(
                        painter = painterResource(R.mipmap.ic_shop_fb_42),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Text(
                    text = "$fbPrice$addStr${if (rmbPrice != "0") "￥$rmbPrice" else ""}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.color_16)
                )
            }
        }
    }
}