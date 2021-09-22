package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ItemKillTimeBinding
import com.changanford.shop.utils.ScreenUtils


class GoodsKillAreaTimeAdapter(var selectPos:Int): BaseQuickAdapter<GoodsBean, BaseDataBindingHolder<ItemKillTimeBinding>>(R.layout.item_kill_time), LoadMoreModule {
    private lateinit var lastBinding:ItemKillTimeBinding
    private val dp10 by lazy { ScreenUtils.dp2px(context,10f) }
    private val dp20 by lazy { ScreenUtils.dp2px(context,20f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillTimeBinding>, item: GoodsBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val position=holder.absoluteAdapterPosition
            when (position) {
                0 -> ScreenUtils.setMargin(holder.itemView,dp20,0,dp10,0)
                data.size-1 -> ScreenUtils.setMargin(holder.itemView,0,0,dp20,0)
                else -> ScreenUtils.setMargin(holder.itemView,0,0,dp10,0)
            }
//            dataBinding.model=item
//            dataBinding.executePendingBindings()
            if(selectPos==position){
                dataBinding.layoutRoot.setBackgroundResource(R.drawable.shadow_e8ebf3_5dp)
                dataBinding.tvTime.setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
                dataBinding.tvState.setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
                lastBinding=dataBinding
            } else{
                dataBinding.layoutRoot.setBackgroundResource(R.drawable.shadow_f9_5dp)
                dataBinding.tvTime.setTextColor(ContextCompat.getColor(context,R.color.color_99))
                dataBinding.tvState.setTextColor(ContextCompat.getColor(context,R.color.color_99))
            }
            dataBinding.root.setOnClickListener { switchItem(dataBinding) }
        }

    }
    private fun switchItem(itemBinding:ItemKillTimeBinding){
        if(::lastBinding.isInitialized&&itemBinding!=lastBinding){
            itemBinding.layoutRoot.setBackgroundResource(R.drawable.shadow_e8ebf3_5dp)
            itemBinding.tvTime.setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
            itemBinding.tvState.setTextColor(ContextCompat.getColor(context,R.color.color_00095B))

            lastBinding.layoutRoot.setBackgroundResource(R.drawable.shadow_f9_5dp)
            lastBinding.tvTime.setTextColor(ContextCompat.getColor(context,R.color.color_99))
            lastBinding.tvState.setTextColor(ContextCompat.getColor(context,R.color.color_99))
            lastBinding=itemBinding
        }
    }
}