package com.changanford.my.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R

class MenuAdapter : BaseQuickAdapter<MenuBeanItem, BaseViewHolder>(R.layout.item_mymenus) {
    override fun convert(holder: BaseViewHolder, item: MenuBeanItem) {
        holder.setText(R.id.toolName, item.menuName)
        holder.getView<ImageView>(R.id.toolImg).load(item.icon)
        holder.getView<View>(R.id.mytoollayout).setOnClickListener {
            JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
        }
    }
}