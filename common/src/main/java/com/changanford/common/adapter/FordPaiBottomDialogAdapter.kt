package com.changanford.common.adapter

import android.annotation.SuppressLint
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
    @SuppressLint("ClickableViewAccessibility")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFordPaiBottomDialogBinding>,
        item: String
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item
        }

    }
}