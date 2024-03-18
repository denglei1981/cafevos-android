package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.SpecialListBean
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterHomePath.SpecialListActivity
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.NEWS_DETAIL_CHANGE
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.FragmentNewsListBinding
import com.changanford.home.databinding.HeaderNewsListBinding
import com.changanford.home.news.adapter.NewsBannerAdapter
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.request.FindNewsListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhpan.bannerview.constants.PageStyle

/**
 *  资讯列表
 * */
class NewsListFragment : BaseLoadSirFragment<FragmentNewsListBinding, FindNewsListViewModel>(),
    OnLoadMoreListener, OnRefreshListener {
    private var headNewBinding: HeaderNewsListBinding? = null
    private val newsListAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this)
    }

    companion object {
        fun newInstance(): NewsListFragment {
            val fg = NewsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    private var selectPosition: Int = -1;// 记录选中的 条目

    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        addHeadView()
        binding.recyclerView.adapter = newsListAdapter

        newsListAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = newsListAdapter.getItem(position)
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35, item.userId.toString())
                }

                R.id.layout_content, R.id.tv_time_look_count, R.id.tv_comment_count -> {// 去资讯详情。
                    if (item.authors != null) {
//                        val newsValueData = NewsValueData(item.artId, item.type)
//                        val values = Gson().toJson(newsValueData)
                        GioPageConstant.infoEntrance = "发现-资讯-信息流"
                        JumpUtils.instans?.jump(2, item.artId)
                        GIOUtils.homePageClick("资讯信息流", (position + 1).toString(), item.title)
                    } else {
                        toastShow("没有作者")
                    }
                }
            }
        }
        binding.smartLayout.setEnableRefresh(true)
        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        homeRefersh()
        setLoadSir(binding.smartLayout)
        newsListAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getNewsList(true)
        }
    }

    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_news_list,
                binding.recyclerView,
                false
            )
            val newsBannerAdapter = NewsBannerAdapter()
            headNewBinding?.let {
                newsListAdapter.addHeaderView(it.root, 0)
                it.bViewpager.setAdapter(newsBannerAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.create()
                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        val speical = it.bViewpager.data[position] as SpecialListBean
                        if (TextUtils.isEmpty(speical.title)) {
                            it.tvSubTitle.text = "长安福特专题"
                        } else {
                            it.tvSubTitle.text = speical.title
                        }
                    }
                })
                it.tvMore.setOnClickListener {
                    GIOUtils.homePageClick("专题区", (0).toString(), "更多")
                    startARouter(SpecialListActivity)
                }
            }
            setIndicator()
        }
    }


    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        headNewBinding?.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            ?.setIndicatorSize(dp6, dp6, resources.getDimensionPixelOffset(R.dimen.dp_20), dp6)
            ?.setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    override fun initData() {

    }

    override fun onRetryBtnClick() {

    }

    override fun observe() {
        super.observe()
        viewModel.specialListLiveData.safeObserve(this) {
            if (it.isSuccess && it.data.extend.articleListShow == 1) {
                headNewBinding?.clTopBanner?.isVisible = true
                headNewBinding?.bViewpager?.create(it.data.dataList)
            } else {
                headNewBinding?.clTopBanner?.isVisible = false
//                ToastUtils.showShortToast(it.message, requireActivity())
            }

        }
        viewModel.newsListLiveData.safeObserve(this) {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    newsListAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                    //设置状态完成
                    newsListAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    newsListAdapter.setNewInstance(dataList)
//                    (parentFragment as HomeV2Fragment).stopRefresh()
                    binding.smartLayout.finishRefresh()
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                    newsListAdapter.loadMoreModule.loadMoreEnd()
                } else {
//                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                when (it.message) {
                    getString(R.string.net_error) -> {
                        showTimeOut()
                    }

                    else -> {
                        showFailure(it.message)
                    }
                }
                ToastUtils.showShortToast(it.message, requireContext())

            }
        }
        LiveDataBus.get().withs<InfoDetailsChangeData>(NEWS_DETAIL_CHANGE).observe(this, Observer {
            // 主要是改，点赞，评论， 浏览记录。。。
            if (isCurrentPage()) {
                if (selectPosition == -1) {
                    return@Observer
                }
                val item = newsListAdapter.getItem(selectPosition)
                item.likesCount = it.likeCount
                item.isLike = it.isLike
                item.commentCount = it.msgCount
                newsListAdapter.notifyItemChanged(selectPosition + 1)// 有t
                if (item.authors?.isFollow != it.isFollow) { // 关注不相同，以详情的为准。。
                    newsListAdapter.notifyAtt(item.userId, it.isFollow)
                }
            }
        })

        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, {
                // 收到 登录状态改变回调都要刷新页面
                homeRefersh()
            })

        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).observe(this, Observer {
            homeRefersh()
        })


    }

    /**
     * 当前是否可见
     */
    private fun isCurrentPage(): Boolean {
        if (parentFragment is HomeV2Fragment) {
            return (parentFragment as HomeV2Fragment).isCurrentIndex(2)
        }
        return false
    }


//    override fun onRefresh(refreshLayout: RefreshLayout) {
//        viewModel.getSpecialList()
//        viewModel.getNewsList(false)
//    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getNewsList(true)
    }


    fun homeRefersh() {
        viewModel.getSpecialList()
        viewModel.getNewsList(false)
    }

    override fun onResume() {
        super.onResume()
        try {
            headNewBinding?.bViewpager?.startLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()

        try {
            headNewBinding?.bViewpager?.stopLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            headNewBinding?.bViewpager?.stopLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        homeRefersh()
    }
}