package com.changanford.home.news.adapter

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
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.ItemHomeNewsCommentBinding
import com.changanford.home.util.AnimScaleInUtil
import com.changanford.home.util.launchWithCatch
import com.changanford.home.widget.MyLinkMovementMethod


class HomeNewsCommentAdapter(var lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_home_news_comment),
    LoadMoreModule {



    override fun convert(holder: BaseViewHolder, item: CommentListBean) {
        val binding = DataBindingUtil.bind<ItemHomeNewsCommentBinding>(holder.itemView)
        binding?.let {

            if (item.typeNull == 1) {
                it.gNoComment.visibility = View.VISIBLE
                it.conComment.visibility = View.GONE
                return@let
            }
            it.gNoComment.visibility = View.GONE
            it.conComment.visibility = View.VISIBLE
            GlideUtils.loadBD(item.avatar, it.ivHead)
            binding.bean = item
            it.tvName.text = item.nickname
            it.tvTime.text = item.timeStr
            it.tvLikeCount.visibility=if(item.likesCount==0) View.INVISIBLE else View.VISIBLE
            it.tvLikeCount.text = CountUtils.formatNum(item.likesCount.toString(), false)
            it.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.home_comment_like
                } else R.mipmap.home_comment_no_like
            )

            binding.ivHead.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("value", item.userId)
//                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)

                JumpUtils.instans?.jump(35,item.userId.toString())
            }
            binding.tvContent.text = item.content
            binding.llLike.setOnClickListener {
                lifecycleOwner.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"] = 1
                    val rKey = getRandomKey()
                    ApiClient.createApi<HomeNetWork>()
                        .commentLike(body.header(rKey), body.body(rKey)).also { cr ->
//                                cr.msg.toast()
                                if (cr.code == 0) {
                                    if (item.isLike == 0) {
                                        item.isLike = 1
                                        item.likesCount++
                                    } else {
                                        item.likesCount--
                                        item.isLike = 0
                                    }
                                    binding.tvLikeCount.text = item.likesCount.toString()
                                    binding.tvLikeCount.visibility=if(item.likesCount==0) View.INVISIBLE else View.VISIBLE
                                    binding.ivLike.setImageResource(
                                        if (item.isLike == 1) {
                                            AnimScaleInUtil.animScaleIn(binding.ivLike)
                                            R.mipmap.home_comment_like
                                        } else R.mipmap.home_comment_no_like
                                    )
                                }
                        }
                }
            }
        }
    }

    /**
     * 处理回复文本各种样式
     */
    private fun contentSty(contentTv: TextView?, item: CommentListBean) {
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
                            "回复@${pare.nickname}：",
                            R.color.color_99,
                            object : ClickableSpan() {
                                //设置点击事件
                                override fun onClick(widget: View) {
//                                    val bundle = Bundle()
//                                    bundle.putString("value", pare.userId)
//                                    startARouter(ARouterMyPath.TaCentreInfoUI, bundle)

                                    JumpUtils.instans?.jump(35,pare.userId.toString())
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
                            if (item.isOpenParent) " 收起" else " 追踪",
                            R.color.blue_tab,
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

