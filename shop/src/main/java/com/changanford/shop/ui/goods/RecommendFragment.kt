package com.changanford.shop.ui.goods

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.databinding.FragmentRecommendBinding
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

    }

    override fun initData() {
        viewModel.GoodsListBean.observe(this){

        }
        arguments?.getString("kindId","0")?.apply {
            kindId=this
            viewModel.getRecommendList(this)
        }
    }
}