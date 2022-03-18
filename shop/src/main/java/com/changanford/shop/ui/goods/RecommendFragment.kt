package com.changanford.shop.ui.goods

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.databinding.FragmentRecommendBinding
import com.changanford.shop.ui.compose.RecommendListCompose
import com.changanford.shop.viewmodel.GoodsViewModel

/**
 * @Author : wenke
 * @Time : 2022/3/18
 * @Description : RecommendFragment
 */
class RecommendFragment:BaseFragment<FragmentRecommendBinding,GoodsViewModel>() {
    companion object {
        fun newInstance(kindId: String?): RecommendFragment {
            val bundle = Bundle()
            bundle.putString("kindId", kindId)
            val fragment = RecommendFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private var kindId:String?=null
    override fun initView() {
        binding.srl.apply {
            setEnableLoadMore(false)
            setOnRefreshListener {
                viewModel.getRecommendList(kindId?:"0")
            }
        }
    }

    override fun initData() {
        viewModel.GoodsListBean.observe(this){
            binding.apply {
                composeView.setContent {
                    RecommendListCompose(dataBean = it)
                }
                srl.finishRefresh()
            }
        }
        arguments?.getString("kindId","0")?.apply {
            kindId=this
            binding.srl.autoRefresh()
        }
    }
}