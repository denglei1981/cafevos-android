package com.changanford.shop.ui.exchange

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.FragmentExchangeBinding
import com.changanford.shop.ui.goods.GoodsDetailsActivity

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class ExchangeListFragment: BaseFragment<FragmentExchangeBinding, ExchangeViewModel>() {
    companion object{
        fun newInstance(itemId:String): ExchangeListFragment {
            val bundle = Bundle()
            bundle.putString("itemId", itemId)
            val fragment= ExchangeListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val adapter by lazy { GoodsAdapter() }
    override fun initView() {
        if(arguments!=null){
            val itemId=arguments?.getString("itemId","0")
        }
    }
    override fun initData() {
//        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
        binding.recyclerView.adapter=adapter
        val datas= arrayListOf<GoodsBean>()
        val title=StringBuffer("Title")
        for (i in 0..30){
            title.append("Title$i>>")
            val item=GoodsBean(i,if(i%3>0)"Title$i" else "$title")
            datas.add(item)
        }
        adapter.setList(datas)
        adapter.setOnItemClickListener { _, _, position ->
            GoodsDetailsActivity.start(requireContext(),"$position")
        }
    }
}