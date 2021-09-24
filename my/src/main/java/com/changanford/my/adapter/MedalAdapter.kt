package com.changanford.my.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R

class MedalAdapter: BaseQuickAdapter<MenuBeanItem, BaseViewHolder>(R.layout.item_medal){
    override fun convert(holder: BaseViewHolder, item: MenuBeanItem) {
        holder.setText(R.id.medalName,item.menuName)
        holder.getView<ImageView>(R.id.medalImg).apply {
            load(item.icon)
            setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
        }
    }
}