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


class MyColletPostAdapter :
    BaseQuickAdapter<PostDataBean, BaseDataBindingHolder<ItemMysNewsBinding>>(R.layout.item_mys_news) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysNewsBinding>,
        item: PostDataBean
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.pics, t.ivCircle)
            val content = if (item.title?.isNotEmpty() == true) {
                item.title
            } else item.content
            t.tvCircleTitle.text= content

        }
    }


}