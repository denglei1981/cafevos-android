package com.changanford.home.search.adapter

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.room.SearchRecordEntity
import com.changanford.home.R
import com.changanford.home.databinding.ItemCommonSearchTagBinding

class SearchHistoryAdapter :
    BaseQuickAdapter<SearchRecordEntity, BaseDataBindingHolder<ItemCommonSearchTagBinding>>(R.layout.item_common_search_tag) {

    var isExpand = MutableLiveData(false)
    var isShowEnd = false

    override fun convert(
        holder: BaseDataBindingHolder<ItemCommonSearchTagBinding>,
        item: SearchRecordEntity
    ) {
        holder.dataBinding?.let {
            it.tvSearchTag.text = item.keyword
        }
    }

    override fun getItemCount(): Int {
        if (data.size > 20) return 20
        return super.getItemCount()
    }
}
