package com.changanford.home.news.activity

import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityNewsPicDetailsBinding
import com.changanford.home.news.adapter.NewsPicDetailsBannerAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.dialog.CommentPicsDialog
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.widget.ReplyDialog
import com.google.android.material.button.MaterialButton
import com.gyf.immersionbar.ImmersionBar
import com.zhpan.bannerview.constants.IndicatorGravity

/**
 *  图片详情。
 * */
@Route(path = ARouterHomePath.NewsPicsActivity)
class NewsPicsActivity : BaseActivity<ActivityNewsPicDetailsBinding, NewsDetailViewModel>(),
    View.OnClickListener {

    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.white)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutHeader.conHomeBar, this)
        ImmersionBar.with(this).statusBarColor(R.color.white).init()
        binding.layoutHeader.ivMore.setOnClickListener(this)
        binding.layoutHeader.ivBack.setOnClickListener { onBackPressed() }
    }

    private lateinit var artId: String
    override fun initData() {
        artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            if (!TextUtils.isEmpty(artId)) {
                viewModel.getNewsDetail(artId)
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
                showBanner(it.data)
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
                isNeedNotify = true
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
                isNeedNotify = true
                setLikeState()
            } else {// 网络原因操作失败了。
                ToastUtils.showShortToast(it.message, this)
                setLikeState()
            }
        })
        viewModel.followLiveData.observe(this, Observer {})

        viewModel.collectLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isSuccess) {
                    setCollection()
                }
            }
        })

        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                shareBackUpHttp(this, newsDetailData?.shares)
            }
        })
    }

    var mCommentDialog: CommentPicsDialog? = null
    private fun showCommentDialog() {
        newsDetailData?.let {
            mCommentDialog = CommentPicsDialog(object :
                CommentPicsDialog.CommentCountInterface {
                override fun commentCount(count: Int) {
                }
            }, this)
            mCommentDialog!!.bizId = newsDetailData?.artId.toString()
            mCommentDialog!!.show(supportFragmentManager, "commentShortVideoDialog")
        }

    }

    private fun showBanner(newsDetailData: NewsDetailData) {
        val newsBannerAdapter = NewsPicDetailsBannerAdapter()
        binding.bViewpager.setAdapter(newsBannerAdapter)
            .setCanLoop(true)
            .setAutoPlay(true)
            .setIndicatorGravity(IndicatorGravity.END)
            .setIndicatorVisibility(View.GONE)
            .setScrollDuration(500)
            .registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tvPicsNum.text = "${position + 1}/${newsDetailData.imageTexts.size}"
                    binding.homeTvContent.text = newsDetailData.imageTexts[position].description
                }
            })
        binding.bViewpager.create(newsDetailData.imageTexts)
        binding.tvPicsNum.text = "1/${newsDetailData.imageTexts.size}"
        binding.homeTvContent.text = newsDetailData.imageTexts[0].description
        binding.homeTvContent.movementMethod = ScrollingMovementMethod.getInstance()

    }


    var newsDetailData: NewsDetailData? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors
        GlideUtils.loadBD(author.avatar, binding.layoutHeader.ivAvatar)
        binding.layoutHeader.tvAuthor.text = author.nickname
        setFollowState(binding.layoutHeader.btnFollow, author)
        binding.tvHomeTitle.text = newsDetailData.title
        binding.homeTvContent.text = newsDetailData.getShowContent()
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
        binding.layoutHeader.ivAvatar.setOnClickListener {
            JumpUtils.instans?.jump(35, newsDetailData.userId)
        }
        binding.llComment.tvNewsToLike.setPageTitleText(newsDetailData.getLikeCount())

        binding.llComment.tvNewsToShare.setPageTitleText(newsDetailData.getShareCount())
        binding.llComment.tvNewsToMsg.text = newsDetailData.getCommentCount()
        binding.llComment.tvNewsToCollect.setPageTitleText(newsDetailData.getCollectCount())
        binding.llComment.tvNewsToLike.setOnClickListener(this)
        binding.llComment.tvNewsToShare.setOnClickListener(this)
        binding.llComment.tvNewsToMsg.setOnClickListener(this)
        binding.llComment.tvSpeakSomething.setOnClickListener(this)
        binding.llComment.tvNewsToCollect.setOnClickListener(this)
        if (newsDetailData.isLike == 0) {
            binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_like_white, false)
        } else {
            binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_like, false)
        }
        if (newsDetailData.isCollect == 0) {
            binding.llComment.tvNewsToCollect.setThumb(
                R.drawable.icon_home_bottom_collection_white,
                false
            )
        } else {
            binding.llComment.tvNewsToCollect.setThumb(
                R.drawable.icon_home_bottom_collection,
                false
            )
        }
    }

    private fun setCommentCount() {
        // 评论成功自增1
        val commentCount = newsDetailData?.commentCount?.plus(1)
        binding.llComment.tvNewsToMsg.text =
            CountUtils.formatNum(commentCount.toString(), false).toString()

    }

    private fun setCollection() {
        var collectCount = newsDetailData?.collectCount
        when (newsDetailData?.isCollect) {
            0 -> {
                newsDetailData?.isCollect = 1
                collectCount = newsDetailData?.collectCount?.plus(1)
                binding.llComment.tvNewsToCollect.setThumb(
                    R.drawable.icon_home_bottom_collection,
                    true
                )
            }
            1 -> {
                newsDetailData?.isCollect = 0
                collectCount = newsDetailData?.collectCount?.minus(1)
                binding.llComment.tvNewsToCollect.setThumb(
                    R.drawable.icon_home_bottom_collection_white,
                    false
                )
            }
        }
        if (collectCount != null) {
            newsDetailData?.collectCount = collectCount
        }
        binding.llComment.tvNewsToCollect.setPageTitleText(
            CountUtils.formatNum(
                collectCount.toString(),
                false
            ).toString()
        )
    }

    private fun setLikeState() { //设置是否喜欢文章。
        var likesCount = newsDetailData?.likesCount
        when (newsDetailData?.isLike) {
            0 -> {
                newsDetailData?.isLike = 1
                likesCount = newsDetailData?.likesCount?.plus(1)
                binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_like, true)
            }
            1 -> {
                newsDetailData?.isLike = 0
                likesCount = newsDetailData?.likesCount?.minus(1)
                binding.llComment.tvNewsToLike.setThumb(
                    R.drawable.icon_home_bottom_like_white,
                    false
                )
            }
        }
        if (likesCount != null) {
            newsDetailData?.likesCount = likesCount
        }
        binding.llComment.tvNewsToLike.setPageTitleText(
            CountUtils.formatNum(
                likesCount.toString(),
                false
            ).toString()
        )
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(this)
        setFollowState.setFollowState(btnFollow, authors)
    }

    // 关注或者取消
    private fun followAction() {
        newsDetailData?.let {
            var followType = it.authors.isFollow
            followType = when (followType) {

                1 -> {
                    2
                }
                else -> {
                    1
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
                viewModel.actionLike(artId)
            }
            R.id.tv_news_to_msg -> { // 去评论。
//                replay()
                // 滑动到看评论的地方
//                binding.homeRvContent.smoothScrollToPosition(1)
                showCommentDialog()
            }

            R.id.tv_news_to_collect -> {
                // 收藏
                viewModel.addCollect(artId)
            }
            R.id.tv_news_to_share -> {
                newsDetailData?.let {
                    HomeShareModel.shareDialog(
                        this,
                        0,
                        it.shares,
                        null,
                        null,
                        it.authors.nickname
                    )
                }

            }
            R.id.iv_more -> {
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

    var isNeedNotify: Boolean = false //  是否需要通知，上个界面。。
    override fun onResume() {
        super.onResume()
        try {
            binding.bViewpager.startLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            binding.bViewpager.stopLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        if (isNeedNotify) {
            newsDetailData?.let {
                var infoDetailsChangeData = InfoDetailsChangeData(
                    it.commentCount,
                    it.likesCount,
                    it.authors.isFollow,
                    it.isLike
                )
                LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
                    .postValue(infoDetailsChangeData)
            }
        }
        super.onDestroy()
        try {
            binding.bViewpager.stopLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}