package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Attribute
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeIndexBinding


class GoodsAttributeIndexAdapter: BaseQuickAdapter<Attribute, BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>>(R.layout.item_goods_attribute_index){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>, item: Attribute) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            val mAdapter=GoodsAttributeAdapter(0)
            dataBinding.recyclerView.adapter=mAdapter
            mAdapter.setList(item.optionVos)
        }
    }
}