package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.databinding.ItemCommentChildBinding
import com.changanford.circle.widget.CommentLoadMoreView

class PostCommentChildAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_comment_child),
    LoadMoreModule {

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemCommentChildBinding>(holder.itemView)
        binding?.let {
            binding.apply {
                if (item.parentVo.isNullOrEmpty()) {
                    tvName.text = "${item.nickname}:"
                } else {
                    tvName.text = "${item.nickname}回复${item.parentVo[0].nickname}:"
                }
                tvContent.text = item.content
            }
//            binding.ivHead.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            binding.bean = item
//            binding.tvLikeCount.text = if (item.likesCount == 0) "" else item.likesCount.toString()
//            binding.ivLike.setImageResource(
//                if (item.isLike == 1) {
//                    R.mipmap.circle_comment_like
//                } else R.mipmap.circle_comment_no_like
//            )
//            binding.llLike.setOnClickListener {
//                lifecycleOwner.launchWithCatch {
//                    val body = MyApp.mContext.createHashMap()
//                    body["commentId"] = item.id
//                    body["type"] = 2
//                    val rKey = getRandomKey()
//                    ApiClient.createApi<CircleNetWork>()
//                        .commentLike(body.header(rKey), body.body(rKey)).also {
//                            it.msg.toast()
//
//                            if (it.code == 0) {
//                                if (item.isLike == 0) {
//                                    item.isLike = 1
//                                    item.likesCount++
//                                } else {
//                                    item.likesCount--
//                                    item.isLike = 0
//                                }
//                                binding.tvLikeCount.text =
//                                    if (item.likesCount == 0) "" else item.likesCount.toString()
//                                binding.ivLike.setImageResource(
//                                    if (item.isLike == 1) {
//                                        AnimScaleInUtil.animScaleIn(binding.ivLike)
//                                        R.mipmap.circle_comment_like
//                                    } else R.mipmap.circle_comment_no_like
//                                )
//                            }
//                        }
//                }
//            }
//
//            binding.ivHead.setOnClickListener {
//                JumpUtils.instans?.jump(35,item.userId.toString())
//            }
        }
    }

    override fun getItemCount(): Int {
        return if (data.size > 2) 2 else data.size
    }
}