package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.NewsValueData
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.PageConstant
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.recommend.request.RecommendViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener


/**
 *  推荐列表
 * */
class RecommendFragment : BaseLoadSirFragment<FragmentRecommendListBinding, RecommendViewModel>(),
    OnLoadMoreListener {

    val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter()
    }

    companion object {
        fun newInstance(): RecommendFragment {
            val fg = RecommendFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    var selectPosition=-1
    override fun initView() {
        viewModel.getRecommend(false)
        binding.smartLayout.setEnableRefresh(false)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { adapter, view, position ->
//            startARouter(ARouterHomePath.NewsPicAdActivity)
            selectPosition = position
            val itemViewType = recommendAdapter.getItemViewType(position)
            val item = recommendAdapter.getItem(position)
            when(itemViewType){
                1,2->{
                    toPostOrNews(item)
                }
                3->{ // 跳转到活动
                    toActs(item)
                }
            }


        }
        setLoadSir(binding.smartLayout)

    }

    private fun toPostOrNews(item: RecommendData) { // 跳转到资讯，或者 帖子
        when(item.rtype){//  val rtype: Int, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
            1->{
                if (item.authors != null) {
                    val newsValueData = NewsValueData(item.artId, item.artType)
                    val values = Gson().toJson(newsValueData)
                    JumpUtils.instans?.jump(2, values)
                } else {
                    toastShow("没有作者")
                }
            }
            2->{
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
            "2"-> {
                JumpUtils.instans?.jump(
                    1,
                    item.jumpValue
                )
//                viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
            }
            "3"-> {
                JumpUtils.instans?.jump(
                    1,
                    item.jumpValue
                )
            }
        }


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