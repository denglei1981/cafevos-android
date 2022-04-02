package com.changanford.shop.ui.shoppingcart.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemAttributeBinding


/**
 *  商品标签
 * */
class GoodsAttributeAdapter : BaseQuickAdapter<String, BaseDataBindingHolder<ItemAttributeBinding>>(R.layout.item_attribute), LoadMoreModule {
//    private val maxWidth by lazy { ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,160f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemAttributeBinding>, item: String) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
//            dataBinding.tvAttribute.maxWidth=this.maxWidth
        }
    }
}