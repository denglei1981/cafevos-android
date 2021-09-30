package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.adapter.order.AllOrderAdapter
import com.changanford.shop.bean.OrderBean
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
        fun start(context: Context, orderType:Int) {
            context.startActivity(Intent(context, AllOrderActivity::class.java).putExtra("orderType",orderType))
        }
    }
    private val mAdapter by lazy { AllOrderAdapter() }
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
        val datas= arrayListOf<OrderBean>()
        for (i in 0..15){
            val item= OrderBean(i,"Title$i")
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