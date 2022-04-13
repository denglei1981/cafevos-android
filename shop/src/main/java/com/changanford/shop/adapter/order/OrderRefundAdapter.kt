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
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding


class OrderRefundAdapter : BaseQuickAdapter<OrderRefundItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    private val dp4 by lazy { ScreenUtils.dp2px(context,4f) }
    private val control by lazy { OrderControl(context,null) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderRefundItemBean) {
        holder.dataBinding?.apply{
            tvOrderNo.text="退款编号：${item.orderNo}"
            ScreenUtils.setMargin(tvOrderNo,l=dp4)
            tvTag.visibility= View.VISIBLE
            tvTotal.visibility=View.GONE
            tvTotalPrice.visibility=View.GONE
            tvOrderStates.text=item.getRefundStatusTxt()
            control.bindingGoodsInfo(inGoodsInfo,item)
            composeView.visibility=View.VISIBLE
            composeView.setContent {
                ItemCompose(item)
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
                Text(text = "$fbPrice$addStr${if(rmbPrice!="0")rmbPrice else ""}", fontSize = 14.sp, color = colorResource(R.color.color_00095B))
            }
        }
    }
}