package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.data.RecommendData
import com.changanford.home.databinding.ItemHomeRecommendItemsOneBinding

class RecommendAdapter: BaseMultiItemQuickAdapter<RecommendData,BaseViewHolder>() {

    init {
        addItemType(1,R.layout.item_home_recommend_items_one)
        addItemType(2,R.layout.item_home_recommend_items_three)
    }

    override fun convert(holder: BaseViewHolder, item: RecommendData) {


    }
}
