package com.changanford.shop.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemIntegralDetailBinding


class IntegralDetailsAdapter: BaseQuickAdapter<String, BaseDataBindingHolder<ItemIntegralDetailBinding>>(R.layout.item_integral_detail), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemIntegralDetailBinding>, item: String) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
//            dataBinding.model=item
//            dataBinding.executePendingBindings()
        }
    }
}