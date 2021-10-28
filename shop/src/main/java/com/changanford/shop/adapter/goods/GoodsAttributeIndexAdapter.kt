package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Attribute
import com.changanford.common.bean.OptionVo
import com.changanford.common.bean.SkuVo
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeIndexBinding


class GoodsAttributeIndexAdapter(private val skuCodeLiveData: MutableLiveData<String>,var skuVos:List<SkuVo>?=null): BaseQuickAdapter<Attribute, BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>>(R.layout.item_goods_attribute_index){
    private lateinit var skuCodes:ArrayList<String>//"108-1-31-43"[108,1,31,43]
    private var currentSkuCode=""//当前skuCode
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>, item: Attribute) {
        val dataBinding=holder.dataBinding
        val position=holder.absoluteAdapterPosition
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
            if(::skuCodes.isInitialized){
                val pos=position+1
                val mAdapter=GoodsAttributeAdapter(pos,skuCodes[pos],skuVos,currentSkuCode,object :GoodsAttributeAdapter.OnSelectedBackListener{
                    override fun onSelectedBackListener(pos: Int, item: OptionVo) {
                        item.optionId.also { skuCodes[pos] = it }
                        updateSkuCode()
                    }
                })
                dataBinding.recyclerView.adapter=mAdapter
                mAdapter.setList(item.optionVos)
            }
        }
    }
    fun setSkuCodes(skuCode:String?){
        currentSkuCode=skuCode?:""
        if(null!=skuCode&&skuCode.contains("-"))skuCodes= skuCode.split("-") as ArrayList<String>
    }
    fun getSkuCodes():ArrayList<String>{
        return skuCodes
    }
    fun updateSkuCode(){
        currentSkuCode=""
        skuCodes.forEach{ currentSkuCode+="$it-" }
        currentSkuCode=currentSkuCode.substring(0,currentSkuCode.length-1)
        skuCodeLiveData.postValue(currentSkuCode)
        notifyDataSetChanged()
    }
}