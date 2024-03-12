package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityNewsPicDetailsBinding
import com.changanford.home.news.activity.InfoDetailActivity
import com.changanford.home.news.adapter.NewsAdsListAdapter
import com.changanford.home.news.adapter.NewsPicDetailsBannerAdapter
import com.changanford.home.news.adapter.NewsRecommendListAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.dialog.CommentPicsDialog
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.util.LoginUtil
import com.gyf.immersionbar.ImmersionBar
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.constants.PageStyle
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

/**
 *  图片详情。
 * */

class NewsPicsFragment : BaseFragment<ActivityNewsPicDetailsBinding, NewsDetailViewModel>(),
    View.OnClickListener {

    private val newsRecommendListAdapter: NewsRecommendListAdapter by lazy {
        NewsRecommendListAdapter()
    }

    private val newsAdsListAdapter: NewsAdsListAdapter by lazy {
        NewsAdsListAdapter()
    }

    override fun initView() {
        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutHeader.conHomeBar, requireActivity())
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true).init()
        binding.layoutHeader.ivMore.setOnClickListener(this)
        binding.layoutHeader.ivBack.setOnClickListener { requireActivity().finish() }

        binding.rvRelate.adapter = newsRecommendListAdapter
        binding.rvAds.adapter = newsAdsListAdapter
        newsRecommendListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = newsRecommendListAdapter.getItem(position)
                if (item.authors != null) {
                    JumpUtils.instans?.jump(2, item.artId)
                } else {
                    toastShow("没有作者")
                }
            }
        })
    }

    companion object {
        fun newInstance(artId: String): NewsPicsFragment {
            val fg = NewsPicsFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.NEWS_ART_ID, artId)
            fg.arguments = bundle
            return fg
        }
    }

    private lateinit var artId: String
    override fun initData() {
        artId = arguments?.getString(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            if (!TextUtils.isEmpty(artId)) {
//                viewModel.getNewsDetail(artId)
                viewModel.getNewsCommentList(artId, false)
                viewModel.getArtAdditional(artId)
            } else {
                toastShow("没有该资讯类型")
            }
        }
        val infoDetailActivity = activity as InfoDetailActivity
        infoDetailActivity.getNewDetailBean()?.let {
            showHeadInfo(it)
            showBanner(it)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
                showBanner(it.data)
            } else {
                toastShow(it.message)
            }
        })
        viewModel.commentsLiveData.observe(this) {
            if (it.isSuccess) {
                if (it.isLoadMore) {
//                    homeNewsCommentAdapter.addData(it.data.dataList)
                } else {
//                    homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                }
            } else {
//                ToastUtils.showShortToast(it.message, this)
                toastShow(it.message)
            }
        }
        viewModel.recommendNewsLiveData.observe(this) {
            if (it.isSuccess) {
                if (it.data != null) {
                    if (it.data.recommendArticles != null && it.data.recommendArticles?.size!! > 0) {
                        binding.flRecommend.visibility = View.VISIBLE
                        newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                    } else {
                        binding.flRecommend.visibility = View.GONE
                        binding.homeGrayLine.isVisible = false
                    }
                    if (it.data.ads != null && it.data.ads?.size!! > 0) {
                        binding.rvAds.visibility = View.VISIBLE
                        newsAdsListAdapter.setNewInstance(it.data.ads)
                    } else {
                        binding.rvAds.visibility = View.GONE
                    }
                } else {// 隐藏热门推荐。
                    binding.flRecommend.visibility = View.GONE
                }
            }
        }
        viewModel.commentSateLiveData.observe(this, Observer {
            if (it.isSuccess) {
                isNeedNotify = true

                toastShow("评论成功")
                // 评论数量加1. 刷新评论。
                viewModel.getNewsCommentList(artId, false)
                setCommentCount()
                val item = viewModel.newsDetailLiveData.value
                item?.data?.let {
                    GIOUtils.commentSuccessInfo(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            } else {
                toastShow(it.message)
            }

        })
        viewModel.actionLikeLiveData.observe(this, Observer {
            try {
                if (it.isSuccess) {
                    isNeedNotify = true
                    setLikeState()
                } else {// 网络原因操作失败了。
                    toastShow(it.message)
                    setLikeState()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
        viewModel.followLiveData.observe(this, Observer {
            try {
                if (it.isSuccess) {
                    isNeedNotify = true
                    newsDetailData?.let { it1 -> surefollow(it1, followType) }
                } else {
                    toastShow(it.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        viewModel.collectLiveData.observe(this, Observer {
            try {
                if (it.isSuccess) {
                    setCollection()
                } else {
                    toastShow(it.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                ToastUtils.reToast(R.string.str_shareSuccess)
                shareBackUpHttp(this, newsDetailData?.shares)
            }
        })

        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this) {
            newsDetailData?.shareCount?.plus(1)?.let {
                newsDetailData?.shareCount = it
                binding.llComment.tvShareNum.text = (newsDetailData?.getShareCount())
            }
        }
    }

    var mCommentDialog: CommentPicsDialog? = null
    private fun showCommentDialog() {
        newsDetailData?.let {
            mCommentDialog = CommentPicsDialog(object :
                CommentPicsDialog.CommentCountInterface {
                override fun commentCount(count: Int) {
                }
            }, requireContext(), newsDetailData?.commentCount.toString())
            mCommentDialog!!.bizId = newsDetailData?.artId.toString()
            mCommentDialog!!.show(parentFragmentManager, "commentShortVideoDialog")
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
            .setPageStyle(PageStyle.MULTI_PAGE_SCALE)
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
        if (author.authorId != MConstant.userId) {
            binding.layoutHeader.btnFollow.visibility = View.VISIBLE
        } else {
            binding.layoutHeader.btnFollow.visibility = View.INVISIBLE
        }
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
            if (LoginUtil.isLongAndBindPhone()) {
                followAction()
            }
        }
        binding.layoutHeader.ivAvatar.setOnClickListener {
            JumpUtils.instans?.jump(35, newsDetailData.userId)
        }
        binding.llComment.tvShareNum.isVisible = true
        binding.llComment.tvLikeNum.text = (newsDetailData.getLikeCount())

        binding.llComment.tvShareNum.text = (newsDetailData.getShareCount())
        binding.llComment.tvCommentNum.text = newsDetailData.getCommentCount()
        binding.llComment.tvCollectionNum.text = (newsDetailData.getCollectCount())
        binding.llComment.tvTalk.setOnClickListener(this)
        binding.llComment.llLike.setOnClickListener(this)
        binding.llComment.tvCommentNum.setOnClickListener(this)
        binding.llComment.llCollection.setOnClickListener(this)
        binding.llComment.tvShareNum.setOnClickListener(this)
        if (newsDetailData.isLike == 0) {
            binding.llComment.ivLike.setImageResource(com.changanford.circle.R.mipmap.circle_no_like_image)
        } else {
            binding.llComment.ivLike.setImageResource(com.changanford.circle.R.mipmap.circle_like_image)
        }
        if (newsDetailData.isCollect == 0) {
            binding.llComment.ivCollection.setImageResource(com.changanford.circle.R.mipmap.circle_no_collection_image)
        } else {
            binding.llComment.ivCollection.setImageResource(com.changanford.circle.R.mipmap.circle_collection_image)
        }
    }

    private fun setCommentCount() {
        // 评论成功自增1
        val commentCount = newsDetailData?.commentCount?.plus(1)
        binding.llComment.tvCommentNum.text =
            CountUtils.formatNum(commentCount.toString(), false).toString()
    }

    private fun setCollection() {
        var collectCount = newsDetailData?.collectCount
        val item = viewModel.newsDetailLiveData.value
        when (newsDetailData?.isCollect) {
            0 -> {
                newsDetailData?.isCollect = 1
                collectCount = newsDetailData?.collectCount?.plus(1)
                binding.llComment.ivCollection.setImageResource(com.changanford.circle.R.mipmap.circle_collection_image)
                item?.data?.let {
                    GIOUtils.collectSuccessInfo(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }

            1 -> {
                newsDetailData?.isCollect = 0
                collectCount = newsDetailData?.collectCount?.minus(1)
                binding.llComment.ivCollection.setImageResource(com.changanford.circle.R.mipmap.circle_no_collection_image)
                item?.data?.let {
                    GIOUtils.cancelCollectSuccessInfo(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }
        }
        if (collectCount != null) {
            newsDetailData?.collectCount = collectCount
        }
        binding.llComment.tvCollectionNum.text = (
                CountUtils.formatNum(
                    collectCount.toString(),
                    false
                ).toString()
                )
    }

    private fun setLikeState() { //设置是否喜欢文章。
        var likesCount = newsDetailData?.likesCount
        val item = viewModel.newsDetailLiveData.value
        when (newsDetailData?.isLike) {
            0 -> {
                newsDetailData?.isLike = 1
                likesCount = newsDetailData?.likesCount?.plus(1)
                binding.llComment.ivLike.setImageResource(com.changanford.circle.R.mipmap.circle_like_image)
                item?.data?.let {
                    GIOUtils.infoLickClick(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }

            1 -> {
                newsDetailData?.isLike = 0
                likesCount = newsDetailData?.likesCount?.minus(1)
                binding.llComment.ivLike.setImageResource(com.changanford.circle.R.mipmap.circle_no_like_image)
                item?.data?.let {
                    GIOUtils.cancelInfoLickClick(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }
        }
        if (likesCount != null) {
            newsDetailData?.likesCount = likesCount
        }
        binding.llComment.tvLikeNum.text = (
                CountUtils.formatNum(
                    likesCount.toString(),
                    false
                ).toString()
                )
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(requireActivity())
        setFollowState.setFollowState(btnFollow, authors)
    }

//    // 关注或者取消
//    private fun followAction() {
//        newsDetailData?.let {
//            var followType = it.authors.isFollow
//            followType = if (followType == 1) 2 else 1
//            it.authors.isFollow = followType;
//            setFollowState(binding.layoutHeader.btnFollow, it.authors)
//            viewModel.followOrCancelUser(it.userId, followType)
//        }
//    }

    var followType = 0

    // 1 关注 2 取消关注
    fun cancel(followId: String, type: Int) {
        if (MineUtils.getBindMobileJumpDataType(true)) {
            return
        }
        if (type == 1) {
            viewModel.followOrCancelUser(followId, type)
        } else {
            QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_two_btn)
                .config(
                    QuickPopupConfig()
                        .gravity(Gravity.CENTER)
                        .withClick(R.id.btn_comfir, View.OnClickListener {
                            viewModel.followOrCancelUser(followId, type)
                        }, true)
                        .withClick(R.id.btn_cancel, View.OnClickListener {
                        }, true)
                )
                .show()
        }
    }

    // 关注或者取消
    private fun followAction() {
        newsDetailData?.let {
            followType = it.authors.isFollow
            followType = if (followType == 1) 2 else 1
            cancel(followId = it.userId, followType)
        }
    }

    private fun surefollow(newsData: NewsDetailData, followType: Int) {
        newsData.authors.isFollow = followType
        setFollowState(binding.layoutHeader.btnFollow, newsData.authors)
        if (followType == 1) {
            "已关注".toast()
        } else {
            "取消关注".toast()
        }
//        setFollowState(binding.layoutHeader.btnFollow, newsData.authors)
        when (followType) {
            1 -> {
                GIOUtils.followClick(
                    newsData.authors.authorId,
                    newsData.authors.nickname,
                    "资讯详情页"
                )
            }

            2 -> {
                GIOUtils.cancelFollowClick(
                    newsData.authors.authorId,
                    newsData.authors.nickname,
                    "资讯详情页"
                )
            }
        }
//        viewModel.followOrCancelUser(newsData.userId, followType)
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.tv_talk -> {
                if (LoginUtil.isLongAndBindPhone()) {
                    replay()
                }
            }

            R.id.ll_like -> {
                // 这里要防抖？
                // 无论成功与否，先改状态?
                // 获取当前对象喜欢与否的状态。
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.actionLike(artId)
                }

            }

            R.id.tv_comment_num -> { // 去评论。
                showCommentDialog()
                val item = viewModel.newsDetailLiveData.value
                item?.data?.let {
                    GIOUtils.clickCommentInfo(
                        "资讯详情页",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }

            R.id.ll_collection -> {
                // 收藏
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.addCollect(artId)
                }

            }

            R.id.tv_share_num -> {
                newsDetailData?.let {
                    HomeShareModel.shareDialog(
                        requireActivity(),
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
                        requireActivity(),
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
        val replyDialog = ReplyDialog(requireActivity(), object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                viewModel.addNewsComment(artId, content)
            }
        })
        replyDialog.show()
    }

    var isNeedNotify: Boolean = false //  是否需要通知，上个界面。。
    override fun onDestroy() {
        if (isNeedNotify) {
            newsDetailData?.let {
                val infoDetailsChangeData = InfoDetailsChangeData(
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

    }
}