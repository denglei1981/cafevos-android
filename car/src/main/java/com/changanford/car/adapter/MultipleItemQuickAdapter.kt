package com.changanford.car.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.databinding.ItemCarIconBinding
import com.changanford.common.bean.NewCarTagBean


/**
 * @Author : wenke
 * @Time : 2022/3/7 0007
 * @Description : MultipleItemQuickAdapter
 */
class MultipleItemQuickAdapter:BaseMultiItemQuickAdapter<NewCarTagBean, BaseDataBindingHolder<ItemCarIconBinding>>() {
//    init {
//        addItemType(NewCarTagBean, R.layout.item_text_view)
//        addItemType(QuickMultipleEntity.IMG, R.layout.item_image_view)
//        addItemType(QuickMultipleEntity.IMG_TEXT, R.layout.item_img_text_view)
//    }
    override fun convert(holder: BaseDataBindingHolder<ItemCarIconBinding>, item: NewCarTagBean) {
        holder.itemViewType
    }
}