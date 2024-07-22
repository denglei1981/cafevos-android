package com.changanford.common.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.ShareEditBean
import com.changanford.common.databinding.ItemShareEditeBinding

/**
 *Author lcw
 *Time on 2022/10/18
 *Purpose
 */
class ShareEditeAdapter :
    BaseQuickAdapter<ShareEditBean, BaseDataBindingHolder<ItemShareEditeBinding>>(
        R.layout.item_share_edite
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemShareEditeBinding>,
        item: ShareEditBean
    ) {
        holder.dataBinding?.let {
            it.ivSrc.setImageResource(item.topDrawable)
            it.tvName.text = item.name
        }
    }
}