package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemAttributeBinding


class OrderGoodsAttributeAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemAttributeBinding>>(R.layout.item_attribute),
    LoadMoreModule {
    //    private val maxWidth by lazy { ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,160f) }
    var noStock = false

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemAttributeBinding>, item: String) {
        val dataBinding = holder.dataBinding
        dataBinding?.let {
            if (noStock) {
                dataBinding.tvAttribute.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_99
                    )
                )
                dataBinding.tvAttribute.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shadow_f5_11dp
                    )
                )
            } else {
                dataBinding.tvAttribute.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_74889D
                    )
                )
                dataBinding.tvAttribute.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shadow_f2f4f9_11dp
                    )
                )
            }
        }

        if (dataBinding != null) {
            dataBinding.model = item
            dataBinding.executePendingBindings()
//            dataBinding.tvAttribute.maxWidth=this.maxWidth
        }
    }
}