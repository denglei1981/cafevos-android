package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.order.OrderGoodsAdapter
import com.changanford.shop.bean.OrderBean
import com.changanford.shop.databinding.FragmentOrdersgoodsListBinding

/**
 * @Author : wenke
 * @Time : 2021/9/24 0024
 * @Description : OrdersGoodsFragment
 */
class OrdersGoodsFragment:BaseFragment<FragmentOrdersgoodsListBinding,OrderViewModel>() {
    companion object{
        fun newInstance(statesId:String): OrdersGoodsFragment {
            val bundle = Bundle()
            bundle.putString("statesId", statesId)
            val fragment= OrdersGoodsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val mAdapter by lazy { OrderGoodsAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
    }

    override fun initData() {
        val datas= arrayListOf<OrderBean>()
        for (i in 0..15){
            val item=OrderBean(i,"Title$i")
            datas.add(item)
        }
        mAdapter.setList(datas)
    }
}