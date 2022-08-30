package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.*
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysJoinTopicBinding
import com.changanford.evos.databinding.ItemMysNewsBinding


class MyColletAccAdapter :
    BaseQuickAdapter<ActBean, BaseDataBindingHolder<ItemMysNewsBinding>>(R.layout.item_mys_news) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysNewsBinding>,
        item: ActBean
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.coverImg, t.ivCircle)

            t.tvCircleTitle.text= item.title

        }
    }


}