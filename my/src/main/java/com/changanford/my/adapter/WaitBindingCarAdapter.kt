package com.changanford.my.adapter

import android.view.Gravity
import android.widget.ImageView
import android.widget.RadioGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.BindCarBean
import com.changanford.common.bean.MyFastInData
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.my.R
import com.changanford.my.databinding.ItemWaitBindingCarBinding

class WaitBindingCarAdapter(val groupInterface: groupInterface): BaseQuickAdapter<BindCarBean, BaseDataBindingHolder<ItemWaitBindingCarBinding>>(R.layout.item_wait_binding_car){
    override fun convert(
        holder: BaseDataBindingHolder<ItemWaitBindingCarBinding>,
        item: BindCarBean
    ) {
        holder.dataBinding?.let { bd->
                bd.model=item
                GlideUtils.loadBD(item.modelUrl,bd.ivCar)

                bd.radioGroup.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
                 override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                     groupInterface.groupInt()
                     when (checkedId) {
                         bd.checkbox.id -> {
                             item.confirm = 1
                         }
                         bd.checkboxNot.id -> {
                             item.confirm = 0
                         }
                         bd.checkboxAgain.id -> {
                             item.confirm = -1
                         }
                     }
                 }
             })
        }

    }

}
interface groupInterface {
    fun groupInt()
}