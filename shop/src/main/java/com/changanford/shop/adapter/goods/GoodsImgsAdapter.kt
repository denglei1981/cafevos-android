package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemImgBinding


class GoodsImgsAdapter: BaseQuickAdapter<String, BaseDataBindingHolder<ItemImgBinding>>(R.layout.item_img), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemImgBinding>, item: String) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            //Test
        }
    }
}