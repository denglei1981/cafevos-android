package com.changanford.home.search.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchPostsResultAdapter
import com.changanford.home.search.request.PolySearchNewsResultViewModel
import com.changanford.home.search.request.PolySearchPostResultViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchPostFragment :
    BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchPostResultViewModel>(),
    OnRefreshListener, OnLoadMoreListener {


    val searchPostsResultAdapter: SearchPostsResultAdapter by lazy {
        SearchPostsResultAdapter(this)
    }
    private var selectPosition: Int = -1;// 记录选中的 条目

    companion object {
        fun newInstance(skwContent: String,tagId:String): SearchPostFragment {
            val fg = SearchPostFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            bundle.putString(JumpConstant.SEARCH_TAG_ID,tagId)
            fg.arguments = bundle

            return fg
        }
    }

    var searchContent: String? = null
    var tagId:String=""
    override fun initView() {

        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
         tagId = arguments?.getString(JumpConstant.SEARCH_TAG_ID).toString()
        binding.recyclerView.adapter = searchPostsResultAdapter
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)

        searchPostsResultAdapter.setOnItemChildClickListener { adapter, view, position ->
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35)
                }
            }
        }
        searchPostsResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = searchPostsResultAdapter.getItem(position)
                selectPosition = position
                // todo 跳转到帖子
//                bundle.putString("postsId", value)
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                JumpUtils.instans!!.jump(4, item.postsId.toString())
            }

        })
    }

    override fun initData() {
        setLoadSir(binding.smartLayout)
        onRefresh(binding.smartLayout)
    }

    override fun observe() {
        super.observe()
        viewModel.newsListLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    binding.smartLayout.finishLoadMore()
                    searchPostsResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchPostsResultAdapter.setNewInstance(it.data.dataList)
                    if (it.data.dataList.size == 0) {
                        showEmpty()
                    }
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                showFailure(it.message)
            }
        })

        LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
            .observe(this, Observer {
                // 主要是改，点赞，评论， 浏览记录。。。
                if (selectPosition == -1) {
                    return@Observer
                }
                val item = searchPostsResultAdapter.getItem(selectPosition)
                item.likesCount = it.likeCount
                item.isLike = it.isLike
                item.authorBaseVo?.isFollow = it.isFollow
                item.commentCount = it.msgCount
                searchPostsResultAdapter.notifyItemChanged(selectPosition)// 有t

            })
    }

    override fun onRetryBtnClick() {

    }
    fun outRefresh(keyWord: String) { // 暴露给外部的耍新
        searchContent = keyWord
        onRefresh(binding.smartLayout)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {

            viewModel.getSearchContent(it, tagId,false)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, tagId,true)
        }
    }
}