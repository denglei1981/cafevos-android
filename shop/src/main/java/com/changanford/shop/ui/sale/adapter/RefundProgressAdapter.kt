package com.changanford.shop.ui.sale.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.util.TimeUtils
import com.changanford.shop.R
import com.changanford.shop.bean.RefundProgressBean
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
            baseViewModel.StatusEnum("MallRefundStatusEnum", item.refundStauts, tvRefundStates)
            if (holder.layoutPosition == 1) {
                tvRefundStates.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvShoperStates.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvShoperTips.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.black))
                ivRight.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.shopping_cart_checked
                    )
                )
            } else {
                val index = holder.layoutPosition
                val lastIndex = index - 1
                if (lastIndex >= 0) {
                    val item1 = getItem(lastIndex-1)
                    if (item1.refundStauts == getItem(lastIndex).refundStauts) {
                        ivRight.visibility = View.INVISIBLE
                    } else {
                        ivRight.visibility = View.VISIBLE
                    }
                }

                tvRefundStates.setTextColor(ContextCompat.getColor(context, R.color.gray_999999))
                tvShoperStates.setTextColor(ContextCompat.getColor(context, R.color.gray_999999))
                tvShoperTips.setTextColor(ContextCompat.getColor(context, R.color.gray_999999))
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.gray_999999))
                ivRight.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.icon_shopping_line_uncheck
                    )
                )
            }
        }

    }

}

