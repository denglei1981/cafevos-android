package com.changanford.shop.ui.order.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.CustomImageSpanV2
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.InItemOrderGoodsV2Binding
import com.changanford.shop.ui.shoppingcart.adapter.GoodsAttributeAdapter
import com.changanford.shop.utils.WCommonUtil
import com.google.gson.Gson

/**
 *
 * */
class OrderDetailsItemV2Adapter() :
    BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<InItemOrderGoodsV2Binding>>(R.layout.in_item_order_goods_v2) {

    var orderStatus: String = ""
    var refundStatus: String = ""
    var orderNo: String = ""
    override fun convert(
        holder: BaseDataBindingHolder<InItemOrderGoodsV2Binding>,
        item: OrderItemBean
    ) {
        holder.dataBinding?.apply {
            model = item
            GlideUtils.loadBD(item.skuImg, imgGoodsCover)
            showTotalTag(tvIntegral, item)
            val goodsAttributeAdapter = GoodsAttributeAdapter()
            goodsAttributeAdapter.setList(item.getTagList())
            recyclerView.adapter = goodsAttributeAdapter

            when (orderStatus) {
                "WAIT_SEND", "WAIT_PAY" -> {
                    tvSaleHandler.visibility = View.VISIBLE
                }
                "REFUNDING" -> {
                    tvSaleHandler.visibility = View.VISIBLE
                    tvSaleHandler.text = "退款中"

                }
                else -> {
                    tvSaleHandler.visibility = View.VISIBLE
                    tvSaleHandler.text = "申请售后"
                }
            }
            if (item.mallRefundStatus != null && !TextUtils.isEmpty(item.mallRefundStatus)) {
                // 单个sku 退款状态
                when (item.mallRefundStatus) {
                    "ON_GOING" -> {
                        tvSaleHandler.visibility = View.VISIBLE
                        tvSaleHandler.text = "退款中"
                    }
                    "FINISH" -> {
                        tvSaleHandler.visibility = View.VISIBLE
                        tvSaleHandler.text = "退款成功"
                    }
                    "CLOSED" -> {
                        tvSaleHandler.visibility = View.VISIBLE
                        tvSaleHandler.text = "退款关闭"
                    }
                }
            }

            tvSaleHandler.setOnClickListener {// 退货申请 单个商品
                val itemStatus = tvSaleHandler.text.toString()
                when (itemStatus) {
                    "退款中","退款成功" -> {// 查询退款进度
                        JumpUtils.instans?.jump(127, item.mallOrderSkuId)
                    }
                    else -> {
                        item.orderNo = orderNo

                        val gson = Gson()
                        val toJson = gson.toJson(item)
                        JumpUtils.instans?.jump(121, toJson)
                    }
                }

            }
        }
    }

    fun getShoppingList() {

    }

    fun showTotalTag(text: AppCompatTextView?, item: OrderItemBean) {
        if (TextUtils.isEmpty(item.price)) {
            showZero(text, item)
            return
        }
        val fbNumber = item.price

        val str = "${WCommonUtil.getRMBBigDecimal(item.price)}([icon] ${item.price})"
        //先设置原始文本
        text?.text = str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text?.post { //获取第一行的宽度
            val stringBuilder: StringBuilder = StringBuilder(str)
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(context, R.mipmap.question_fb)
            drawable?.apply {
                val imageSpan = CustomImageSpanV2(this)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val strLength = spannableString.length
                val numberLength = fbNumber?.length
                val startIndex = strLength - numberLength!! - 1
//                spannableString.setSpan(
//                    AbsoluteSizeSpan(30),
//                    startIndex,
//                    strLength,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                spannableString.setSpan(
//                    ForegroundColorSpan(Color.parseColor("#E1A743")), startIndex, strLength,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                spannableString.setSpan(
                    imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text.text = spannableString
            }
        }
    }

    fun showZero(text: AppCompatTextView?, item: OrderItemBean) {
        val tagName = item.price

        //先设置原始文本
        text?.text = "合计".plus("  ￥${tagName}")


    }

    interface ShopBackListener {

        fun check()
    }
}

