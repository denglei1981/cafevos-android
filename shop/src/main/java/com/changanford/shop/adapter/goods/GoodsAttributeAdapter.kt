package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.os.Build
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
    private var isUpdate=false
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
//        Log.e("okhttp","$pos>>>currentSkuCode:$currentSkuCode")
        skuVos?.apply {
            for (item in this){
                //skuVos是否包含 optionId可选属性
                if(item.skuCode.split("-")[pos]==optionId){
                    if(isUpdate){
                        //查询 optionId 的组合可能性是否也在 skuVos中
                        val skuCode=getTemporarySkuCode(optionId)
                        skuVos?.find { skuCode==it.skuCode }?.let { return true }
                        break
                    }
                    return true
                }
            }
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
    fun updateAdapter(skuCode: String){
        currentSkuCode=skuCode
        isUpdate=isLegalSkuCode(currentSkuCode)<2
        notifyDataSetChanged()
    }
    private fun isLegalSkuCode(_skuCode:String?):Int{
        return _skuCode?.split("-")?.filter { "0"==it }?.size?:0
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