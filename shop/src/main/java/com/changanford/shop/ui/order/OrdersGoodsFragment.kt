package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderAdapter
import com.changanford.shop.databinding.FragmentOrdersgoodsListBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2021/9/24 0024
 * @Description : OrdersGoodsFragment
 */
class OrdersGoodsFragment:BaseFragment<FragmentOrdersgoodsListBinding, OrderViewModel>(),
    OnRefreshLoadMoreListener {
    companion object{
        fun newInstance(statesId:Int): OrdersGoodsFragment {
            val bundle = Bundle()
            bundle.putInt("statesId", statesId)
            val fragment= OrdersGoodsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val mAdapter by lazy { OrderAdapter(0) }
    private var pageNo=1
    private var statesId=-1
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemClickListener { _, _, position ->
            OrderDetailsActivity.start(requireContext(),mAdapter.data[position].mallMallOrderId)
        }
        binding.smartRl.setOnRefreshLoadMoreListener(this)
    }
    override fun initData() {
        if(arguments!=null){
            statesId= requireArguments().getInt("statesId", -1)
            viewModel.getShopOrderList(statesId,pageNo)
        }
        viewModel.shopOrderData.observe(this,{
            mAdapter.nowTime=it.nowTime
            if(1==pageNo)mAdapter.setList(it.dataList)
            else mAdapter.addData(it.dataList)
            binding.smartRl.finishLoadMore()
            binding.smartRl.finishRefresh()
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        viewModel.getShopOrderList(statesId,pageNo)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getShopOrderList(statesId,pageNo)
    }
}