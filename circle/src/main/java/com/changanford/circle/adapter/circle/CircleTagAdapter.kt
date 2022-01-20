package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCreateCircleTagBinding
import com.changanford.common.bean.NewCirceTagBean
import com.changanford.common.utilext.toast

class CircleTagAdapter(var tagMaxCount:Int=0): BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCreateCircleTagBinding>>(R.layout.item_create_circle_tag){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCreateCircleTagBinding>, item: NewCirceTagBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
            checkBox.apply {
                setOnClickListener {
                    //0 表示不限制
                    if(0!=tagMaxCount&&isChecked){
                        //获取已选中集合大小
                        val isCheckSize=data.filter {it.isCheck==true}.size
                        //已选中的集合大小已经等于最大限制则不能在被选中
                        if(isCheckSize>=tagMaxCount){
                            isChecked=false
                            context.getString(R.string.str_mostOptionalTagX,tagMaxCount).toast()
                        }
                    }
                    item.isCheck=isChecked
                }
            }
        }
    }
}