package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.shop.R
import com.changanford.shop.databinding.InComposeBinding
import com.changanford.shop.ui.compose.RecommendItemCompose

class ShopRecommendListAdapter: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<InComposeBinding>>(
    R.layout.in_compose){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<InComposeBinding>, item: GoodsItemBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
            composeView.setContent {
                RecommendItemCompose(position,item)
            }
        }
    }
}