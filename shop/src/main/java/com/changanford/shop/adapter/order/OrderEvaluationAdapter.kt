package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemPostEvaluationBinding


class OrderEvaluationAdapter: BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemPostEvaluationBinding>>(R.layout.item_post_evaluation){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemPostEvaluationBinding>, item: OrderItemBean) {
        holder.dataBinding.apply {

        }
    }
}