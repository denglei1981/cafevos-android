package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ItemGoodsKillAreaBinding


class GoodsKillAreaAdapter: BaseQuickAdapter<GoodsBean, BaseDataBindingHolder<ItemGoodsKillAreaBinding>>(R.layout.item_goods_kill_area), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsKillAreaBinding>, item: GoodsBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
}