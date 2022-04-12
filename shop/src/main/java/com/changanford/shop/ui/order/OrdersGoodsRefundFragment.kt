package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderRefundAdapter
import com.changanford.shop.databinding.FragmentOrdersgoodsListBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2021/9/24 0024
 * @Description : OrdersGoodsFragment
 */
class OrdersGoodsRefundFragment:BaseFragment<FragmentOrdersgoodsListBinding, OrderViewModel>(),
    OnRefreshLoadMoreListener {
    companion object{
        fun newInstance(statesId:Int): OrdersGoodsRefundFragment {
            val bundle = Bundle()
            bundle.putInt("statesId", statesId)
            val fragment= OrdersGoodsRefundFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val mAdapter by lazy { OrderRefundAdapter() }
    private var pageNo=1
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty_order)
        mAdapter.setOnItemClickListener { _, _, position ->
            OrderDetailsV2Activity.start(mAdapter.data[position].orderNo)
        }
        binding.smartRl.setOnRefreshLoadMoreListener(this)
    }
    override fun initData() {
        viewModel.refundBeanLiveData.observe(this) {
            it?.dataList?.let {dataList->
                if (1 == pageNo) mAdapter.setList(dataList)
                else mAdapter.addData(dataList)
            }
            binding.smartRl.apply {
                setEnableLoadMore(mAdapter.data.size < it?.total?:0)
                when (state) {
                    RefreshState.Refreshing -> finishRefresh()
                    RefreshState.Loading -> finishLoadMore()
                    else -> {
                        finishRefresh()
                        finishLoadMore()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        pageNo=1
        viewModel.getShopOrderRefundList(pageNo)
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        viewModel.getShopOrderRefundList(pageNo)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getShopOrderRefundList(pageNo)
    }
}