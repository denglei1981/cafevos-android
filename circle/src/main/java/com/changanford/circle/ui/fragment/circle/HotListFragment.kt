package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import com.changanford.circle.adapter.circle.CircleTopAdapter
import com.changanford.circle.databinding.FragmentHotlistBinding
import com.changanford.circle.ui.activity.CircleDetailsActivity
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : HotListFragment
 */
class HotListFragment:BaseFragment<FragmentHotlistBinding, NewCircleViewModel>(),
    OnRefreshLoadMoreListener {
    companion object {
        fun newInstance(topId: Int): HotListFragment {
            val bundle = Bundle()
            bundle.putInt("topId", topId)
            val fragment = HotListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private var topId=0
    private var pageNo=1
    private val mAdapter by lazy { CircleTopAdapter() }
    override fun initView() {
        binding.srl.setOnRefreshLoadMoreListener(this)
        binding.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            CircleDetailsActivity.start( mAdapter.data[position].circleId)
        }
    }

    override fun initData() {
        viewModel.circleListData.observe(this,{
            it?.apply {
                val dataList=this.dataList
                if(1==pageNo)mAdapter.setList(dataList)
                else dataList?.let {itemData-> mAdapter.addData(itemData) }
            }
            if(it == null ||mAdapter.data.size>= it.total)binding.srl.setEnableLoadMore(false)
            else binding.srl.setEnableLoadMore(true)
            binding.srl.apply {
                finishLoadMore()
                finishRefresh()
            }
        })
        arguments?.getInt("topId",0)?.apply {
            topId=this
            viewModel.getHotList(topId,pageNo)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        viewModel.getHotList(topId,pageNo)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getHotList(topId,pageNo)
    }
}