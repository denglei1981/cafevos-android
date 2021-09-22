package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemKillDateBinding


class GoodsKillDateAdapter(var selectPos:Int): BaseQuickAdapter<String, BaseDataBindingHolder<ItemKillDateBinding>>(R.layout.item_kill_date), LoadMoreModule {
    private lateinit var lastBinding:ItemKillDateBinding
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemKillDateBinding>, item: String) {
        val dataBinding=holder.dataBinding
        val position=holder.absoluteAdapterPosition
        if(dataBinding!=null){
//            dataBinding.model=item
////            dataBinding.executePendingBindings()
            dataBinding.radioButton.text=item
            val isChecked=if(selectPos==position){
                lastBinding=dataBinding
                true
            }else false
            dataBinding.radioButton.isChecked= isChecked
            dataBinding.rbDot.isChecked=isChecked
            dataBinding.radioButton.setOnClickListener {
                selectRb(dataBinding)
                selectPos=position
            }
        }
    }
    private fun selectRb(dataBinding:ItemKillDateBinding){
        if(::lastBinding.isInitialized){
            lastBinding.radioButton.isChecked=false
            lastBinding.rbDot.isChecked=false
        }
        dataBinding.radioButton.isChecked=true
        dataBinding.rbDot.isChecked=true
        lastBinding=dataBinding
    }
}