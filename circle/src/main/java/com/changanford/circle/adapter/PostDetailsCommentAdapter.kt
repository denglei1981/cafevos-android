package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CommentListBean
import com.changanford.circle.databinding.ItemPostDetailsCommentBinding
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.widget.CommentLoadMoreView
import com.changanford.common.MyApp
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast

class PostDetailsCommentAdapter(private val mLifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_post_details_comment),
    LoadMoreModule {

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemPostDetailsCommentBinding>(holder.itemView)
        binding?.apply {
            layoutHeader.ivHeader.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            layoutHeader.tvAuthorName.text = item.nickname
            if (!item.memberIcon.isNullOrEmpty()) {
                layoutHeader.ivVip.load(item.memberIcon)
            }
            if (item.carOwner.isNullOrEmpty()) {
                layoutHeader.tvSubTitle.isVisible = false
            } else {
                layoutHeader.tvSubTitle.isVisible = true
                layoutHeader.tvSubTitle.text = item.carOwner
            }
            if (item.isFollow == 1) {//已关注
                layoutHeader.btnFollow.text = "已关注"
                layoutHeader.btnFollow.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_4d16
                    )
                )
                layoutHeader.btnFollow.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_80a6_100)
            } else {
                layoutHeader.btnFollow.text = "关注"
                layoutHeader.btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white))
                layoutHeader.btnFollow.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_1700f4_100)
            }
            binding.bean = item
            binding.tvLikeCount.text = item.likesCount.toString()
            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.color_1700F4))
                    R.mipmap.circle_comment_like
                } else {
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
                    R.mipmap.circle_comment_no_like
                }

            )
            tvContent.text = item.content
            tvContent.post {
                MUtils.expandText(tvContent, item.content)
            }
            binding.llLike.setOnClickListener {
                mLifecycleOwner.launchWithCatch {
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
                llChild.isVisible = false
            } else {
                llChild.isVisible = true
                val childAdapter = PostCommentChildAdapter(lifecycleOwner = mLifecycleOwner)
                childAdapter.setNewInstance(item.childVo)
                binding.rvChild.adapter = childAdapter

                binding.tvChildCount.text = "共${item.childCount}条回复"
                childAdapter.setOnItemClickListener { _, _, position ->
                    val commentBean = childAdapter.getItem(position)
                    val bundle = Bundle()
                    bundle.putString("groupId", commentBean.groupId)
                    bundle.putInt("type", 2)// 1 资讯 2 帖子
                    bundle.putString("bizId", commentBean.bizId)
                    startARouter(ARouterCirclePath.AllReplyActivity, bundle)
                }
            }
            val labelAdapter = LabelAdapter(16)
            layoutHeader.rvUserTag.adapter = labelAdapter
            labelAdapter.setNewInstance(item.imags)

            layoutHeader.ivHeader.setOnClickListener {
                JumpUtils.instans?.jump(35, item.userId)
            }
        }
    }

}