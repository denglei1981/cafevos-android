package com.changanford.home.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.NewsValueData
import com.changanford.common.router.path.ARouterHomePath.SpecialListActivity
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.NEWS_DETAIL_CHANGE
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.R
import com.changanford.home.bean.SpecialListBean
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.FragmentNewsListBinding
import com.changanford.home.databinding.HeaderNewsListBinding
import com.changanford.home.news.adapter.NewsBannerAdapter
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.request.FindNewsListViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 *  新闻列表
 * */
class NewsListFragment : BaseLoadSirFragment<FragmentNewsListBinding, FindNewsListViewModel>(),
    OnRefreshListener {
    val newsListAdapter: NewsListAdapter by lazy {
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
                    JumpUtils.instans!!.jump(35)
                }
                R.id.layout_content, R.id.tv_time_look_count, R.id.tv_comment_count -> {// 去资讯详情。
                    if (item.authors != null) {
                        var newsValueData = NewsValueData(item.artId, item.type)
                        var values = Gson().toJson(newsValueData)
                        JumpUtils.instans?.jump(2, values)
                    } else {
                        toastShow("没有作者")
                    }
                }
            }
        }

        binding.smartLayout.setOnRefreshListener(this)
        onRefresh(binding.smartLayout)
        setLoadSir(binding.smartLayout)

    }

    var headNewBinding: HeaderNewsListBinding? = null

    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_news_list,
                binding.recyclerView,
                false
            )
            var newsBannerAdapter = NewsBannerAdapter()
            headNewBinding?.let {
                newsListAdapter.addHeaderView(it.root, 0)
                it.bViewpager.setAdapter(newsBannerAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.create()
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        var speical = it.bViewpager.data[position] as SpecialListBean
                        if (TextUtils.isEmpty(speical.summary)) {
                            it.tvSubTitle.text = "长安福特,yyds"
                        } else {
                            it.tvSubTitle.text = speical.summary
                        }
                    }
                })
                it.tvMore.setOnClickListener {
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
        viewModel.specialListLiveData.observe(this, {
            if (it.isSuccess) {
                headNewBinding?.bViewpager?.create(it.data.dataList)
            } else {
                ToastUtils.showShortToast(it.message, requireActivity())
            }

        })
        viewModel.newsListLiveData.observe(this, {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    newsListAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    newsListAdapter.setNewInstance(dataList)
                    binding.smartLayout.finishRefresh()
                }
                if (dataList.size < it.data.pageSize) { // 没有请求的多, 不加载更多。
                    binding.smartLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                showFailure(it.message)
                ToastUtils.showShortToast(it.message, requireContext())
            }
        })
        LiveDataBus.get().withs<InfoDetailsChangeData>(NEWS_DETAIL_CHANGE).observe(this, Observer {
            // 主要是改，点赞，评论， 浏览记录。。。
            if(isCurrentPage()){
                val item = newsListAdapter.getItem(selectPosition)
                item.likesCount = it.likeCount
                item.isLike = it.isLike
                item.authors?.isFollow = it.isFollow
                item.commentCount = it.msgCount
                newsListAdapter.notifyItemChanged(selectPosition + 1)// 有t
            }
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

    // 关注或者取消
    private fun followActionIn(authorBaseVo: AuthorBaseVo, position: Int) {
        var followType = authorBaseVo.isFollow
        when (followType) {
            1 -> {
                followType = 2
            }
            else -> {
                followType = 1
            }
        }
        authorBaseVo.isFollow = followType
        // 有头布局。
//        newsListAdapter.notifyItemChanged(position+1,"follow")
//        newsListAdapter.notifyItemChanged(position+1)
        viewModel.followOrCancelUser(authorBaseVo.authorId, followType)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getSpecialList()
        viewModel.getNewsList(false)
    }
}