package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ItemKillGoodsBinding
import com.changanford.shop.utils.ScreenUtils


class GoodsKillAdatpter: BaseQuickAdapter<GoodsBean, BaseDataBindingHolder<ItemKillGoodsBinding>>(R.layout.item_kill_goods), LoadMoreModule {
    val dp11 by lazy { ScreenUtils.dp2px(context,11f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillGoodsBinding>, item: GoodsBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val position=holder.absoluteAdapterPosition
            dataBinding.model=item
            dataBinding.executePendingBindings()
            val layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0,0,0,20)
            when (position) {
                0 -> ScreenUtils.setMargin(holder.itemView,dp11,0,0,0)
                data.size-1 -> ScreenUtils.setMargin(holder.itemView,0,0,dp11,0)
                else -> ScreenUtils.setMargin(holder.itemView,0,0,0,0)
            }
        }
    }
}