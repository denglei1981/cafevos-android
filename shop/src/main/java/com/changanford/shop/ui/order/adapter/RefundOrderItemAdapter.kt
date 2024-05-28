package com.changanford.shop.ui.order.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.RefundOrderItemBean
import com.changanford.common.util.CustomImageSpanV2
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil.getHeatNum
import com.changanford.shop.R
import com.changanford.shop.databinding.InItemOrderGoodsV2Binding
import com.changanford.shop.ui.shoppingcart.adapter.GoodsAttributeAdapter
import com.changanford.shop.utils.WCommonUtil
import com.google.gson.Gson
import java.math.BigDecimal

/**
 *
 * */
class RefundOrderItemAdapter() :
    BaseQuickAdapter<RefundOrderItemBean, BaseDataBindingHolder<InItemOrderGoodsV2Binding>>(R.layout.in_item_order_goods_v2) {

    var orderStatus: String = ""
    var refundStatus: String = ""
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<InItemOrderGoodsV2Binding>,
        item: RefundOrderItemBean
    ) {
        holder.dataBinding?.apply {
            GlideUtils.loadBD(item.skuImg, imgGoodsCover)
            vLine.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
//            tvIntegral.text = "实付价￥${item.getMPayPrice().toFloat()/item.buyNum!!.toInt()}"
//            tvOldPrice.text = "原价￥${item.getRMB2(item.orginPrice).toFloat()/item.buyNum!!.toInt()}"
            tvIntegral.text = "实付价￥${
                MUtils.divideAndRetainTwoDecimalPlaces(
                    item.getMPayPrice().toDouble(),
                    item.buyNum!!.toDouble()
                )
            }"
            tvOldPrice.text = "原价￥${
                MUtils.divideAndRetainTwoDecimalPlaces(
                    item.getRMB2(item.orginPrice).toDouble(), item.buyNum!!.toDouble()
                )
            }"
//            showTotalTag(tvIntegral, item)
            val goodsAttributeAdapter = GoodsAttributeAdapter()
            recyclerView.adapter = goodsAttributeAdapter
            goodsAttributeAdapter.setList(item.getTagList())
            tvGoodsTitle.text = item.spuName
            tvGoodsNumber.text = "×".plus(item.buyNum)
            tvSaleHandler.visibility = View.GONE

            tvSaleHandler.setOnClickListener {// 退货申请 单个商品
                val gson = Gson()
                val toJson = gson.toJson(item)
                JumpUtils.instans?.jump(125, toJson)
            }
        }
    }

    fun getShoppingList() {

    }

    fun showTotalTag(text: AppCompatTextView?, item: RefundOrderItemBean) {
        if (TextUtils.isEmpty(item.price)) {
            showZero(text, item)
            return
        }
        val fbNumber = item.price

//        val str = "${WCommonUtil.getRMBBigDecimal("${(item.price?.toInt()?:0)/(item.buyNum?.toInt()?:1)}")}([icon] ${(item.price?.toInt()?:0)/(item.buyNum?.toInt()?:1)})"
        val str = "${
            WCommonUtil.getRMBBigDecimal(
                "${
                    getHeatNum(
                        item.price,
                        2
                    ).divide(BigDecimal(item.buyNum))
                }"
            )
        }([icon] ${getHeatNum(item.price).divide(BigDecimal(item.buyNum))})"
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

    fun showZero(text: AppCompatTextView?, item: RefundOrderItemBean) {
        val tagName = item.price

        //先设置原始文本
        text?.text = "合计".plus("  ￥${tagName}")


    }

    interface ShopBackListener {

        fun check()
    }
}

