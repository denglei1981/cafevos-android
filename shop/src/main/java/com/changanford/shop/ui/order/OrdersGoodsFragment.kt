package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderAdapter
import com.changanford.shop.control.OrderControl
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
class OrdersGoodsFragment : BaseFragment<FragmentOrdersgoodsListBinding, OrderViewModel>(),
    OnRefreshLoadMoreListener {
    companion object {
        fun newInstance(statesId: Int): OrdersGoodsFragment {
            val bundle = Bundle()
            bundle.putInt("statesId", statesId)
            val fragment = OrdersGoodsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val mAdapter by lazy { OrderAdapter(-1, viewModel = viewModel) }
    private var pageNo = 1
    private var statesId = -1//-1全部 不等于-1其他
    private val control by lazy { OrderControl(requireContext(), viewModel) }
    private var searchContent = ""
    override fun initView() {
        binding.recyclerView.adapter = mAdapter
        mAdapter.setEmptyView(R.layout.view_empty_order)
        mAdapter.setOnItemClickListener { _, _, position ->
            control.clickOrderItemJump(mAdapter.data[position])
        }
        binding.smartRl.setOnRefreshLoadMoreListener(this)
        initLiveDataBus()
    }

    override fun initData() {
        if (arguments != null) {
            statesId = requireArguments().getInt("statesId", -1)
            mAdapter.orderSource = statesId
//            viewModel.getShopOrderList(statesId,pageNo,showLoading = -1==statesId)
        }
        viewModel.shopOrderData.observe(this) {
            it?.let {
                mAdapter.nowTime = it.nowTime
                if (1 == pageNo) {
                    if (it.dataList.isNullOrEmpty() && statesId == -1 && searchContent.isEmpty()) {
                        LiveDataBus.get().with(LiveDataBusKey.ORDERS_GOODS_SHOW_EMPTY).postValue("")
                    }
                    mAdapter.setList(it.dataList)
                    binding.recyclerView.scrollToPosition(0)
                } else mAdapter.addData(it.dataList)
            }
            //            if(null==it|| it.dataList.isEmpty())pageNo--
            if (null == it || mAdapter.data.size >= it.total) binding.smartRl.setEnableLoadMore(
                false
            )
            else binding.smartRl.setEnableLoadMore(true)
            binding.smartRl.apply {
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
            viewModel.getShopOrderList(
                statesId,
                pageNo,
                searchContent = searchContent,
                showLoading = -1 == statesId
            )
        }
    }

    override fun onStart() {
        super.onStart()
        pageNo = 1
        viewModel.getShopOrderList(
            statesId,
            pageNo,
            searchContent = searchContent,
            showLoading = (-1 == statesId && mAdapter.data.size < 1)
        )
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo = 1
        viewModel.getShopOrderList(statesId, pageNo, searchContent = searchContent)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getShopOrderList(statesId, pageNo, searchContent = searchContent)
    }
}