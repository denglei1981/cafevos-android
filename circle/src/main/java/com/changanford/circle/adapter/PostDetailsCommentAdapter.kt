package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.databinding.ItemPostDetailsCommentBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.launchWithCatch
import com.changanford.circle.widget.CommentLoadMoreView
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

class PostDetailsCommentAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_post_details_comment),
    LoadMoreModule {

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemPostDetailsCommentBinding>(holder.itemView)
        binding?.let {
            binding.ivHead.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            binding.bean = item
            binding.tvLikeCount.text = if (item.likesCount == 0) "" else item.likesCount.toString()
            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.circle_comment_like
                } else R.mipmap.circle_comment_no_like
            )
            binding.llLike.setOnClickListener {
                lifecycleOwner.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"] = 2
                    val rKey = getRandomKey()
                    ApiClient.createApi<CircleNetWork>()
                        .commentLike(body.header(rKey), body.body(rKey)).also {
                            it.msg.toast()
                            if (it.code == 0) {
                                if (item.isLike == 0) {
                                    item.isLike = 1
                                    item.likesCount++
                                } else {
                                    item.likesCount--
                                    item.isLike = 0
                                }
                                binding.tvLikeCount.text =
                                    if (item.likesCount == 0) "" else item.likesCount.toString()
                                binding.ivLike.setImageResource(
                                    if (item.isLike == 1) {
                                        AnimScaleInUtil.animScaleIn(binding.ivLike)
                                        R.mipmap.circle_comment_like
                                    } else R.mipmap.circle_comment_no_like
                                )
                            }
                        }
                }
            }
            if (item.childCount == 0) {
                binding.tvChildCount.visibility = View.GONE
            } else {
                binding.tvChildCount.visibility = View.VISIBLE
                binding.tvChildCount.text = "${item.childCount}回复"
            }
            binding.ivHead.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("value", item.userId)
//                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
                JumpUtils.instans?.jump(35,item.userId.toString())
            }
        }
    }

}