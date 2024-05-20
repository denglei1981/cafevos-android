package com.changanford.shop.ui.sale.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.util.TimeUtils
import com.changanford.shop.R
import com.changanford.shop.bean.RefundStautsBean
import com.changanford.shop.databinding.ItemRefundLineProgressBinding

/**
 *    退款进度线。
 * */
class RefundProgressAdapter(val baseViewModel: BaseViewModel) :
    BaseQuickAdapter<RefundStautsBean, BaseDataBindingHolder<ItemRefundLineProgressBinding>>(R.layout.item_refund_line_progress) {

    var refundStatus = ""
    override fun convert(
        holder: BaseDataBindingHolder<ItemRefundLineProgressBinding>,
        item: RefundStautsBean
    ) {

        holder.dataBinding?.apply {
            tvTime.text = TimeUtils.MillisToStr(item.operationTime)
            tvShoperTips.text = item.operationDesc
            baseViewModel.StatusEnum(
                "MallRefundDetailStatusEnum",
                item.refundDetailStatus,
                tvShoperStates
            )
            setBotStatus(item.refundStauts,tvRefundStates)
//            baseViewModel.StatusEnum("MallRefundStatusEnum", item.refundStauts, tvRefundStates)
            if (holder.layoutPosition == 1) {
                ivRight.visibility = View.VISIBLE
                tvRefundStates.visibility=View.VISIBLE
                tvRefundStates.setTextColor(ContextCompat.getColor(context, R.color.color_1700f4))
                tvShoperStates.setTextColor(ContextCompat.getColor(context, R.color.color_d916))
                tvShoperTips.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                ivRight.setBackgroundResource(R.drawable.bg_oval_app_color)
                vLineTop.setBackgroundColor(ContextCompat.getColor(context,R.color.color_1700F4))
            } else {
                val index = holder.layoutPosition
                val lastIndex = index - 1
                if (lastIndex >= 0) {
                    val item1 = getItem(lastIndex-1)
                    if (item1.refundStauts == getItem(lastIndex).refundStauts) {
                        ivRight.visibility = View.GONE
                        tvRefundStates.visibility=View.GONE
                    } else {
                        ivRight.visibility = View.VISIBLE
                        tvRefundStates.visibility=View.VISIBLE
                    }
                }

                tvRefundStates.setTextColor(ContextCompat.getColor(context, R.color.color_9916))
                tvShoperStates.setTextColor(ContextCompat.getColor(context, R.color.color_9916))
                tvShoperTips.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                ivRight.setBackgroundResource(R.drawable.bg_oval_4d_app_color)
                vLineTop.setBackgroundColor(ContextCompat.getColor(context,R.color.color_26000000))
            }
        }

    }

    private fun setBotStatus(state: String,tv: TextView){
        when (state) {
            "ON_GOING" -> {
                tv.text = "退款中"
            }

            "FINISH" -> {
                tv.text = "退款成功"
            }

            "CLOSED" -> {
                tv.text = "退款关闭"
            }
        }
    }
}

