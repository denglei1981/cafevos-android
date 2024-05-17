package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
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
class OrdersGoodsRefundFragment : BaseFragment<FragmentOrdersgoodsListBinding, OrderViewModel>(),
    OnRefreshLoadMoreListener {
    companion object {
        fun newInstance(statesId: Int): OrdersGoodsRefundFragment {
            val bundle = Bundle()
            bundle.putInt("statesId", statesId)
            val fragment = OrdersGoodsRefundFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val mAdapter by lazy { OrderRefundAdapter() }
    private var pageNo = 1
    private var searchContent = ""
    override fun initView() {
        binding.recyclerView.adapter = mAdapter
        mAdapter.setEmptyView(R.layout.view_empty_order)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                //整单退
                if (refundType == "ALL_ORDER") JumpUtils.instans?.jump(124, mallMallRefundId)
                //单SKU退
                else JumpUtils.instans?.jump(126, mallMallRefundId)

            }
        }
        binding.smartRl.setOnRefreshLoadMoreListener(this)
        initLiveDataBus()
    }

    override fun initData() {
        viewModel.refundBeanLiveData.observe(this) {
            it?.dataList?.let { dataList ->
                if (1 == pageNo) {
                    mAdapter.setList(dataList)
                    binding.recyclerView.scrollToPosition(0)
                } else mAdapter.addData(dataList)
            }
            binding.smartRl.apply {
                setEnableLoadMore(mAdapter.data.size < it?.total ?: 0)
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

    private fun initLiveDataBus() {
        LiveDataBus.get().withs<String>(LiveDataBusKey.SHOP_ORDER_SEARCH).observe(this) {
            searchContent = it
            pageNo = 1
            viewModel.getShopOrderRefundList(pageNo, searchContent = searchContent)
        }
    }

    override fun onStart() {
        super.onStart()
        pageNo = 1
        viewModel.getShopOrderRefundList(pageNo)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo = 1
        viewModel.getShopOrderRefundList(pageNo)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getShopOrderRefundList(pageNo)
    }
}