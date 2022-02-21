package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleSelecttypeBinding
import com.changanford.common.bean.NewCirceTagBean


class CircleSelectTypeAdapter: BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCircleSelecttypeBinding>>(
    R.layout.item_circle_selecttype){
    private var selectItemBean:NewCirceTagBean?=null
    private var lastRadioButton: RadioButton?=null
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleSelecttypeBinding>, item: NewCirceTagBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
            if(item.isCheck==true){
                lastRadioButton=radioButton
                selectItemBean=item
            }
            radioButton.setOnClickListener {
                selectItemBean=item
                if(lastRadioButton!=radioButton)lastRadioButton?.isChecked=false
                radioButton.isChecked=true
                lastRadioButton=radioButton
            }
        }
    }
    fun getSelectItemBean():NewCirceTagBean?{
        return selectItemBean
    }
}