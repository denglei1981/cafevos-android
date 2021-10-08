package com.changanford.circle.adapter

import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.databinding.ItemPostDetailsCommentBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage

class PostDetailsCommentAdapter() :
    BaseQuickAdapter<CommentListBean,BaseViewHolder>( R.layout.item_post_details_comment),LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding =DataBindingUtil.bind<ItemPostDetailsCommentBinding>(holder.itemView)
        binding?.let {
            binding.ivHead.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            binding.bean = item
        }
    }
}