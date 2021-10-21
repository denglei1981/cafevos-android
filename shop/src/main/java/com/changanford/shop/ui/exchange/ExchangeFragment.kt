package com.changanford.shop.ui.exchange

import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.ExchangeAdapter
import com.changanford.shop.databinding.FragmentShopExchangeBinding
import com.changanford.shop.viewmodel.GoodsViewModel

/**
 * @Author : wenke
 * @Time : 2021/9/8
 * @Description : ExchangeFragment
 */
class ExchangeFragment: BaseFragment<FragmentShopExchangeBinding, GoodsViewModel>() {
    companion object {
        fun newInstance(): ExchangeFragment {
            return ExchangeFragment()
        }
    }
    private val adapter by lazy { ExchangeAdapter(requireContext(),requireActivity()) }
    override fun initView() {
        adapter.setItems(arrayListOf("", ""))
        binding.prvShopExchange.adapter = adapter
        binding.prvShopExchange.setStickyListener {
        }
    }
    override fun initData() {
    }
}