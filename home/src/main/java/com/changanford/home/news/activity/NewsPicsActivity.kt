package com.changanford.home.news.activity

import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.databinding.ActivityNewsPicDetailsBinding
import com.changanford.home.news.request.NewsDetailViewModel
import androidx.lifecycle.Observer
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.setDrawableTop
import com.changanford.home.SetFollowState
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.news.data.Authors
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.widget.ReplyDialog
import com.google.android.material.button.MaterialButton

/**
 *  图片详情。
 * */
@Route(path = ARouterHomePath.NewsPicsActivity)
class NewsPicsActivity : BaseActivity<ActivityNewsPicDetailsBinding, NewsDetailViewModel>() ,View.OnClickListener{

    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.white)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutHeader.conHomeBar, this)
    }
    private lateinit var artId: String
    override fun initData() {
        artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            if (!TextUtils.isEmpty(artId)) {
                viewModel.getNewsDetail(artId!!)
                viewModel.getNewsCommentList(artId, false)
            } else {
                ToastUtils.showShortToast("没有该资讯类型", this)
            }

        }

    }

    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
        viewModel.commentsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
//                    homeNewsCommentAdapter.addData(it.data.dataList)
                } else {
//                    homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                }
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
        viewModel.commentSateLiveData.observe(this, Observer {
            if (it.isSuccess) {
                ToastUtils.showShortToast("评论成功", this)
                // 评论数量加1. 刷新评论。
                viewModel.getNewsCommentList(artId, false)
                setCommentCount()
            } else {
                ToastUtils.showShortToast(it.message, this)
            }

        })
        viewModel.actionLikeLiveData.observe(this, Observer {
            if (it.isSuccess) {
            } else {// 网络原因操作失败了。
                ToastUtils.showShortToast(it.message, this)
                setLikeState()
            }
        })
        viewModel.followLiveData.observe(this, Observer {})
    }


    var newsDetailData: NewsDetailData? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors
        GlideUtils.loadBD(author.avatar, binding.layoutHeader.ivAvatar)
        setFollowState(binding.layoutHeader.btnFollow, author)


        try {

            if (TextUtils.isEmpty(newsDetailData.specialTopicTitle)) {
                binding.llSpecial.visibility = View.GONE
            }
            binding.tvTopicName.text = newsDetailData.specialTopicTitle
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.llSpecial.setOnClickListener {// 跳转到专题详情。
            if (newsDetailData.specialTopicId > 0) {
                JumpUtils.instans?.jump(8, newsDetailData.specialTopicId.toString())
            }
        }
        binding.layoutHeader.btnFollow.setOnClickListener {
            if (MConstant.token.isEmpty()) {
                startARouter(ARouterMyPath.SignUI)
            } else {
                followAction()
            }
        }
        binding.llComment.tvNewsToLike.text = newsDetailData.getLikeCount()
        binding.llComment.tvNewsToShare.text = newsDetailData.getShareCount()
        binding.llComment.tvNewsToMsg.text = newsDetailData.getCommentCount()
        binding.llComment.tvNewsToLike.setOnClickListener(this)
        binding.llComment.tvNewsToShare.setOnClickListener(this)
        binding.llComment.tvNewsToMsg.setOnClickListener(this)
        if (newsDetailData.isLike == 0) {
            binding.llComment.tvNewsToLike.setDrawableTop(this, R.drawable.icon_home_bottom_unlike)
        } else {
            binding.llComment.tvNewsToLike.setDrawableTop(this, R.drawable.icon_home_bottom_like)
        }

    }
    private fun setCommentCount() {
        // 评论成功自增1
        val commentCount = newsDetailData?.commentCount?.plus(1)
        binding.llComment.tvNewsToMsg.text =
            CountUtils.formatNum(commentCount.toString(), false).toString()

    }

    private fun setLikeState() { //设置是否喜欢文章。
        var likesCount = newsDetailData?.likesCount
        when (newsDetailData?.isLike) {
            0 -> {
                newsDetailData?.isLike = 1
                binding.llComment.tvNewsToLike.setDrawableTop(
                    this,
                    R.drawable.icon_home_bottom_like
                )
                newsDetailData?.getLikeCount() + 1
                likesCount = newsDetailData?.likesCount?.plus(1)
                binding.llComment.tvNewsToLike.text =
                    CountUtils.formatNum(likesCount.toString(), false).toString()
            }
            1 -> {
                newsDetailData?.isLike = 0
                binding.llComment.tvNewsToLike.setDrawableTop(
                    this,
                    R.drawable.icon_home_bottom_unlike
                )
                likesCount = newsDetailData?.likesCount?.minus(1)
                binding.llComment.tvNewsToLike.text =
                    CountUtils.formatNum(likesCount.toString(), false).toString()

            }
        }
        if (likesCount != null) {
            newsDetailData?.likesCount = likesCount
        }
    }
    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: Authors) {
        val setFollowState = SetFollowState(this)
        setFollowState.setFollowState(btnFollow, authors)
    }
    // 关注或者取消
    private fun followAction() {
        newsDetailData?.let {
            var followType = it.authors.isFollow
            when (followType) {
                0 -> {
                    followType = 1
                }
                1 -> {
                    followType = 0
                }
            }
            it.authors.isFollow = followType;
            setFollowState(binding.layoutHeader.btnFollow, it.authors)
            viewModel.followOrCancelUser(it.userId, followType)
        }
    }

    override fun onClick(v: View) {
        if (MConstant.token.isEmpty()) {
            startARouter(ARouterMyPath.SignUI)
            return
        }
        when (v.id) {
            R.id.tv_speak_something -> {
                replay()
            }
            R.id.tv_news_to_like -> {
                // 这里要防抖？
                // 无论成功与否，先改状态?
                // 获取当前对象喜欢与否的状态。
                setLikeState()
                viewModel.actionLike(artId)
            }
            R.id.tv_news_to_msg -> { // 去评论。
//                replay()
                // 滑动到看评论的地方
//                binding.homeRvContent.smoothScrollToPosition(1)

            }
            R.id.tv_news_to_share, R.id.iv_more -> {
                newsDetailData?.let {
                    HomeShareModel.shareDialog(
                        this,
                        1,
                        it.shares,
                        ReportDislikeBody(1, it.artId.toString()),
                        null,
                        it.authors.nickname
                    )
                }
            }
        }
    }

    private fun replay() {
        val replyDialog = ReplyDialog(this, object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                viewModel.addNewsComment(artId, content)
            }
        })
        replyDialog.show()
    }
}