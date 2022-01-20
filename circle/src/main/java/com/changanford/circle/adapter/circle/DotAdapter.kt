package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleDotBinding


class DotAdapter: BaseQuickAdapter<Int, BaseDataBindingHolder<ItemCircleDotBinding>>(R.layout.item_circle_dot){
    private var pos:Int=0
    private var lastRadioButton: RadioButton?=null
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleDotBinding>, item: Int) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
            if(pos==position){
                updateSelected(radioButton,position)
            }else radioButton.isChecked=false
        }
    }
    private fun updateSelected(radioButton:RadioButton,position:Int){
        if(lastRadioButton!=radioButton){
            radioButton.isChecked=true
            lastRadioButton?.isChecked=false
            lastRadioButton=radioButton
        }
        this.pos=position
    }
    @SuppressLint("NotifyDataSetChanged")
    fun selectPosition(position:Int){
        this.pos=position
        this.notifyDataSetChanged()
    }
}