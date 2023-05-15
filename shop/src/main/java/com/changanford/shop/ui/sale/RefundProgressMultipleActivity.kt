package com.changanford.shop.ui.sale

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.adapter.order.OrderRefundMultipleAdapter
import com.changanford.shop.databinding.ActRefundProgressMultipleBinding
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.view.TopBar

/**
 *Author lcw
 *Time on 2023/5/12
 *Purpose 退款进度多个子订单
 */
@Route(path = ARouterShopPath.RefundProgressMultipleActivity)
class RefundProgressMultipleActivity :
    BaseActivity<ActRefundProgressMultipleBinding, RefundViewModel>() {

    private var orderNo: String = ""

    private val adapter by lazy {
        OrderRefundMultipleAdapter(viewModel)
    }

    override fun initView() {
        title = "退款进度"
        orderNo = intent.getStringExtra("orderNo").toString()
        binding.tobBar.setTitle("退款进度")
        binding.recyclerView.adapter = adapter
        initListener()
    }

    private fun initListener(){
        binding.tobBar.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.smartLayout.setOnRefreshListener {
            viewModel.getOrderMultiple(orderNo)
        }

        LiveDataBus.get().withs<String>(LiveDataBusKey.REFUND_NOT_SHOP_SUCCESS).observe(this){
            initData()
        }
    }

    override fun initData() {
        viewModel.getOrderMultiple(orderNo)
        viewModel.refundMultipleBean.observe(this) {
            binding.smartLayout.finishRefresh()
            adapter.setList(it)
        }
    }
}