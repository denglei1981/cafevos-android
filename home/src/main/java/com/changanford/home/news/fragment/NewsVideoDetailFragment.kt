package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.dk.cache.DKPlayerHelperBig
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.LayoutHeadlinesHeaderNewsDetailBinding
import com.changanford.home.news.activity.InfoDetailActivity
import com.changanford.home.news.adapter.NewsAdsListAdapter
import com.changanford.home.news.adapter.NewsRecommendListAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.util.LoginUtil
import com.changanford.home.widget.TopSmoothScroller
import com.changanford.home.widget.loadmore.CustomLoadMoreView
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

class NewsVideoDetailFragment :
    BaseLoadSirFragment<ActivityHomeNewsVideoDetailBinding, NewsDetailViewModel>(),
    View.OnClickListener {
    private lateinit var playerHelper: DKPlayerHelperBig //播放器帮助类
    private lateinit var artId: String
    var linearLayoutManager: LinearLayoutManager? = null
    var checkPosition: Int = -1

    var isNeedNotify: Boolean = false //  是否需要通知，上个界面。。
    private val newsRecommendListAdapter: NewsRecommendListAdapter by lazy {
        NewsRecommendListAdapter()
    }
    private val homeNewsCommentAdapter by lazy {
        PostDetailsCommentAdapter(this)
    }

    private val newsAdsListAdapter: NewsAdsListAdapter by lazy {
        NewsAdsListAdapter()
    }
    private val customLoadMoreView: CustomLoadMoreView by lazy {
        CustomLoadMoreView()
    }
    private val adapter by lazy { NewsAdsListAdapter() }
    private val inflateHeader: LayoutHeadlinesHeaderNewsDetailBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_headlines_header_news_detail,
            binding.homeRvContent,
            false
        )
    }

    companion object {
        fun newInstance(artId: String): NewsVideoDetailFragment {
            val fg = NewsVideoDetailFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.NEWS_ART_ID, artId)
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
//        StatusBarUtil.setStatusBarMarginTop(binding.homesDkVideo, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.ivBack, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.ivMore, requireActivity())
//        ImmersionBar.with(this)
//            .statusBarColor(R.color.black)
//            .statusBarDarkFont(true)
//            .autoStatusBarDarkModeEnable(true, 0.5f)
//            .init()
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.homeRvContent.layoutManager = linearLayoutManager
        homeNewsCommentAdapter.loadMoreModule.loadMoreView = customLoadMoreView
        binding.homeRvContent.adapter = adapter
        inflateHeader.ryComment.adapter = homeNewsCommentAdapter
        homeNewsCommentAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getNewsCommentList(artId, true)
        }
        playerHelper = DKPlayerHelperBig(requireActivity(), binding.homesDkVideo)
        binding.ivBack.setOnClickListener {
//            onBackPressed()

            backPressed {
                requireActivity().finish()
            }
//            requireActivity().finish()
        }
        addHeaderView()
        binding.llComment.tvCommentNum.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        homeNewsCommentAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val commentBean = homeNewsCommentAdapter.getItem(position)
//                if (commentBean.typeNull == 1) {
//                    return
//                }
                val bundle = Bundle()
                bundle.putString("groupId", commentBean.groupId)
                bundle.putInt("type", 1)// 1 资讯 2 帖子
                bundle.putString("bizId", artId)
                startARouter(ARouterCirclePath.AllReplyActivity, bundle)
                checkPosition = position

            }
        })
    }

    override fun initData() {
        artId = arguments?.getString(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
//            viewModel.getNewsDetail(artId)
            viewModel.getNewsCommentList(artId, false)
            viewModel.getArtAdditional(artId)
        } else {
            toastShow("没有该资讯类型")
        }
        bus()
        val infoDetailActivity = activity as InfoDetailActivity
        infoDetailActivity.getNewDetailBean()?.let {
            showHeadInfo(it)
            playVideo(it.videoUrl)
        }
    }

    private fun addHeaderView() {
        adapter.addHeaderView(inflateHeader.root)
        inflateHeader.rvRelate.adapter = newsRecommendListAdapter
        inflateHeader.rvAds.adapter = newsAdsListAdapter
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

    private fun playVideo(playUrl: String) {
        playerHelper.startPlay(GlideUtils.defaultHandleImageUrl(playUrl))
    }

    var tips = ""
    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
                playVideo(it.data.videoUrl)
            } else {
                toastShow(it.message)
            }
        })
        viewModel.commentsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    homeNewsCommentAdapter.loadMoreModule.loadMoreComplete()
                    homeNewsCommentAdapter.addData(it.data.dataList)
                } else {
//                    if (it.data.dataList.size <= 0) {
//                        val commentListBean = CommentListBean(typeNull = 1)
//                        val comList = arrayListOf(commentListBean)
//                        homeNewsCommentAdapter.setList(comList)
//                        tips = "暂无评论~"
//                    } else {
                    if (it.data.dataList.size == 0) {
                        homeNewsCommentAdapter.setEmptyView(com.changanford.circle.R.layout.circle_comment_empty_layout)
                    } else {
                        inflateHeader.tvCommentNum.isVisible = true
                        inflateHeader.tvCommentNum.text = "(${it.data.dataList.size})"
                    }
                    tips = ""
                    homeNewsCommentAdapter.setNewInstance(it.data.dataList)
//                    }
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    if (tips == "暂无评论~") {
                        homeNewsCommentAdapter.loadMoreModule.loadMoreEnd(true)
                    }
                    homeNewsCommentAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                toastShow(it.message)
            }
        })
        viewModel.recommendNewsLiveData.observe(this) {
            if (it.isSuccess) {
                if (it.data != null) {
                    if (it.data.recommendArticles != null && it.data.recommendArticles?.size!! > 0) {
                        inflateHeader.flRecommend.visibility = View.VISIBLE
                        newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                    } else {
                        inflateHeader.flRecommend.visibility = View.GONE
                        inflateHeader.vLin2.isVisible = false
                    }
                    if (it.data.ads != null && it.data.ads?.size!! > 0) {
                        inflateHeader.rvAds.visibility = View.VISIBLE
                        newsAdsListAdapter.setNewInstance(it.data.ads)
                    } else {
                        inflateHeader.rvAds.visibility = View.GONE
                    }
                } else {// 隐藏热门推荐。
                    inflateHeader.flRecommend.visibility = View.GONE
                }
            }
        }
        viewModel.commentSateLiveData.observe(this) {
            if (it.isSuccess) {

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

        }
        viewModel.actionLikeLiveData.observe(this, Observer {
            if (it.isSuccess) {
                isNeedNotify = true
                setLikeState()
//                toastShow(it.data.toString())
            } else {// 网络原因操作失败了。
                //
//                ToastUtils.showShortToast(it.message, this)
                toastShow(it.message)
                setLikeState()
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
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this) {
            newsDetailData?.shareCount?.plus(1)?.let {
                newsDetailData?.shareCount = it
                binding.llComment.tvShareNum.text = (newsDetailData?.getShareCount())
            }
        }
    }

    var newsDetailData: NewsDetailData? = null

    //web帮助类
    private var webHelper: CustomWebHelper? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors

        if (author.authorId != MConstant.userId) {
            inflateHeader.btFollow.visibility = View.VISIBLE
        } else {
            inflateHeader.btFollow.visibility = View.INVISIBLE
        }

        GlideUtils.loadBD(author.avatar, inflateHeader.ivAvatar)
        inflateHeader.ivAvatar.setOnClickListener {
            JumpUtils.instans?.jump(35, newsDetailData.userId)
        }
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvTitle.text = newsDetailData.title

        inflateHeader.tvTopicName.text = newsDetailData.specialTopicTitle
        inflateHeader.tvTime.text = newsDetailData.timeStr

        if (newsDetailData.specialTopicId > 0) {
            inflateHeader.llSpecial.visibility = View.VISIBLE
        } else {
            inflateHeader.llSpecial.visibility = View.GONE
        }


        inflateHeader.llSpecial.setOnClickListener {// 跳转到专题详情。
            JumpUtils.instans?.jump(8, newsDetailData.specialTopicId.toString())
        }
        setFollowState(inflateHeader.btFollow, author)

        inflateHeader.btFollow.setOnClickListener {
            if (LoginUtil.isLongAndBindPhone()) {
                followAction()
            }
        }
        binding.llComment.tvLikeNum.text = (
                newsDetailData.getLikeCount()
                )
        binding.llComment.tvShareNum.isVisible = true
        binding.llComment.tvShareNum.text = (newsDetailData.getShareCount())
        binding.llComment.tvCommentNum.text = (newsDetailData.getCommentCount())
        binding.llComment.tvCollectionNum.text = (newsDetailData.getCollectCount())
        binding.llComment.llLike.setOnClickListener(this)
        binding.llComment.tvShareNum.setOnClickListener(this)
        binding.llComment.tvCommentNum.setOnClickListener(this)
        binding.llComment.llCollection.setOnClickListener(this)
        binding.llComment.tvTalk.setOnClickListener(this)
        if (newsDetailData.isLike == 0) {
            binding.llComment.ivLike.setImageResource(R.mipmap.circle_no_like_image)
        } else {
            binding.llComment.ivLike.setImageResource(R.mipmap.circle_like_image)
        }
        if (newsDetailData.isCollect == 0) {
            binding.llComment.ivCollection.setImageResource(R.mipmap.circle_no_collection_image)
        } else {
            binding.llComment.ivCollection.setImageResource(R.mipmap.circle_collection_image)
        }
//        if (TextUtils.isEmpty(newsDetailData.content)) {
//            inflateHeader.tvOpen.visibility = View.GONE
//        }
        if (webHelper == null) {
            webHelper = CustomWebHelper(
                BaseApplication.curActivity,
                inflateHeader.wvContent
            )
        }
        webHelper?.loadDataWithBaseURL(newsDetailData.content)
//        inflateHeader.wvContent.visibility = View.GONE
//        inflateHeader.tvOpen.setOnClickListener {
//            if (inflateHeader.webContent.visibility == View.GONE) {
//                inflateHeader.webContent.visibility = View.VISIBLE
//                inflateHeader.tvOpen.text = "收起详情"
//            } else {
//                inflateHeader.webContent.visibility = View.GONE
//                inflateHeader.tvOpen.text = "展开详情"
//            }
//        }
    }

    // 关注或者取消
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
        setFollowState(inflateHeader.btFollow, newsData.authors)
        setFollowState(inflateHeader.btFollow, newsData.authors)
        if (followType == 1) {
            "已关注".toast()
        } else {
            "取消关注".toast()
        }
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

    fun smooth() {// todo  没有评论呢？
        val smoothScroller = TopSmoothScroller(requireActivity())
        smoothScroller.targetPosition = 1//要滑动到的位置
        linearLayoutManager?.startSmoothScroll(smoothScroller)
    }

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


    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(requireActivity())
        setFollowState.setFollowState(btnFollow, authors)
    }

    override fun onResume() {
        super.onResume()
        playerHelper.resume()
    }

    override fun onPause() {
        super.onPause()
        playerHelper.pause()
    }

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
        playerHelper.release()
    }

//    //点击系统返回需要判断是否全屏，切换全屏状态
//    private fun backPressed(back: () -> Unit) {
//        playerHelper.backPressed {
//            back()
//        }
//    }
//    fun onBackPressed() {
//        backPressed { super.onBackPressed() }
//    }
    /**
     *  有重试 重写此方法
     * */
    override fun onRetryBtnClick() {

    }

    private fun replay() {
        val replyDialog = ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                viewModel.addNewsComment(artId, content)
            }
        })
        replyDialog.show()
    }

    override fun onClick(v: View) {
//        if (MConstant.token.isEmpty()) {
//            startARouter(ARouterMyPath.SignUI)
//            return
//        }
        when (v.id) {
            R.id.ll_collection -> {
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.addCollect(artId)
                }

            }

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
//                replay()
                // 滑动到看评论的地方
//                binding.homeRvContent.smoothScrollToPosition(1)
                smooth()
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

    private fun setLikeState() { //设置是否喜欢文章。
        var likesCount = newsDetailData?.likesCount
        val item = viewModel.newsDetailLiveData.value
        when (newsDetailData?.isLike) {
            0 -> {
                newsDetailData?.isLike = 1
                likesCount = newsDetailData?.likesCount?.plus(1)
                binding.llComment.ivLike.setImageResource(R.mipmap.circle_like_image)
                item?.data?.let {
                    GIOUtils.infoLickClick(
                        "资讯详情",
                        it.specialTopicTitle,
                        it.artId.toString(),
                        it.title
                    )
                }
            }

            1 -> {
                newsDetailData?.isLike = 0
                likesCount = newsDetailData?.likesCount?.minus(1)
                binding.llComment.ivLike.setImageResource(R.mipmap.circle_no_like_image)
                item?.data?.let {
                    GIOUtils.cancelInfoLickClick(
                        "资讯详情",
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

    private fun setCollection() {
        var collectCount = newsDetailData?.collectCount
        val item = viewModel.newsDetailLiveData.value
        when (newsDetailData?.isCollect) {
            0 -> {
                newsDetailData?.isCollect = 1
                collectCount = newsDetailData?.collectCount?.plus(1)
                binding.llComment.ivCollection.setImageResource(R.mipmap.circle_collection_image)
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
                binding.llComment.ivCollection.setImageResource(R.mipmap.circle_no_collection_image)
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

    private fun setCommentCount() {
        // 评论成功自增1
        val commentCount = newsDetailData?.commentCount?.plus(1)
        binding.llComment.tvCommentNum.text = (
                CountUtils.formatNum(
                    commentCount.toString(),
                    false
                ).toString()
                )

    }

    //点击系统返回需要判断是否全屏，切换全屏状态
    fun backPressed(back: () -> Unit) {
        playerHelper.backPressed {
            back()
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_COMMENT_ITEM).observe(this, {
            if (checkPosition == -1) {
                return@observe
            }

            val bean = homeNewsCommentAdapter.getItem(checkPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.likesCount++
            } else {
                bean.likesCount--
            }
            // 有头布局。
            homeNewsCommentAdapter.notifyItemChanged(checkPosition + 1)
        })

        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                ToastUtils.reToast(R.string.str_shareSuccess)
                shareBackUpHttp(this, newsDetailData?.shares)
            }
        })

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this, {
            val bean = homeNewsCommentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            homeNewsCommentAdapter.notifyItemChanged(checkPosition + 1)
        })
    }

}