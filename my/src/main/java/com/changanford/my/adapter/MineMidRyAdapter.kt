package com.changanford.my.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineMidRyBinding

/**
 * @author: niubobo
 * @date: 2024/4/23
 * @description：
 */
class MineMidRyAdapter :
    BaseQuickAdapter<MenuBeanItem, BaseDataBindingHolder<ItemMineMidRyBinding>>(R.layout.item_mine_mid_ry) {
    override fun convert(holder: BaseDataBindingHolder<ItemMineMidRyBinding>, item: MenuBeanItem) {
        holder.dataBinding?.let {
            it.ivIcon.load(item.icon)
            it.tvName.text = item.menuName
            it.root.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
            }
        }
    }

}