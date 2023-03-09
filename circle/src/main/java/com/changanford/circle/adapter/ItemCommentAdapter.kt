package com.changanford.circle.adapter

import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChildCommentListBean
import com.changanford.circle.databinding.ItemItemCommentBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.widget.CommentLoadMoreView
import com.changanford.circle.widget.MyLinkMovementMethod
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

class ItemCommentAdapter(private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<ChildCommentListBean, BaseViewHolder>(R.layout.item_item_comment),
    LoadMoreModule {

    init {
        loadMoreModule.loadMoreView = CommentLoadMoreView()
    }

    override fun convert(holder: BaseViewHolder, item: ChildCommentListBean) {
        val binding = DataBindingUtil.bind<ItemItemCommentBinding>(holder.itemView)
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
                            LiveDataBus.get().with(LiveDataBusKey.CHILD_COMMENT_STAR).postValue(1)
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
            binding.ivHead.setOnClickListener {
                JumpUtils.instans?.jump(35,item.userId.toString())
            }

            contentSty(binding.tvContent, item)
        }
    }

    /**
     * 处理回复文本各种样式
     */
    private fun contentSty(contentTv: TextView?, item: ChildCommentListBean) {
        if (contentTv != null) {
            contentTv.text = item.content
            contentTv.movementMethod = MyLinkMovementMethod.get()//点击必用
            if (!item.parentVo.isNullOrEmpty()) {
                val size = item.parentVo.size
                item.parentVo.forEachIndexed { index, pare ->
                    if (index == size - 1) return@forEachIndexed//最后一个不需要再进行追加，接口返回的多余数据
                    if (index > 1 && !item.isOpenParent) {//数据大于1，已经展开
                        return@forEachIndexed
                    }
                    //开始对contentTv追加名字效果
                    contentTv.append(
                        SpannableStringUtils.getSpannable(
                            "//@${pare.nickname}：",
                            R.color.color_8195C8,
                            object : ClickableSpan() {
                                //设置点击事件
                                override fun onClick(widget: View) {
                                    JumpUtils.instans?.jump(35,pare.userId)
                                }
                                override fun updateDrawState(ds: TextPaint) {
                                    ds.isUnderlineText = false
                                }
                            })
                    )
                    //开始对contentTv追加内容
                    contentTv.append(pare.content)
                }
                if (item.parentVo.size - 1 > 2) {//只有数据大于2的时候才会有收起或追踪按钮
                    //追加可点击的收缩效果
                    contentTv.append(
                        SpannableStringUtils.getSpannable(
                            if (item.isOpenParent) " 收起" else " 展开",
                            R.color.color_8195C8,
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    item.isOpenParent = !item.isOpenParent
                                    contentSty(contentTv, item)
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                    ds.isUnderlineText = false
                                }
                            })
                    )
                }
            }

        }
    }
}