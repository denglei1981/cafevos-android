package com.changanford.shop.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.adapter.order.OrderEvaluationAdapter
import com.changanford.shop.databinding.ActPostEvaluationBinding
import com.changanford.shop.viewmodel.OrderViewModel

/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2022/4/9
 * Update Time:
 * Note:订单评价、追评
 */
@Route(path = ARouterShopPath.PostEvaluationActivity)
class PostEvaluationActivity:BaseActivity<ActPostEvaluationBinding, OrderViewModel>() {
    companion object{
        fun start(orderNo:String) {
            JumpUtils.instans?.jump(112,orderNo)
        }
    }
    private val mAdapter by lazy { OrderEvaluationAdapter() }
    override fun initView() {
        binding.apply {
            topBar.setActivity(this@PostEvaluationActivity)
            recyclerView.adapter=mAdapter
        }
    }

    override fun initData() {
        intent.getStringExtra("orderNo")?.apply {
            viewModel.getOrderDetail(this)
        }
        viewModel.orderItemLiveData.observe(this){
            mAdapter.setList(it.skuList)
        }
    }
}