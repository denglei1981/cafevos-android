package com.changanford.my.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.R

class MedalAdapter: BaseQuickAdapter<MedalListBeanItem, BaseViewHolder>(R.layout.item_medal){
    override fun convert(holder: BaseViewHolder, item: MedalListBeanItem) {
        holder.setText(R.id.medalName,item.medalName)
        holder.getView<ImageView>(R.id.medalImg).apply {
            load(item.medalImage)
            setOnClickListener {
                JumpUtils.instans?.jump(29,item.medalId)
            }
        }
    }
}