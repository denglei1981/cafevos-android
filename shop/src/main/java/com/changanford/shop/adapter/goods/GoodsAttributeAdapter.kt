package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OptionVo
import com.changanford.common.bean.SkuVo
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeBinding


class GoodsAttributeAdapter(private val pos:Int, var selectedOptionId:String, var skuVos:List<SkuVo>?=null, var currentSkuCode:String,val listener:OnSelectedBackListener): BaseQuickAdapter<OptionVo, BaseDataBindingHolder<ItemGoodsAttributeBinding>>(R.layout.item_goods_attribute), LoadMoreModule {
    private lateinit var lastRb:RadioButton
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeBinding>, item: OptionVo) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            val optionId=item.optionId
            dataBinding.radioButton.apply {
                isChecked= if(selectedOptionId==optionId){
                    lastRb=this
                    true
                }else false
                isEnabled=if(isExistSku(optionId)){
                    dataBinding.radioButton.setTextAppearance(R.style.rb_goods0)
                    setOnClickListener {
                        selectRb(dataBinding.radioButton)
                        selectedOptionId=item.optionId
                        listener.onSelectedBackListener(pos,item)
                    }
                    true
                }else {
                    dataBinding.radioButton.setTextAppearance(R.style.rb_goods1)
                    false
                }
            }
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
    /**
     * 是否存在该sku选项
     * */
    private fun isExistSku(optionId:String):Boolean{
        if(TextUtils.isEmpty(currentSkuCode)||isInvalidSelectAttrs(currentSkuCode)){
            skuVos?.apply {
                for (item in this){
                    if(item.skuCode.split("-")[pos]==optionId){
                        return true
                    }
                }
            }
        }else{//根据optionId得到skuCode组合
            val skuCode=getTemporarySkuCode(optionId)
            skuVos?.find { skuCode==it.skuCode }?.let { return true }
        }
        return false
    }
    private fun getTemporarySkuCode(optionId:String):String{
        var skuCode=""
        if(currentSkuCode.contains("-")){
            val skuCodeArr= currentSkuCode.split("-").toMutableList()
            skuCodeArr[pos]=optionId//更新
            skuCodeArr.forEach{ skuCode+="$it-" }
            skuCode=skuCode.substring(0,skuCode.length-1)
        }
        return skuCode
    }
    /**
     * 是否是无效选择商品属性
     * return false 有效 、true 无效
     * */
   private fun isInvalidSelectAttrs(skuCode:String):Boolean{
        return skuCode.contains("-")&&skuCode.split("-").find { it =="0" }!=null
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