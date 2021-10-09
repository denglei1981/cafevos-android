package com.changanford.home.news.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.ItemHomeNewsCommentBinding

class HomeNewsCommentAdapter(context: Context) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_home_news_comment) {
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemHomeNewsCommentBinding>(holder.itemView)
        binding?.let {
//            binding.ivHead.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
//            binding.bean = item
            GlideUtils.loadBD(item.avatar, it.ivHead)
            it.tvName.text = item.nickname
            it.tvContent.text = item.content
            it.tvTime.text = item.timeStr
            it.tvCommentCount.text=CountUtils.formatNum(item.likesCount,false)
        }
    }

}

