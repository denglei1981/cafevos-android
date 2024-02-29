package com.changanford.common.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.databinding.ItemFordPaiBottomDialogBinding

/**
 * @author: niubobo
 * @date: 2024/2/26
 * @description：
 */
class FordPaiBottomDialogAdapter() :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemFordPaiBottomDialogBinding>>(
        R.layout.item_ford_pai_bottom_dialog
    ) {

    var defaultColorIndex = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFordPaiBottomDialogBinding>,
        item: String
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item
            if (defaultColorIndex == holder.layoutPosition) {
                tvContent.setTextColor(
                    ContextCompat.getColor(
                        tvContent.context,
                        R.color.color_1700f4
                    )
                )
            } else {
                tvContent.setTextColor(
                    ContextCompat.getColor(
                        tvContent.context,
                        R.color.color_d916
                    )
                )
            }
        }

    }
}