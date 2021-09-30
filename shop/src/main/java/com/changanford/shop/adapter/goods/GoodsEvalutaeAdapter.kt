package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsEvaluateBinding


class GoodsEvalutaeAdapter: BaseQuickAdapter<String, BaseDataBindingHolder<ItemGoodsEvaluateBinding>>(R.layout.item_goods_evaluate), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsEvaluateBinding>, item: String) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
//            dataBinding.model=item
//            dataBinding.executePendingBindings()
        }
    }
}