package com.changanford.home.news.adapter

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.ItemHomeNewsCommentBinding
import com.changanford.home.util.AnimScaleInUtil
import com.changanford.home.util.launchWithCatch


class HomeNewsCommentAdapter(var lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<CommentListBean, BaseViewHolder>(R.layout.item_home_news_comment) {

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

            binding.ivHead.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("value", item.userId)
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }
            binding.llLike.setOnClickListener {
                lifecycleOwner.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"]=1
                    val rKey = getRandomKey()
                    ApiClient.createApi<HomeNetWork>()
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
                                binding.tvLikeCount.text = item.likesCount.toString()
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



}

