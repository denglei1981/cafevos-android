package com.changanford.home.news.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseActivity
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
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivityNewsDetailsBinding
import com.changanford.home.databinding.LayoutHeadlinesHeaderNewsDetailBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter
import com.changanford.home.news.adapter.NewsRecommendListAdapter
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.request.NewsDetailViewModel
import com.changanford.home.widget.ReplyDialog
import com.changanford.home.widget.TopSmoothScroller
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

/**
 *  图文详情。。。
 * */
@Route(path = ARouterHomePath.NewsDetailActivity)
class NewsDetailActivity : BaseActivity<ActivityNewsDetailsBinding, NewsDetailViewModel>(),
    View.OnClickListener {


    val  linearLayoutManager: LinearLayoutManager by lazy{
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    var checkPosition: Int = -1

    private lateinit var artId: String
    private val homeNewsCommentAdapter: HomeNewsCommentAdapter by lazy {
        HomeNewsCommentAdapter(this)
    }

    private val newsRecommendListAdapter: NewsRecommendListAdapter by lazy {
        NewsRecommendListAdapter()
    }

    //HTML文本
    private val webHelper by lazy {
        CustomWebHelper(this, inflateHeader.wvContent)
    }

    var llInfoBottom:Int =0
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.pbRecyclerview.layoutManager = linearLayoutManager
        binding.pbRecyclerview.adapter = homeNewsCommentAdapter
        addHeaderView()
        binding.llComment.tvSpeakSomething.setOnClickListener(this)
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
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutTitle.ivMore.setOnClickListener(this)
        llInfoBottom = binding.layoutTitle.llAuthorInfo.getBottom()
        binding.pbRecyclerview.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position: Int = linearLayoutManager.findFirstVisibleItemPosition()
                val firstVisiableChildView = linearLayoutManager.findViewByPosition(position) as View
                val itemHeight = firstVisiableChildView.height
                val scrollHeight = position * itemHeight - firstVisiableChildView.top
                binding.layoutTitle.llAuthorInfo.visibility = if (scrollHeight > llInfoBottom) View.VISIBLE else View.GONE //如果滚动超过用户信息一栏，显示标题栏中的用户头像和昵称

            }
        })
        binding.layoutTitle.llAuthorInfo.setOnClickListener {
            JumpUtils.instans!!.jump(35, newsDetailData?.userId.toString())
        }
    }
    override fun initData() {
        artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID).toString()
        if (!TextUtils.isEmpty(artId)) {
            if (!TextUtils.isEmpty(artId)) {
                viewModel.getNewsDetail(artId)
                viewModel.getNewsCommentList(artId, false)
                viewModel.getArtAdditional(artId)
            } else {
                ToastUtils.showShortToast("没有该资讯类型", this)
            }
            bus()
        }
    }

    private val inflateHeader: LayoutHeadlinesHeaderNewsDetailBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_headlines_header_news_detail,
            binding.pbRecyclerview,
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
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(this)
        setFollowState.setFollowState(btnFollow, authors)

    }


    var newsDetailData: NewsDetailData? = null
    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        this.newsDetailData = newsDetailData
        val author = newsDetailData.authors
        GlideUtils.loadBD(author.avatar, inflateHeader.ivAvatar)
        GlideUtils.loadBD(author.avatar, binding.layoutTitle.ivAvatar)
        binding.layoutTitle.tvAuthor.text=author.nickname
        setFollowState(inflateHeader.btFollow, author)
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvTitle.text = newsDetailData.title
        inflateHeader.tvTime.text = newsDetailData.timeStr


        if (!TextUtils.isEmpty(newsDetailData.content)) {
            webHelper.loadDataWithBaseURL(newsDetailData.content)
        }
        try {
            if (!TextUtils.isEmpty(newsDetailData.getPicUrl())) {
                GlideUtils.loadBD(newsDetailData.getPicUrl(), inflateHeader.ivPic)
                inflateHeader.ivPic.visibility = View.VISIBLE
            } else {
                inflateHeader.ivPic.visibility = View.GONE
            }
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
            if (MConstant.token.isEmpty()) {
                startARouter(ARouterMyPath.SignUI)
            } else {
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
                    homeNewsCommentAdapter.addData(it.data.dataList)
                } else {
                    homeNewsCommentAdapter.setNewInstance(it.data.dataList)
                }
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
        viewModel.commentSateLiveData.observe(this, Observer {
            if (it.isSuccess) {
                isNeedNotify
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
                isNeedNotify
                setLikeState()
            } else {// 网络原因操作失败了。
                ToastUtils.showShortToast(it.message, this)

            }
        })
        viewModel.recommendNewsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data != null && it.data.recommendArticles.size > 0) {
                    inflateHeader.flRecommend.visibility=View.VISIBLE
                    newsRecommendListAdapter.setNewInstance(it.data.recommendArticles)
                } else {// 隐藏热门推荐。
                    inflateHeader.flRecommend.visibility=View.GONE
                }

            }
        })

        viewModel.followLiveData.observe(this, Observer {

        })

        viewModel.collectLiveData.observe(this, Observer {
            if(it.isSuccess){
                if (it.isSuccess) {
                    setCollection()
                }
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
            it.authors.isFollow = followType;
            setFollowState(inflateHeader.btFollow, it.authors)
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
            R.id.tv_news_to_collect -> {
                // 收藏
                viewModel.addCollect(artId)
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

    fun smooth() {// todo  没有评论呢？
        val smoothScroller = TopSmoothScroller(this)
        smoothScroller.targetPosition = 1//要滑动到的位置
        linearLayoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun replay() {
        val replyDialog = ReplyDialog(this, object : ReplyDialog.ReplyListener {
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
        webHelper.onDestroy()
        super.onDestroy()
    }

}