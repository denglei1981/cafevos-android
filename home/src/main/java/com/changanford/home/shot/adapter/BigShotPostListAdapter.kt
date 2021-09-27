package com.changanford.home.shot.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.home.R
import com.changanford.home.databinding.ItemBigShotItemsBinding
import com.changanford.home.databinding.ItemBigShotStateBinding
import com.changanford.home.news.data.SpecialData

class BigShotPostListAdapter  : BaseQuickAdapter<SpecialData,BaseDataBindingHolder<ItemBigShotItemsBinding>>(R.layout.item_big_shot_items){


    override fun convert(
        holder: BaseDataBindingHolder<ItemBigShotItemsBinding>,
        item: SpecialData
    ) {

    }
}