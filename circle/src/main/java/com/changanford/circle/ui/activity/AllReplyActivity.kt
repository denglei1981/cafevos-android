package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.TextView
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.ItemCommentAdapter
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChildCommentListBean
import com.changanford.circle.databinding.ActivityAllReplyBinding
import com.changanford.circle.viewmodel.AllReplyViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toastShow
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

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
    private var mAuthors: AuthorBaseVo? = null

    private var page = 1

    var childCommentListBean: ChildCommentListBean? = null

    private val commentAdapter by lazy {
        ItemCommentAdapter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        type = intent.getIntExtra("type", 2)
        groupId = intent.getStringExtra("groupId").toString()
        bizId = intent.getStringExtra("bizId").toString()
        val chileCount = intent.getStringExtra("childCount").toString()
        binding.run {
            ryComment.adapter = commentAdapter
            AppUtils.setStatusBarMarginTop(title.root, this@AllReplyActivity)
            title.run {
                tvTitle.text = "${chileCount}条回复"
                ivBack.setOnClickListener { finish() }
            }

        }

        initListener()
        binding.tvTalk.setOnClickListener {
            ReplyDialog(this, object : ReplyDialog.ReplyListener {
                override fun getContent(content: String) {
                    childCommentListBean?.let {
                        when (type) {
                            1 -> {
                                viewModel.addNewsComment(bizId, it.groupId, it.id, content)
                            }

                            2 -> {
                                viewModel.addPostsComment(bizId, it.groupId, it.id, content)
                            }
                        }
                    }
                }
            }, hintText = "回复@" + childCommentListBean?.nickname).show()

        }
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
            var commentItem = commentAdapter.getItem(position = position)
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
            }, hintText = "回复@" + commentItem.nickname).show()
        }
    }

    override fun observe() {
        super.observe()
        viewModel.commentBean.observe(this) {
            mAuthors = it.authorBaseVo
            val item = it
            childCommentListBean = item
            binding.tvTalk.text = "回复@${item.nickname}"
            binding.layoutHeader.ivHeader.loadImage(
                item.avatar,
                ImageOptions().apply { circleCrop = true })
            binding.layoutHeader.tvAuthorName.text = item.nickname
            binding.tvTime.text = item.getTimeAndAddress()
            if (!item.memberIcon.isNullOrEmpty()) {
                binding.layoutHeader.ivVip.load(item.memberIcon)
            }
            binding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    followAction(item.authorBaseVo)
                }
            }
            setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo)
            if (item.authorBaseVo.carOwner.isNullOrEmpty()) {
                binding.layoutHeader.tvSubTitle.isVisible = false
            } else {
                binding.layoutHeader.tvSubTitle.isVisible = true
                binding.layoutHeader.tvSubTitle.text = item.authorBaseVo.carOwner
            }
            val labelAdapter = LabelAdapter(16)
            binding.layoutHeader.rvUserTag.adapter = labelAdapter
            labelAdapter.setList(item.imags)

            binding.tvContent.text = item.content

            binding.clTopContent.setOnClickListener { _ ->
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
                }, hintText = "回复@" + childCommentListBean?.nickname).show()
            }
            binding.layoutHeader.ivHeader.setOnClickListener { _ ->
                JumpUtils.instans?.jump(35, it.userId)
            }

        }

        viewModel.commentListBean.observe(this) {
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
        }

        viewModel.addCommendBean.observe(this) {
            page = 1
            initData()
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CHILD_COUNT)
                .postValue(commentAdapter.itemCount)
        }
    }

    private fun followAction(authorBaseVo: AuthorBaseVo) {
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        if (followType == 2) { //取消关注
            cancelFollowDialog(authorBaseVo.authorId, followType)
        } else {
            //埋点
            BuriedUtil.instant?.communityFollow(authorBaseVo.nickname)
            getFollow(authorBaseVo.authorId, followType)
        }

    }

    private fun cancelFollowDialog(followId: String, type: Int) {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.dialog_cancel_follow)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, {
                        getFollow(followId, type)
                    }, true)
                    .withClick(R.id.btn_cancel, {
                    }, true)
            )
            .show()
    }

    private fun getFollow(followId: String, type: Int) {
        BaseApplication.curActivity.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .userFollowOrCancelFollow(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (type == 1) {
                        toastShow("已关注")
                    } else {
                        toastShow("取消关注")
                    }
                    mAuthors?.isFollow = type
                    mAuthors?.let { it1 -> setFollowState(binding.layoutHeader.btnFollow, it1) }
                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }
        }
    }

    private fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(this)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }
}