package com.changanford.shop.ui.exchange

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.changanford.common.basic.BaseFragment
import com.changanford.shop.adapter.goods.GoodsAdatpter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.InListBinding
import com.changanford.shop.ui.goods.GoodsDetailsActivity

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class ExchangeListFragment: BaseFragment<InListBinding, ExchangeViewModel>() {
    companion object{
        fun newInstance(itemId:String): ExchangeListFragment {
            val bundle = Bundle()
            bundle.putString("itemId", itemId)
            val fragment= ExchangeListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val adapter by lazy { GoodsAdatpter() }
    override fun initView() {
        if(arguments!=null){
            val itemId=arguments?.getString("itemId","0")
        }
    }
    override fun initData() {
//        binding.rvList.layoutManager= StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        binding.rvList.layoutManager= GridLayoutManager(requireContext(),2)
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
        binding.rvList.adapter=adapter
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