package com.changanford.shop.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.adapter.order.OrderAdapter
import com.changanford.shop.databinding.ActOrderAllBinding
import com.changanford.shop.popupwindow.OrderScreeningPop
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2021/9/26
 * @Description : 所有订单
 */
@Route(path = ARouterShopPath.AllOrderActivity)
class AllOrderActivity:BaseActivity<ActOrderAllBinding, OrderViewModel>(),
    OrderScreeningPop.OnSelectListener, OnRefreshLoadMoreListener {
    companion object{
        fun start(orderType:Int) {
            JumpUtils.instans?.jump(36,"$orderType")
        }
    }
    private var pageNo=1
    private val mAdapter by lazy { OrderAdapter(-1) }
    override fun initView() {
        binding.topBar.setActivity(this)
        binding.topBar.setOnRightTvClickListener(object :TopBar.OnRightTvClickListener{
            override fun onRightTvClick() {
                OrderScreeningPop(this@AllOrderActivity).show(this@AllOrderActivity)
            }
        })
        binding.smartRl.setOnRefreshLoadMoreListener(this)
        binding.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            OrderDetailsActivity.start(this,mAdapter.data[position].orderNo)
        }
    }

    override fun initData() {
        viewModel.shopOrderData.observe(this,{
            it?.let {
                mAdapter.nowTime=it.nowTime
                if(1==pageNo)mAdapter.setList(it.dataList)
                else mAdapter.addData(it.dataList)
            }
            binding.smartRl.finishLoadMore()
            binding.smartRl.finishRefresh()
        })
        viewModel.getAllOrderList(pageNo)
//        viewModel.getOrderKey()
    }

    /**
     * 订单筛选结果回调 0 商品、1购车 2 试驾
    * */
    override fun onSelectBackListener(type: Int) {
        when(type){
            0->JumpUtils.instans?.jump(52)
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        viewModel.getAllOrderList(pageNo)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getAllOrderList(pageNo)
    }
}