package com.changanford.shop.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.adapter.order.OrderAdapter
import com.changanford.shop.databinding.ActOrderAllBinding
import com.changanford.shop.popupwindow.OrderScreeningPop
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel

/**
 * @Author : wenke
 * @Time : 2021/9/26
 * @Description : 所有订单
 */
@Route(path = ARouterShopPath.AllOrderActivity)
class AllOrderActivity:BaseActivity<ActOrderAllBinding, OrderViewModel>(),
    OrderScreeningPop.OnSelectListener {
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
        binding.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            OrderDetailsActivity.start(this,"$position")
        }
    }

    override fun initData() {
        viewModel.getAllOrderList(pageNo)
        val datas= arrayListOf<OrderItemBean>()
        for (i in 0..15){
            val item= OrderItemBean(i,"Title$i")
            datas.add(item)
        }
        mAdapter.setList(datas)
    }

    /**
     * 订单筛选结果回调 0 商品、1购车 2 试驾
    * */
    override fun onSelectBackListener(type: Int) {
        ToastUtils.showLongToast("订单筛选结果：$type",this)
    }
}