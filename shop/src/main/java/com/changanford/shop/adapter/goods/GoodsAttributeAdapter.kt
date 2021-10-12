package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OptionVo
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeBinding


class GoodsAttributeAdapter(val pos:Int,var optionId:String,val listener:OnSelectedBackListener): BaseQuickAdapter<OptionVo, BaseDataBindingHolder<ItemGoodsAttributeBinding>>(R.layout.item_goods_attribute), LoadMoreModule {
    private lateinit var lastRb:RadioButton
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeBinding>, item: OptionVo) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            dataBinding.radioButton.isChecked= if(optionId==item.optionId){
                lastRb=dataBinding.radioButton
                true
            }else false
            dataBinding.radioButton.setOnClickListener {
                selectRb(dataBinding.radioButton)
                optionId=item.optionId
                listener.onSelectedBackListener(pos,item)
            }
        }
    }
    private fun selectRb(rb:RadioButton){
        if(::lastRb.isInitialized)lastRb.isChecked=false
        rb.isChecked=true
        lastRb=rb
    }
    interface OnSelectedBackListener {
        fun onSelectedBackListener(pos: Int,item: OptionVo)
    }
}