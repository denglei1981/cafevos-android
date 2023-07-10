package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toastShow
import com.changanford.common.web.ShareViewModule
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.CommentListBean
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityNewsDetailsBinding
import com.changanford.home.databinding.LayoutHeadlinesHeaderNewsDetailBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter
import com.changanford.home.news.adapter.NewsAdsListAdapter
import com.changanford.home.news.adapter.NewsRecommendListAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.util.LoginUtil
import com.changanford.home.widget.ReplyDialog
import com.changanford.home.widget.TopSmoothScroller
import com.changanford.home.widget.loadmore.CustomLoadMoreView
import com.google.android.material.button.MaterialButton
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

/**
 *  图文详情。。。
 * */

class NewsDetailFragment : BaseFragment<ActivityNewsDetailsBinding, NewsDetailViewModel>(),
    View.OnClickListener {

    val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
    var checkPosition: Int = -1

    private lateinit var artId: String
    private var isShowFollow = false
    private val homeNewsCommentAdapter: HomeNewsCommentAdapter by lazy {
        HomeNewsCommentAdapter(this)
    }

    private val newsRecommendListAdapter: NewsRecommendListAdapter by lazy {
        NewsRecommendListAdapter()
    }

    private val newsAdsListAdapter: NewsAdsListAdapter by lazy {
        NewsAdsListAdapter()
    }

    companion object {
        fun newInstance(artId: String): NewsDetailFragment {
            val fg = NewsDetailFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.NEWS_ART_ID, artId)
            fg.arguments = bundle
            return fg
        }
    }

    //HTML文本
    private val webHelper by lazy {
        CustomWebHelper(requireActivity(), inflateHeader.wvContent)
    }

    var llInfoBottom: Int = 0
    private val customLoadMoreView: CustomLoadMoreView by lazy {
        CustomLoadMoreView()
    }
    private lateinit var shareViewModule: ShareViewModule //分享
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, requireActivity())
        binding.pbRecyclerview.layoutManager = linearLayoutManager
        binding.pbRecyclerview.adapter = homeNewsCommentAdapter
        addHeaderView()
        binding.llComment.tvSpeakSomething.setOnClickListener(this)
        homeNewsCommentAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getNewsCommentList(bizId = artId, true)
        }
        homeNewsCommentAdapter.loadMoreModule.loadMoreView = customLoadMoreView
        homeNewsCommentAdapter.setOnItemClickListener { _, view, position ->
            val commentBean = homeNewsCommentAdapter.getItem(position)
            if (commentBean.typeNull == 1) {
                return@setOnItemClickListener
            }
            val bundle = Bundle()
            bundle.putString("groupId", commentBean.groupId)
            bundle.putInt("type", 1)// 1 资讯 2 帖子
            bundle.putString("bizId", artId)
            startARouter(ARouterCirclePath.AllReplyActivity, bundle)
            checkPosition = position
        }
        binding.layoutTitle.ivBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.layoutTitle.ivMore.setOnClickListener(this)
        llInfoBottom = binding.layoutTitle.llAuthorInfo.bottom
        binding.layoutTitle.llAuthorInfo.setOnClickListener {
            JumpUtils.instans!!.jump(35, newsDetailData?.userId.toString())
        }

        shareViewModule = createViewModel(ShareViewModule::class.java)
    }

    override fun initData() {
        artId = arguments?.getString(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            if (!TextUtils.isEmpty(artId)) {
                viewModel.getNewsDetail(artId)
                viewModel.getNewsCommentList(artId, false)
                viewModel.getArtAdditional(artId)
            } else {
                toastShow("没有该资讯类型")
            }
            bus()
        }
    }

    private val inflateHeader: LayoutHeadlinesHeaderNewsDetailBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_headlines_header_news_detail,
            binding.pbRecyclerview,
            false
        )
    }

    private fun addHeaderView() {
        homeNewsCommentAdapter.addHeaderView(inflateHeader.root)
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
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this, {
            val bean = homeNewsCommentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            homeNewsCommentAdapter.notifyItemChanged(checkPosition + 1)
        })
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(requireContext())
        setFollowState.setFollowState(btnFollow, authors)
    }


    var newsDetailData: NewsDetailData? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors
        if (author.authorId != MConstant.userId) {
            isShowFollow = true
//            binding.layoutTitle.btFollow.visibility = View.VISIBLE
            inflateHeader.btFollow.visibility = View.VISIBLE
        } else {
            isShowFollow = false
//            binding.layoutTitle.btFollow.visibility = View.INVISIBLE
            inflateHeader.btFollow.visibility = View.INVISIBLE
        }
        GlideUtils.loadBD(author.avatar, inflateHeader.ivAvatar)
        GlideUtils.loadBD(author.avatar, binding.layoutTitle.ivAvatar)
        binding.layoutTitle.tvAuthor.text = author.nickname
        setFollowState(inflateHeader.btFollow, author)
        setFollowState(binding.layoutTitle.btFollow, author)
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvTitle.text = newsDetailData.title
        inflateHeader.tvTime.text = newsDetailData.timeStr
        binding.layoutTitle.tvTime.text = newsDetailData.timeStr
        if (!TextUtils.isEmpty(newsDetailData.content)) {
            webHelper.loadDataWithBaseURL(newsDetailData.content)
        }
        try {
            if (TextUtils.isEmpty(newsDetailData.specialTopicTitle)) {
                inflateHeader.llSpecial.visibility = View.GONE
            }
            inflateHeader.tvTopicName.text = newsDetailData.specialTopicTitle
        } catch (e: Exception) {
            e.printStackTrace()
        }
        inflateHeader.llSpecial.setOnClickListener {// 跳转到专题详情。
            if (newsDetailData.specialTopicId > 0) {
                JumpUtils.instans?.jump(8, newsDetailData.specialTopicId.toString())
            }
        }
        inflateHeader.ivAvatar.setOnClickListener {
            JumpUtils.instans!!.jump(35, newsDetailData.userId.toString())
        }
        inflateHeader.tvAuthor.setOnClickListener {
            JumpUtils.instans!!.jump(35, newsDetailData.userId.toString())
        }
        inflateHeader.btFollow.setOnClickListener {
            if (LoginUtil.isLongAndBindPhone()) {
                followAction()
            }
        }
        binding.layoutTitle.btFollow.setOnClickListener {
            if (LoginUtil.isLongAndBindPhone()) {
                followAction()
            }
        }

        binding.llComment.tvNewsToLike.setPageTitleText(newsDetailData.getLikeCount())
        binding.llComment.tvNewsToCollect.setPageTitleText(newsDetailData.getCollectCount())
        binding.llComment.tvNewsToShare.setPageTitleText(newsDetailData.getShareCount())
        binding.llComment.tvNewsToMsg.setPageTitleText(newsDetailData.getCommentCount())
        binding.llComment.tvNewsToLike.setOnClickListener(this)
        binding.llComment.tvNewsToShare.setOnClickListener(this)
        binding.llComment.tvNewsToMsg.setOnClickListener(this)
        binding.llComment.tvNewsToCollect.setOnClickListener(this)
        if (newsDetailData.isLike == 0) {
            binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_unlike, false)
        } else {
            binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_like, false)
        }
        if (newsDetailData.isCollect == 0) {
            binding.llComment.tvNewsToCollect.setThumb(R.drawable.icon_home_bottom_uncollect, false)
        } else {
            binding.llComment.tvNewsToCollect.setThumb(
                R.drawable.icon_home_bottom_collection,
                false
            )
        }
        addScrollListener()
    }

    private fun addScrollListener() {
        binding.pbRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position: Int = linearLayoutManager.findFirstVisibleItemPosition()
                val firstVisiableChildView =
                    linearLayoutManager.findViewByPosition(position) as View
                val itemHeight = firstVisiableChildView.height
                val scrollHeight = position * itemHeight - firstVisiableChildView.top
                binding.layoutTitle.llAuthorInfo.visibility =
                    if (scrollHeight > llInfoBottom) View.VISIBLE else View.GONE //如果滚动超过用户信息一栏，显示标题栏中的用户头像和昵称
                binding.layoutTitle.btFollow.visibility =
                    if (scrollHeight > llInfoBottom && isShowFollow) View.VISIBLE else View.GONE //如果滚动超过用户信息一栏，显示标题栏中的用户头像和昵称

            }
        })
    }

    var tips = ""
    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
            } else {
                toastShow(it.message)
            }
        })
        viewModel.commentsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    homeNewsCommentAdapter.loadMoreModule.loadMoreComplete()
                    it.data.dataList?.let { it1 -> homeNewsCommentAdapter.addData(it1) }
                } else {
                    if (it.data.dataList == null || it.data.dataList?.size!! <= 0) {
                        val commentListBean = CommentListBean(typeNull = 1)
                        val comList = arrayListOf(commentListBean)
                        homeNewsCommentAdapter.setList(comList)
                        tips = "暂无评论~"
                    } else {
                        tips = ""
                        homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                    }
                }
                if (it.data.dataList == null || it.data.dataList?.size!! < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    if (tips == "暂无评论~") {
                        homeNewsCommentAdapter.loadMoreModule.loadMoreEnd(true)
                    }
                    homeNewsCommentAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                toastShow(it.message)
            }
        })
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
        viewModel.recommendNewsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data != null) {
                    if (it.data.recommendArticles != null && it.data.recommendArticles?.size!! > 0) {
                        inflateHeader.flRecommend.visibility = View.VISIBLE
                        newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                    } else {
                        inflateHeader.flRecommend.visibility = View.GONE
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
        })
        viewModel.followLiveData.observe(this, Observer {
            try {
                if (it.isSuccess) {
                    isNeedNotify = true
                    newsDetailData?.let { it1 -> surefollow(it1, followType) }
                } else {
                    toastShow(it.message)
                    newsDetailData?.let { na ->
                        val followType = na.authors.isFollow
                        na.authors.isFollow = if (followType == 1) 2 else 1
                        setFollowState(inflateHeader.btFollow, na.authors)
                        setFollowState(binding.layoutTitle.btFollow, na.authors)
                    }
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
//                shareViewModule.shareBack(newsDetailData?.shares)
            }
        })
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this, {
            newsDetailData?.shareCount?.plus(1)?.let {
                newsDetailData?.shareCount = it
                binding.llComment.tvNewsToShare.setPageTitleText(newsDetailData?.getShareCount())
            }
        })
    }

    private fun setCommentCount() {
        // 评论成功自增1
        val commentCount = newsDetailData?.commentCount?.plus(1)
        binding.llComment.tvNewsToMsg.setPageTitleText(
            CountUtils.formatNum(
                commentCount.toString(),
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
                binding.llComment.tvNewsToCollect.setThumb(
                    R.drawable.icon_home_bottom_collection,
                    true
                )
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
                binding.llComment.tvNewsToCollect.setThumb(
                    R.drawable.icon_home_bottom_uncollect,
                    false
                )
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
        binding.llComment.tvNewsToCollect.setPageTitleText(
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
                binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_like, true)
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
                binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_unlike, false)
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
        binding.llComment.tvNewsToLike.setPageTitleText(
            CountUtils.formatNum(
                likesCount.toString(),
                false
            ).toString()
        )
    }

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

    var followType = 0

    // 关注或者取消
    private fun followAction() {
        newsDetailData?.let {
            followType = it.authors.isFollow
            followType = if (followType == 1) 2 else 1
            cancel(followId = it.userId, followType)
        }
    }

    //followType(1、关注 2、取消关注)
    private fun surefollow(newsData: NewsDetailData, followType: Int) {
        newsData.authors.isFollow = followType
        setFollowState(binding.layoutTitle.btFollow, newsData.authors)
        setFollowState(inflateHeader.btFollow, newsData.authors)
        when (followType) {
            1 -> {
                GIOUtils.followClick(newsData.authors.authorId, newsData.authors.nickname, "资讯详情页")
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
//        if (LoginUtil.isLongAndBindPhone()) {
//            startARouter(ARouterMyPath.SignUI)
//            return
//        }
        when (v.id) {
            R.id.tv_speak_something -> {
                if (LoginUtil.isLongAndBindPhone()) {
                    replay()
                }
            }
            R.id.tv_news_to_like -> {
                // 这里要防抖？
                // 无论成功与否，先改状态?
                // 获取当前对象喜欢与否的状态。
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.actionLike(artId)
                }

            }
            R.id.tv_news_to_collect -> {
                // 收藏
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.addCollect(artId)
                }

            }
            R.id.tv_news_to_msg -> { // 去评论。
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
            R.id.tv_news_to_share -> {
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

    fun smooth() {// todo  没有评论呢？
        val smoothScroller = TopSmoothScroller(requireContext())
        smoothScroller.targetPosition = 1//要滑动到的位置
        linearLayoutManager.startSmoothScroll(smoothScroller)
    }

    private fun replay() {
        val replyDialog = ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                viewModel.addNewsComment(artId, content)
            }
        })
        replyDialog.show()
    }

    override fun onResume() {
        webHelper.onResume()
        super.onResume()
    }

    override fun onPause() {
        webHelper.onPause()
        super.onPause()
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
        webHelper.onDestroy()
        super.onDestroy()
    }

}