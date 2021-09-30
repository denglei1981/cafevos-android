package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.SeckillSession
import com.changanford.common.bean.SeckillTimeRange
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemKillDateBinding
import com.changanford.shop.utils.ScreenUtils
import java.text.SimpleDateFormat


class GoodsKillDateAdapter(var selectPos:Int,val listener:selectBackListener): BaseQuickAdapter<SeckillSession, BaseDataBindingHolder<ItemKillDateBinding>>(R.layout.item_kill_date), LoadMoreModule {
    private lateinit var lastBinding:ItemKillDateBinding
    private val sf = SimpleDateFormat("MM月dd日")
    private val dp9 by lazy { ScreenUtils.dp2px(context,9f) }
    private val dp20 by lazy { ScreenUtils.dp2px(context,20f) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillDateBinding>, item: SeckillSession) {
        val dataBinding=holder.dataBinding
        val position=holder.absoluteAdapterPosition
        if(dataBinding!=null){
            dataBinding.date=sf.format(item.date)
            dataBinding.executePendingBindings()
            when (position) {
                0 -> ScreenUtils.setMargin(dataBinding.radioButton,dp20,0,dp9,0)
                data.size-1 -> ScreenUtils.setMargin(dataBinding.radioButton,dp9,0,dp20,0)
                else -> ScreenUtils.setMargin(dataBinding.radioButton,dp9,0,dp9,0)
            }
            val isChecked=if(selectPos==position){
                lastBinding=dataBinding
                true
            }else false
            dataBinding.radioButton.isChecked= isChecked
            dataBinding.rbDot.isChecked=isChecked
            dataBinding.radioButton.setOnClickListener {selectRb(dataBinding,position,item)}
        }
    }
    private fun selectRb(dataBinding:ItemKillDateBinding,position:Int,item: SeckillSession){
        selectPos=position
        if(::lastBinding.isInitialized&&lastBinding!=dataBinding){
            dataBinding.radioButton.isChecked=true
            dataBinding.rbDot.isChecked=true

            lastBinding.radioButton.isChecked=false
            lastBinding.rbDot.isChecked=false
            lastBinding=dataBinding
            listener.onSelectBackListener(position,item.seckillTimeRanges)
        }
    }
    interface selectBackListener{
        fun onSelectBackListener(position:Int,seckillTimeRanges: ArrayList<SeckillTimeRange>)
    }
}