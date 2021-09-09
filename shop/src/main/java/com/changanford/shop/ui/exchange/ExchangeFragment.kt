package com.changanford.shop.ui.exchange

import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.ExchangeAdapter
import com.changanford.shop.databinding.FragmentShopExchangeBinding

/**
 * @Author : wenke
 * @Time : 2021/9/8 0008
 * @Description : ExchangeFragment
 */
class ExchangeFragment: BaseFragment<FragmentShopExchangeBinding, ExchangeViewModel>() {
    companion object {
        fun newInstance(): ExchangeFragment {
            return ExchangeFragment()
        }
    }
    private val adapter by lazy { ExchangeAdapter(requireContext(),requireActivity()) }
    override fun initView() {
        adapter.setItems(arrayListOf("", ""))
        binding.prvShopExchange.setStickyHeight(20)
        binding.prvShopExchange.adapter = adapter
    }
    override fun initData() {
    }
}