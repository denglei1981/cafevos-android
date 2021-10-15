package com.changanford.home.news.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.ItemHomeNewsCommentBinding

class HomeNewsCommentAdapter(context: Context) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_home_news_comment) {

    init {
        addChildClickViewIds(R.id.tv_like_count,R.id.iv_like)
    }
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemHomeNewsCommentBinding>(holder.itemView)
        binding?.let {
            GlideUtils.loadBD(item.avatar, it.ivHead)
            binding.bean=item
            it.tvName.text = item.nickname
            it.tvTime.text = item.timeStr
            it.tvLikeCount.text=CountUtils.formatNum(item.likesCount.toString(),false)
            it.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.home_comment_like
                } else R.mipmap.home_comment_no_like
            )
        }
    }

}

