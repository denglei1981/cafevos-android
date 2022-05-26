package com.changanford.my.adapter

import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MenuBeanItem
import com.changanford.my.R
import com.changanford.my.bean.MineMenuData
import com.changanford.my.databinding.ItemMineMenuBinding
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MineMenuAdapter :
    BaseQuickAdapter<MineMenuData, BaseDataBindingHolder<ItemMineMenuBinding>>(R.layout.item_mine_menu) {
    override fun convert(holder: BaseDataBindingHolder<ItemMineMenuBinding>, item: MineMenuData) {
        holder.dataBinding?.let { t ->
            val mineFastUsedAdapter = MineFastUsedAdapter()
            mineFastUsedAdapter.setNewInstance(item.list)
            t.rvMenu.adapter = mineFastUsedAdapter
            t.tvTitle.text = item.title
            val layoutManager =GridLayoutManager(context,4)
            layoutManager.orientation = GridLayoutManager.VERTICAL
            t.rvMenu.layoutManager = layoutManager
        }
    }


}