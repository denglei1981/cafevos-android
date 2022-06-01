package com.changanford.my.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.bean.MyFastInData
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R

class MineFastUsedAdapter: BaseQuickAdapter<MenuBeanItem, BaseViewHolder>(R.layout.item_my_fast_in){
    override fun convert(holder: BaseViewHolder, item: MenuBeanItem) {
        holder.setText(R.id.tv_name, item.menuName)
        if(      !TextUtils.isEmpty(item.icon)){
            holder.getView<ImageView>(R.id.iv_icon).load(item.icon)
        }else{
            holder.getView<ImageView>(R.id.iv_icon).load(item.drawInt)
        }


        holder.getView<View>(R.id.mytoollayout).setOnClickListener {
            JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
        }
    }
}