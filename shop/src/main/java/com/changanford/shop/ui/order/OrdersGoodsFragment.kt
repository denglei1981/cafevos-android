package com.changanford.shop.ui.order

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
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
    override fun initView() {

    }

    override fun initData() {

    }
}