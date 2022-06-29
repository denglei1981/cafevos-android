package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.Topic
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysJoinTopicBinding
import com.changanford.evos.databinding.ItemMysJoinTopicMoreBinding


class MyJoinTopicMoreAdapter :
    BaseQuickAdapter<Topic, BaseDataBindingHolder<ItemMysJoinTopicMoreBinding>>(R.layout.item_mys_join_topic_more) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysJoinTopicMoreBinding>,
        item: Topic
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.pic, t.ivCircle)
            t.tvCircleTitle.text = item.name
            t.tvCircleDesc.text = item.description


            t.tvPostCount.text = CountUtils.formatNum(item.postsCount.toString(),false).toString().plus("\t帖子")
            t.tvPeople.text = CountUtils.formatNum(item.heat,false).toString().plus("\t热度")
        }
    }


}