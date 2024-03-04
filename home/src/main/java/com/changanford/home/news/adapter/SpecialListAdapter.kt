package com.changanford.home.news.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.MUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.home.R
import com.changanford.home.bean.SpecialListBean
import com.changanford.home.databinding.ItemHomeSpecialBinding

class SpecialListAdapter :
    BaseQuickAdapter<SpecialListBean, BaseDataBindingHolder<ItemHomeSpecialBinding>>(R.layout.item_home_special) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeSpecialBinding>,
        item: SpecialListBean
    ) {

        holder.dataBinding?.let {
//             GlideUtils.loadBD(item.getPicUrl(),it.ivSpecial)
            MUtils.setTopMargin(it.root, 16, holder.layoutPosition)
            it.ivSpecial.loadCompress(item.getPicUrl())
            it.tvContent.text = item.summary
            it.tvTitle.text = item.title
            it.tvCount.text = item.getCount()
        }


    }
}