package com.changanford.home.search.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AskListMainData
import com.changanford.home.R
import com.changanford.home.databinding.ItemSearchResultAskBinding

class SearchAskResultAdapter(val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<AskListMainData, BaseDataBindingHolder<ItemSearchResultAskBinding>>(
        R.layout.item_search_result_user) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchResultAskBinding>,
        item: AskListMainData
    ) {
        //     val headFrameName:String="", 这里取 这些
        //    val headFrameImage:String=""
        holder.dataBinding?.let { it ->


        }

    }



}