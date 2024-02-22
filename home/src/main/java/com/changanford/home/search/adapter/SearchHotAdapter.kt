package com.changanford.home.search.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.SearchKeyBean
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.setDrawableNull
import com.changanford.home.R
import com.changanford.home.databinding.ItemCommonSearchHotBinding

class SearchHotAdapter :
    BaseQuickAdapter<SearchKeyBean, BaseDataBindingHolder<ItemCommonSearchHotBinding>>(R.layout.item_common_search_hot) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemCommonSearchHotBinding>,
        item: SearchKeyBean
    ) {
        holder.dataBinding?.let {
            if (item.hotTag == 1) {
                it.label.setTextColor(ContextCompat.getColor(context, R.color.color_E67400))
                it.label.setDrawableLeft(R.mipmap.ic_search_hot_tips)
            } else {
                it.label.setTextColor(ContextCompat.getColor(context, R.color.color_d916))
                it.label.setDrawableNull()
            }
            it.label.text = item.keyword
        }

    }
}
