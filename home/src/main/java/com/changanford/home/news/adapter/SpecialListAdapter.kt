package com.changanford.home.news.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.databinding.ItemHomeSpecialBinding
import com.changanford.home.news.data.SpecialData

class SpecialListAdapter  : BaseQuickAdapter<SpecialData,BaseDataBindingHolder<ItemHomeSpecialBinding>>(R.layout.item_home_special){
    override fun convert(holder: BaseDataBindingHolder<ItemHomeSpecialBinding>, item: SpecialData) {

    }
}