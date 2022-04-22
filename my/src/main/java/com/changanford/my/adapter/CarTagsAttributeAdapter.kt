package com.changanford.my.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.my.R
import com.changanford.my.databinding.ItemCarTagAttributeBinding

class CarTagsAttributeAdapter : BaseQuickAdapter<String, BaseDataBindingHolder<ItemCarTagAttributeBinding>>(
    R.layout.item_car_tag_attribute),
    LoadMoreModule {
    //    private val maxWidth by lazy { ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,160f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarTagAttributeBinding>, item: String) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
//            dataBinding.tvAttribute.maxWidth=this.maxWidth
        }
    }
}