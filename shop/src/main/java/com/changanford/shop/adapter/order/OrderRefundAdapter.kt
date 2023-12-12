package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.view.View
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
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderRefundItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding


class OrderRefundAdapter : BaseQuickAdapter<OrderRefundItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    private val dp4 by lazy { ScreenUtils.dp2px(context,4f) }
    private val control by lazy { OrderControl(context,null) }
    private val btnWidth by lazy { (ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,55f))/4 }
    private val dp30 by lazy { ScreenUtils.dp2px(context,30f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderRefundItemBean) {
        holder.dataBinding?.apply{
            initBtn(this)
            tvOrderNo.text="退款编号：${item.refundNo}"
            ScreenUtils.setMargin(tvOrderNo,l=dp4)
            if (item.refundMethod == "ONLY_COST"){
                tvTag.text = "退款"
            } else if (item.refundMethod == "CONTAIN_GOODS"){
                tvTag.text = "退货"
            }
            tvTag.visibility= View.VISIBLE
            tvTotal.visibility=View.GONE
            tvTotalPrice.visibility=View.GONE
            tvOrderStates.text=item.getRefundStatusTxt()
            control.bindingGoodsInfo(inGoodsInfo,item)
            btnConfirm.visibility=View.GONE
            btnCancel.apply {//售后详情
                visibility=View.VISIBLE
                setText(R.string.str_afterDetails)
                setOnClickListener {
                    item.apply {
                        //整单退
                        if(refundType=="ALL_ORDER") JumpUtils.instans?.jump(124, mallMallRefundId)
                        //单SKU退
                        else JumpUtils.instans?.jump(126, mallMallRefundId)

                    }
                }
            }
            composeView.visibility=View.VISIBLE
            composeView.setContent {
                ItemCompose(item)
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
    @Composable
    private fun ItemCompose(item: OrderRefundItemBean){
        item.apply {
            var fbPrice=fbRefund?:fbRefundApply?:"0"
            val rmbPrice=rmbRefund?:rmbRefundApply?:"0"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "退款金额：", fontSize = 14.sp, color = colorResource(R.color.color_33))
                val addStr=if(fbPrice!="0"&&rmbPrice.toFloat()>0f)"+" else ""
                if(fbPrice=="0"&&rmbPrice.toFloat()>0f)fbPrice=""
                if(fbPrice!=""){
                    Image(painter = painterResource(R.mipmap.ic_shop_fb_42), contentDescription = null)
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(text = "$fbPrice$addStr${if(rmbPrice!="0")"￥$rmbPrice" else ""}", fontSize = 14.sp, color = colorResource(R.color.color_1700f4))
            }
        }
    }
}