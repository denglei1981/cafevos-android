package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.InfoDataBean
import com.changanford.home.R

class RecommendAdapter : BaseMultiItemQuickAdapter<InfoDataBean, BaseViewHolder>() {

    init {
        addItemType(1, R.layout.item_home_recommend_items_one)
        addItemType(2, R.layout.item_home_recommend_items_three)
    }

    override fun convert(holder: BaseViewHolder, item: InfoDataBean) {


    }
}
