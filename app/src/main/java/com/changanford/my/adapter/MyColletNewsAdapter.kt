package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.Topic
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysJoinTopicBinding
import com.changanford.evos.databinding.ItemMysNewsBinding


class MyColletNewsAdapter :
    BaseQuickAdapter<InfoDataBean, BaseDataBindingHolder<ItemMysNewsBinding>>(R.layout.item_mys_news) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysNewsBinding>,
        item: InfoDataBean
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.getPicCover(), t.ivCircle)
            val content = if (!item.title.isEmpty()) {
                item.title
            } else item.content
            t.tvCircleTitle.text= content


        }
    }


}