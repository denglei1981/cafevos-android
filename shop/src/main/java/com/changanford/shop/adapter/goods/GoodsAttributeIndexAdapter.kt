package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Attribute
import com.changanford.common.bean.OptionVo
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeIndexBinding


class GoodsAttributeIndexAdapter(var skuCode:String): BaseQuickAdapter<Attribute, BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>>(R.layout.item_goods_attribute_index){
   private var skuCodes:ArrayList<String> = skuCode.split("-") as ArrayList<String>//"108-1-31-43"
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>, item: Attribute) {
        val dataBinding=holder.dataBinding
        val position=holder.absoluteAdapterPosition
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            val mAdapter=GoodsAttributeAdapter(position+1,skuCodes[position+1],object :GoodsAttributeAdapter.OnSelectedBackListener{
                override fun onSelectedBackListener(pos: Int, item: OptionVo) {
                    item.optionId.also { skuCodes[pos] = it }
                    updateSkuCode()
                }
            })
            dataBinding.recyclerView.adapter=mAdapter
            mAdapter.setList(item.optionVos)
        }
    }
    private fun updateSkuCode(){
        skuCode=""
        skuCodes.forEach{
            skuCode+="$it-"
        }
    }
}