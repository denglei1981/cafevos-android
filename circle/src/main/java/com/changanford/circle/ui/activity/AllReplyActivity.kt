package com.changanford.circle.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.ItemCommentAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityAllReplyBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.launchWithCatch
import com.changanford.circle.viewmodel.AllReplyViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.xiaomi.push.it

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 全部回复
 */
@Route(path = ARouterCirclePath.AllReplyActivity)
class AllReplyActivity : BaseActivity<ActivityAllReplyBinding, AllReplyViewModel>() {

    private var type: Int = 2
    private var groupId: String = ""
    private var bizId: String = "null"

    private var page = 1

    private val commentAdapter by lazy {
        ItemCommentAdapter(this)
    }

    override fun initView() {
        type = intent.getIntExtra("type", 2)
        groupId = intent.getStringExtra("groupId").toString()
        bizId = intent.getStringExtra("bizId").toString()
        binding.run {
            ryComment.adapter = commentAdapter
            AppUtils.setStatusBarMarginTop(title.root, this@AllReplyActivity)
            title.run {
                tvTitle.text = "全部回复"
                ivBack.setOnClickListener { finish() }
            }
        }

        initListener()
    }

    override fun initData() {
        viewModel.getListData(bizId, groupId, type.toString(), page)
    }

    private fun initListener() {
        commentAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            initData()
        }
        commentAdapter.setOnItemClickListener { adapter, view, position ->
            ReplyDialog(this, object : ReplyDialog.ReplyListener {
                override fun getContent(content: String) {
                    val bean = commentAdapter.getItem(position)
                    when (type) {
                        1 -> {// 资讯
                            viewModel.addNewsComment(bizId, bean.groupId, bean.id, content)
                        }
                        2 -> {//帖子
                            viewModel.addPostsComment(bizId, bean.groupId, bean.id, content)
                        }
                    }

                }
            }).show()
        }
    }

    override fun observe() {
        super.observe()
        viewModel.commentBean.observe(this, {
            val item = it
            binding.ivHead.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            binding.tvName.text = item.nickname
            binding.tvTime.text = item.timeStr
            binding.tvLikeCount.text = if (item.likesCount == 0) "" else item.likesCount.toString()
            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.circle_comment_like
                } else R.mipmap.circle_comment_no_like
            )
            binding.tvContent.text = item.content

            binding.llTopComment.setOnClickListener { _ ->
                ReplyDialog(this, object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        when (type) {
                            1 -> {
                                viewModel.addNewsComment(bizId, it.groupId, it.id, content)
                            }
                            2 -> {
                                viewModel.addPostsComment(bizId, it.groupId, it.id, content)
                            }
                        }

                    }
                }).show()
            }
            binding.ivHead.setOnClickListener { _ ->
                val bundle = Bundle()
                bundle.putString("value", it.userId)
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }
            binding.llLike.setOnClickListener {
                this.launchWithCatch {
                    val body = MyApp.mContext.createHashMap()
                    body["commentId"] = item.id
                    body["type"] = type
                    val rKey = getRandomKey()
                    ApiClient.createApi<CircleNetWork>()
                        .commentLike(body.header(rKey), body.body(rKey)).also { it1 ->
//                            it1.msg.toast()
                            if (it1.code == 0) {
                                if (item.isLike == 0) {
                                    item.isLike = 1
                                    item.likesCount++
                                } else {
                                    item.likesCount--
                                    item.isLike = 0
                                }
                                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_COMMENT_ITEM)
                                    .postValue(item.isLike)
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
        })

        viewModel.commentListBean.observe(this, {
            if (page == 1) {
                commentAdapter.setList(it)
                if (it.size == 0) {
                    commentAdapter.setEmptyView(R.layout.circle_comment_empty_layout)
                }
            } else {
                commentAdapter.addData(it)
                commentAdapter.loadMoreModule.loadMoreComplete()
            }
            if (it.size != 20) {
                commentAdapter.loadMoreModule.loadMoreEnd()
            }
        })

        viewModel.addCommendBean.observe(this, {
            page = 1
            initData()
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CHILD_COUNT)
                .postValue(commentAdapter.itemCount)
        })
    }
}