package com.changanford.home.search.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.noAnima
import com.changanford.common.utilext.toastShow
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.activity.PloySearchResultActivity
import com.changanford.home.search.adapter.SearchNewsResultAdapter
import com.changanford.home.search.request.PolySearchNewsResultViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchNewsFragment :
    BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchNewsResultViewModel>(),
    OnRefreshListener, OnLoadMoreListener {


    val searchNewsResultAdapter: SearchNewsResultAdapter by lazy {
        SearchNewsResultAdapter(this)
    }
    private var selectPosition: Int = -1;// 记录选中的 条目

    companion object {
        fun newInstance(skwContent: String): SearchNewsFragment {
            val fg = SearchNewsFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle

            return fg
        }
    }

    var searchContent: String? = null
    override fun initView() {

//        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        searchContent = (activity as PloySearchResultActivity).searchContent
        binding.recyclerView.noAnima()
        binding.recyclerView.adapter = searchNewsResultAdapter
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)

        searchNewsResultAdapter.setOnItemChildClickListener { adapter, view, position ->
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35)
                }

            }
        }
        searchNewsResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = searchNewsResultAdapter.getItem(position)
                selectPosition = position
                if (item.authors != null) {
//                    var newsValueData = NewsValueData(item.artId, item.type)
//                    var values = Gson().toJson(newsValueData)
                    JumpUtils.instans?.jump(2, item.artId)
                } else {
                    toastShow("没有作者")
                }
            }

        })

        LiveDataBus.get().withs<String>(LiveDataBusKey.UPDATE_SEARCH_RESULT).observe(this){
            outRefresh(it)
        }
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
                    searchNewsResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchNewsResultAdapter.setNewInstance(it.data.dataList)
                    if (it.data.dataList.size == 0) {
                        showResultEmpty()
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

                val item = searchNewsResultAdapter.getItem(selectPosition)
                item.likesCount = it.likeCount
                item.isLike = it.isLike
                item.authors?.isFollow = it.isFollow
                item.commentCount = it.msgCount
                searchNewsResultAdapter.notifyItemChanged(selectPosition)// 有t

            })
    }

    override fun onRetryBtnClick() {

    }

  private  fun outRefresh(keyWord: String) { // 暴露给外部的耍新
        searchContent = keyWord
        onRefresh(binding.smartLayout)
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, false)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, true)
        }
    }
}