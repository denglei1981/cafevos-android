package com.changanford.my.adapter

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.my.R
import com.changanford.my.bean.MineMenuData
import com.changanford.my.databinding.ItemMineMenuBinding


class MineMenuAdapter :
    BaseQuickAdapter<MineMenuData, BaseDataBindingHolder<ItemMineMenuBinding>>(R.layout.item_mine_menu) {
    override fun convert(holder: BaseDataBindingHolder<ItemMineMenuBinding>, item: MineMenuData) {
        holder.dataBinding?.let { t ->
            if (item.title == "我的订单") {
                val mineFastUsedAdapter = MineFastUsedAdapter()
                mineFastUsedAdapter.setNewInstance(item.list)
                val layoutManager = GridLayoutManager(context, 4)
                layoutManager.orientation = GridLayoutManager.VERTICAL
                t.rvMenu.layoutManager = layoutManager
                t.rvMenu.adapter = mineFastUsedAdapter
            } else {
                val bottomAdapter = MineBottomRyAdapter()
                bottomAdapter.setList(item.list)
                val layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                t.rvMenu.layoutManager = layoutManager
                t.rvMenu.adapter = bottomAdapter
            }
            if (!TextUtils.isEmpty(item.title)) {
                t.tvTitle.text = item.title
                t.tvTitle.visibility = View.VISIBLE
            } else {
                t.tvTitle.visibility = View.GONE
            }


        }
    }


}