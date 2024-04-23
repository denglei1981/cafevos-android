package com.changanford.my.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R
import com.changanford.my.databinding.ItemMineBottomRyBinding

/**
 * @author: niubobo
 * @date: 2024/4/23
 * @description：
 */
class MineBottomRyAdapter :
    BaseQuickAdapter<MenuBeanItem, BaseDataBindingHolder<ItemMineBottomRyBinding>>(
        R.layout.item_mine_bottom_ry
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMineBottomRyBinding>,
        item: MenuBeanItem
    ) {
        holder.dataBinding?.apply {
            ivIcon.load(item.icon)
            tvName.text=item.menuName
            root.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
            }
        }
    }
}