package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.FollowUserChangeBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.noAnima
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.activity.PloySearchResultActivity
import com.changanford.home.search.adapter.SearchPostsResultAdapter
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
        fun newInstance(skwContent: String, tagId: String): SearchPostFragment {
            val fg = SearchPostFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            bundle.putString(JumpConstant.SEARCH_TAG_ID, tagId)
            fg.arguments = bundle

            return fg
        }
    }

    var searchContent: String? = null
    var tagId: String = ""
    override fun initView() {

        //        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        searchContent = (activity as PloySearchResultActivity).searchContent
        tagId = arguments?.getString(JumpConstant.SEARCH_TAG_ID).toString()
        binding.recyclerView.noAnima()
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
        searchPostsResultAdapter.setOnItemClickListener { adapter, view, position ->
            GioPageConstant.postEntrance = "搜索结果页"
            val item = searchPostsResultAdapter.getItem(position)
            selectPosition = position
            JumpUtils.instans!!.jump(4, item.postsId.toString())
        }

        LiveDataBus.get().withs<String>(LiveDataBusKey.UPDATE_SEARCH_RESULT).observe(this) {
            outRefresh(it)
        }
        LiveDataBus.get().withs<FollowUserChangeBean>(LiveDataBusKey.FOLLOW_USER_CHANGE)
            .observe(this) {
                refreshUserFollow(it.userId, it.followType)
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
                    searchPostsResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchPostsResultAdapter.setNewInstance(it.data.dataList)
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


//        LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
//            .observe(this, Observer {
//                // 主要是改，点赞，评论， 浏览记录。。。
//                if (selectPosition == -1) {
//                    return@Observer
//                }
//                val item = searchPostsResultAdapter.getItem(selectPosition)
//                item.likesCount = it.likeCount
//                item.isLike = it.isLike
//                item.authorBaseVo?.isFollow = it.isFollow
//                item.commentCount = it.msgCount
//                searchPostsResultAdapter.notifyItemChanged(selectPosition)// 有t
//
//            })
        LiveDataBus.get().withs<Int>(LiveDataBusKey.REFRESH_POST_LIKE).observe(this) {
            val item = searchPostsResultAdapter.getItem(selectPosition)
            item.isLike = it
            if (it == 0) {
                item.likesCount--
            } else {
                item.likesCount++
            }
            searchPostsResultAdapter.notifyItemChanged(selectPosition)
        }
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_FOLLOW_USER).observe(this) {
            val item = searchPostsResultAdapter.getItem(selectPosition)
            if (item.authorBaseVo?.isFollow != it) {
                item.authorBaseVo?.authorId?.let { it1 -> refreshUserFollow(it1, it) }
            }
            item.authorBaseVo?.isFollow = it
            searchPostsResultAdapter.notifyItemChanged(selectPosition)
        }
    }

    private fun refreshUserFollow(userId: String, type: Int) {
        val list = searchPostsResultAdapter.data
        list.forEach { bean ->
            if (bean.authorBaseVo?.authorId == userId) {
                bean.authorBaseVo?.isFollow = type
            }
        }
        searchPostsResultAdapter.notifyDataSetChanged()
    }

    override fun onRetryBtnClick() {

    }

    private fun outRefresh(keyWord: String) { // 暴露给外部的耍新
        searchContent = keyWord
        onRefresh(binding.smartLayout)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, tagId, false)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, tagId, true)
        }
    }
}