package com.changanford.home.news.activity

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.NewsValueData
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
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
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.IncludeHomePicVideoNewsContentBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter
import com.changanford.home.news.adapter.NewsRecommendListAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.widget.ReplyDialog
import com.changanford.home.widget.TopSmoothScroller
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.tools.ScreenUtils

@Route(path = ARouterHomePath.NewsVideoDetailActivity)
class NewsVideoDetailActivity :
    BaseLoadSirActivity<ActivityHomeNewsVideoDetailBinding, NewsDetailViewModel>(),
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

    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.homesDkVideo, this)
        StatusBarUtil.setStatusBarMarginTop(binding.ivBack, this)
        StatusBarUtil.setStatusBarMarginTop(binding.ivMore, this)
        ImmersionBar.with(this)
            .statusBarColor(R.color.black)
            .statusBarDarkFont(true)
            .autoStatusBarDarkModeEnable(true, 0.5f)
            .init()
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeRvContent.layoutManager = linearLayoutManager

        binding.homeRvContent.adapter = homeNewsCommentAdapter
        playerHelper = DKPlayerHelperBig(this, binding.homesDkVideo)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        addHeaderView()
        binding.llComment.tvSpeakSomething.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        homeNewsCommentAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val commentBean = homeNewsCommentAdapter.getItem(position)
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
        artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            viewModel.getNewsDetail(artId)
            viewModel.getNewsCommentList(artId, false)
            viewModel.getArtAdditional(artId)
        } else {
            ToastUtils.showShortToast("没有该资讯类型", this)
        }
        bus()
    }


    private val inflateHeader: IncludeHomePicVideoNewsContentBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.include_home_pic_video_news_content,
            binding.homeRvContent,
            false
        )
    }

    private fun addHeaderView() {
        homeNewsCommentAdapter.addHeaderView(inflateHeader.root)
        inflateHeader.rvRelate.adapter = newsRecommendListAdapter
        newsRecommendListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = newsRecommendListAdapter.getItem(position)
                if (item.authors != null) {
                    val newsValueData = NewsValueData(item.artId, item.type)
                    val values = Gson().toJson(newsValueData)
                    JumpUtils.instans?.jump(2, values)
                } else {
                    toastShow("没有作者")
                }
            }
        })

    }

    private fun playVideo(playUrl: String) {
        playerHelper.startPlay(GlideUtils.defaultHandleImageUrl(playUrl))
    }
    var footerView: View? = null
    private fun addFooter() {
        footerView = layoutInflater.inflate(R.layout.comment_no_data, binding.homeRvContent, false)
        homeNewsCommentAdapter.addFooterView(footerView!!)
    }
    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
                playVideo(it.data.videoUrl)
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
        viewModel.commentsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    homeNewsCommentAdapter.loadMoreModule.loadMoreComplete()
                    homeNewsCommentAdapter.addData(it.data.dataList)
                } else {
                    if (it.data.dataList.size <= 0) {
                        addFooter()
                    } else {
                        footerView?.let { fv ->
                            homeNewsCommentAdapter.removeFooterView(fv)
                        }
                        homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                    }
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    homeNewsCommentAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
        viewModel.recommendNewsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data != null && it.data.recommendArticles.size > 0) {
                    newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                    inflateHeader.grRecommend.visibility = View.VISIBLE
                } else {// 隐藏热门推荐。
                    inflateHeader.grRecommend.visibility = View.GONE

                }
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
                isNeedNotify = true
            } else {// 网络原因操作失败了。
                //
                ToastUtils.showShortToast(it.message, this)
                setLikeState()
            }
        })
        viewModel.followLiveData.observe(this, Observer {
            if (it.isSuccess) {
                isNeedNotify = true
            }
        })

        viewModel.collectLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isSuccess) {
                    setCollection()
                }
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
        inflateHeader.ivAvatar.setOnClickListener{
            JumpUtils.instans?.jump(35,newsDetailData.userId)
        }
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvHomeTitle.text = newsDetailData.title
        if (!TextUtils.isEmpty(newsDetailData.getPicUrl())) {
            GlideUtils.loadBD(newsDetailData.getPicUrl(), inflateHeader.ivPic)
            inflateHeader.ivPic.visibility = View.VISIBLE
        } else {
            inflateHeader.ivPic.visibility = View.GONE
        }
        inflateHeader.ivPic.setOnClickListener {

        }
        inflateHeader.tvTopicName.text = newsDetailData.specialTopicTitle
        inflateHeader.tvTime.text = newsDetailData.timeStr

        if(newsDetailData.specialTopicId>0){
            inflateHeader.llSpecial.visibility=View.VISIBLE
        }else{
            inflateHeader.llSpecial.visibility=View.GONE
        }


        inflateHeader.llSpecial.setOnClickListener {// 跳转到专题详情。
            JumpUtils.instans?.jump(8, newsDetailData.specialTopicId.toString())
        }
        setFollowState(inflateHeader.btFollow, author)

        inflateHeader.btFollow.setOnClickListener {
            if (MConstant.token.isEmpty()) {
                startARouter(ARouterMyPath.SignUI)
            } else {
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
    private fun followAction() {
        newsDetailData?.let {
            var followType = it.authors.isFollow
            when (followType) {
                1 -> {
                    followType = 2
                }
                else -> {
                    followType = 1
                }
            }
            it.authors.isFollow = followType
            setFollowState(inflateHeader.btFollow, it.authors)
            viewModel.followOrCancelUser(it.userId, followType)
        }
    }

    fun smooth() {// todo  没有评论呢？
        val smoothScroller = TopSmoothScroller(this)
        smoothScroller.targetPosition = 1//要滑动到的位置
        linearLayoutManager?.startSmoothScroll(smoothScroller)
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(this)
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
        playerHelper.release()
    }

    //点击系统返回需要判断是否全屏，切换全屏状态
    private fun backPressed(back: () -> Unit) {
        playerHelper.backPressed {
            back()
        }
    }
    override fun onBackPressed() {
        backPressed { super.onBackPressed() }
    }
    /**
     *  有重试 重写此方法
     * */
    override fun onRetryBtnClick() {

    }

    private fun replay() {
        val replyDialog = ReplyDialog(this, object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                viewModel.addNewsComment(artId, content)
            }
        })
        replyDialog.show()
    }

    override fun onClick(v: View) {
        if (MConstant.token.isEmpty()) {
            startARouter(ARouterMyPath.SignUI)
            return
        }
        when (v.id) {
            R.id.tv_news_to_collect -> {
                viewModel.addCollect(artId)
            }
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
                smooth()
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


    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_COMMENT_ITEM).observe(this, {
            if (checkPosition == -1) {
                return@observe
            }
            ToastUtils.showShortToast("checkPosition=" + checkPosition, this)
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
                shareBackUpHttp(this,newsDetailData?.shares)
            }
        })
    }

}