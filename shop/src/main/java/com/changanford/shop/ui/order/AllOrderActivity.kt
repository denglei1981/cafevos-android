package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.adapter.order.AllOrderAdapter
import com.changanford.shop.bean.OrderBean
import com.changanford.shop.databinding.ActOrderAllBinding

/**
 * @Author : wenke
 * @Time : 2021/9/26
 * @Description : 所有订单
 */
class AllOrderActivity:BaseActivity<ActOrderAllBinding,OrderViewModel>() {
    companion object{
        fun start(context: Context, orderType:Int) {
            context.startActivity(Intent(context, AllOrderActivity::class.java).putExtra("orderType",orderType))
        }
    }
    private val mAdapter by lazy { AllOrderAdapter() }
    override fun initView() {
        binding.topBar.setActivity(this)
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
}