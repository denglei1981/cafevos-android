package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.PageConstant
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.recommend.request.RecommendViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener


/**
 *  推荐列表
 * */
class RecommendFragment : BaseLoadSirFragment<FragmentRecommendListBinding, RecommendViewModel>(),
    OnLoadMoreListener {
    val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(this)
    }
    companion object {
        fun newInstance(): RecommendFragment {
            val fg = RecommendFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    var selectPosition = -1
    override fun initView() {
        viewModel.getRecommend(false)
        binding.smartLayout.setEnableRefresh(false)
        binding.smartLayout.setOnLoadMoreListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            val itemViewType = recommendAdapter.getItemViewType(position)
            val item = recommendAdapter.getItem(position)
            when (itemViewType) {
                1, 2 -> {
                    toPostOrNews(item)
                }
                3 -> { // 跳转到活动
                    toActs(item)
                }
            }
        }
        setLoadSir(binding.smartLayout)
    }

    override fun observe() {
        super.observe()
        bus()
    }

    private fun toPostOrNews(item: RecommendData) { // 跳转到资讯，或者 帖子
        when (item.rtype) {//  val rtype: Int, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
            1 -> {
                if (item.authors != null) {
//                    val newsValueData = NewsValueData(item.artId, item.artType)
//                    val values = Gson().toJson(newsValueData)
                    JumpUtils.instans?.jump(2, item.artId)
                } else {
                    toastShow("没有作者")
                }
            }
            2 -> {
                // todo 跳转到帖子
//                bundle.putString("postsId", value)
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                JumpUtils.instans!!.jump(4, item.postsId)
            }
        }


    }

    private fun toActs(item: RecommendData) {
        when (item.jumpType) {
            "1" -> {
                JumpUtils.instans?.jump(
                    10000,
                    item.jumpValue
                )
            }
            "2" -> {
                JumpUtils.instans?.jump(
                    1,
                    item.jumpValue
                )
//                viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
            }
            "3" -> {
                JumpUtils.instans?.jump(
                    1,
                    item.jumpValue
                )
            }
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = recommendAdapter.getItem(selectPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.postsLikesCount++
            } else {
                bean.postsLikesCount--
            }
            recommendAdapter.notifyItemChanged(selectPosition)
        })

        LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
            .observe(this, Observer {
                // 主要是改，点赞，评论， 浏览记录。。。
                if (selectPosition == -1) {
                    return@Observer
                }
                val item = recommendAdapter.getItem(selectPosition)
                item.artLikesCount = it.likeCount
                item.isLike = it.isLike
                item.commentCount = it.msgCount
                recommendAdapter.notifyItemChanged(selectPosition)// 有t
                if (item.authors?.isFollow != it.isFollow) {
                    // 关注不相同，以详情的为准。。
                    if (item.authors != null) {
                        recommendAdapter.notifyAtt(item.authors!!.authorId, it.isFollow)
                    }
                }
            })

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_FOLLOW_USER).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = recommendAdapter.getItem(selectPosition)
            if (bean.authors?.isFollow != it) { // 关注不相同，以详情的为准。。
                if (bean.authors != null) {
                    recommendAdapter.notifyAtt(bean.authors!!.authorId, it)
                }
            }
        })
    }
    override fun initData() {
        viewModel.recommendLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    recommendAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    recommendAdapter.setNewInstance(dataList)
                    (parentFragment as HomeV2Fragment).stopRefresh()
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                showFailure(it.message)
                // 刷新也得停
                (parentFragment as HomeV2Fragment).stopRefresh()
                ToastUtils.showShortToast(it.message, requireContext())
            }

        })
    }

    open fun homeRefersh() {
        viewModel.getRecommend(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getRecommend(true)
    }

    override fun onRetryBtnClick() {

    }
}