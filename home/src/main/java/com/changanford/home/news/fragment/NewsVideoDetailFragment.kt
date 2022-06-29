package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseLoadSirFragment
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
import com.changanford.common.util.dk.cache.DKPlayerHelperBig
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.CommentListBean
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.IncludeHomePicVideoNewsContentBinding
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
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
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
    private val homeNewsCommentAdapter: HomeNewsCommentAdapter by lazy {
        HomeNewsCommentAdapter(this)
    }

    private val newsAdsListAdapter: NewsAdsListAdapter by lazy {
        NewsAdsListAdapter()
    }
    private val customLoadMoreView: CustomLoadMoreView by lazy {
        CustomLoadMoreView()
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
        StatusBarUtil.setStatusBarMarginTop(binding.homesDkVideo, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.ivBack, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.ivMore, requireActivity())
        ImmersionBar.with(this)
            .statusBarColor(R.color.black)
            .statusBarDarkFont(true)
            .autoStatusBarDarkModeEnable(true, 0.5f)
            .init()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.homeRvContent.layoutManager = linearLayoutManager
        homeNewsCommentAdapter.loadMoreModule.loadMoreView = customLoadMoreView
        binding.homeRvContent.adapter = homeNewsCommentAdapter
        homeNewsCommentAdapter.loadMoreModule.setOnLoadMoreListener{
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
        binding.llComment.tvSpeakSomething.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        homeNewsCommentAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val commentBean = homeNewsCommentAdapter.getItem(position)
                if (commentBean.typeNull == 1) {
                    return
                }
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
            viewModel.getNewsDetail(artId)
            viewModel.getNewsCommentList(artId, false)
            viewModel.getArtAdditional(artId)
        } else {
            toastShow("没有该资讯类型")
        }
        bus()
    }

    private val inflateHeader: IncludeHomePicVideoNewsContentBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.include_home_pic_video_news_content,
            binding.homeRvContent,
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
                    if (it.data.dataList.size <= 0) {
                        val commentListBean = CommentListBean(typeNull = 1)
                        val comList = arrayListOf(commentListBean)
                        homeNewsCommentAdapter.setList(comList)
                        tips = "暂无评论~"
                    } else {
                        tips = ""
                        homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                    }
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
        viewModel.recommendNewsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data != null) {
                    if (it.data.recommendArticles != null && it.data.recommendArticles?.size!! > 0) {
                        newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                        inflateHeader.grRecommend.visibility = View.VISIBLE
                    }else{
                        inflateHeader.grRecommend.visibility=View.GONE
                    }
                    if (it.data.ads != null && it.data.ads?.size!! > 0) {
                        inflateHeader.rvAds.visibility = View.VISIBLE
                        newsAdsListAdapter.setNewInstance(it.data.ads)
                    }else{
                        inflateHeader.rvAds.visibility=View.GONE
                    }
                }
            }
        })
        viewModel.commentSateLiveData.observe(this, Observer {
            if (it.isSuccess) {

                toastShow("评论成功")
                // 评论数量加1. 刷新评论。
                viewModel.getNewsCommentList(artId, false)
                setCommentCount()
            } else {
                toastShow(it.message)
            }

        })
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
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this, {
            newsDetailData?.shareCount?.plus(1)?.let {
                newsDetailData?.shareCount=it
                binding.llComment.tvNewsToShare.setPageTitleText(newsDetailData?.getShareCount())
            }
        })
    }

    var newsDetailData: NewsDetailData? = null

    //web帮助类
    private var webHelper: CustomWebHelper? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors
        GlideUtils.loadBD(author.avatar, inflateHeader.ivAvatar)
        inflateHeader.ivAvatar.setOnClickListener {
            JumpUtils.instans?.jump(35, newsDetailData.userId)
        }
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvHomeTitle.text = newsDetailData.title


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
        binding.llComment.tvNewsToLike.setPageTitleText(
            CountUtils.formatNum(
                newsDetailData.getLikeCount(),
                false
            ).toString()
        )
        binding.llComment.tvNewsToShare.setPageTitleText(newsDetailData.getShareCount())
        binding.llComment.tvNewsToMsg.setPageTitleText(newsDetailData.getCommentCount())
        binding.llComment.tvNewsToCollect.setPageTitleText(newsDetailData.getCollectCount())
        binding.llComment.tvNewsToLike.setOnClickListener(this)
        binding.llComment.tvNewsToShare.setOnClickListener(this)
        binding.llComment.tvNewsToMsg.setOnClickListener(this)
        binding.llComment.tvNewsToCollect.setOnClickListener(this)
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
        if (TextUtils.isEmpty(newsDetailData.content)) {
            inflateHeader.tvOpen.visibility = View.GONE
        }
        if (webHelper == null) {
            webHelper = CustomWebHelper(
                BaseApplication.curActivity,
                inflateHeader.webContent
            )
        }
        webHelper?.loadDataWithBaseURL(newsDetailData.content)
        inflateHeader.webContent.visibility = View.GONE
        inflateHeader.tvOpen.setOnClickListener {
            if (inflateHeader.webContent.visibility == View.GONE) {
                inflateHeader.webContent.visibility = View.VISIBLE
                inflateHeader.tvOpen.text = "收起详情"
            } else {
                inflateHeader.webContent.visibility = View.GONE
                inflateHeader.tvOpen.text = "展开详情"
            }
        }
    }

    // 关注或者取消
    // 关注或者取消
    private fun followAction() {
        newsDetailData?.let {

            followType = it.authors.isFollow

            followType = if (followType == 1) 2 else 1

            cancel(followId = it.userId,followType)


        }
    }
    private fun surefollow(newsData: NewsDetailData, followType: Int) {
        newsData.authors.isFollow = followType
        setFollowState(inflateHeader.btFollow, newsData.authors)
        setFollowState(inflateHeader.btFollow, newsData.authors)
//        viewModel.followOrCancelUser(newsData.userId, followType)
    }

    fun smooth() {// todo  没有评论呢？
        val smoothScroller = TopSmoothScroller(requireActivity())
        smoothScroller.targetPosition = 1//要滑动到的位置
        linearLayoutManager?.startSmoothScroll(smoothScroller)
    }

    var followType =0
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
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
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
            R.id.tv_news_to_collect -> {
                if (LoginUtil.isLongAndBindPhone()) {
                    viewModel.addCollect(artId)
                }

            }
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
            R.id.tv_news_to_msg -> { // 去评论。
//                replay()
                // 滑动到看评论的地方
//                binding.homeRvContent.smoothScrollToPosition(1)
                smooth()
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
                binding.llComment.tvNewsToLike.setThumb(R.drawable.icon_home_bottom_unlike, false)
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
                    R.drawable.icon_home_bottom_uncollect,
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
            homeNewsCommentAdapter.notifyItemChanged(checkPosition+1)
        })
    }

}