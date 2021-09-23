package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.home.R
import com.changanford.home.data.RecommendData
import com.changanford.home.databinding.ItemHomeRecommendItemsOneBinding

class RecommendAdapter: BaseQuickAdapter<RecommendData,BaseDataBindingHolder<ItemHomeRecommendItemsOneBinding>>(
    R.layout.item_home_recommend_items_one) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendItemsOneBinding>,
        item: RecommendData
    ) {

    }
}