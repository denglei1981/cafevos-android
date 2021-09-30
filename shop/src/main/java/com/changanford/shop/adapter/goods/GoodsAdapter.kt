package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsItemBean
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsBinding


class GoodsAdapter: BaseQuickAdapter<GoodsItemBean, BaseDataBindingHolder<ItemGoodsBinding>>(R.layout.item_goods){
    init {
        this.setEmptyView(R.layout.view_empty)
    }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsBinding>, item: GoodsItemBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
}