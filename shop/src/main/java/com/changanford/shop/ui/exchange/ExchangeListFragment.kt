package com.changanford.shop.ui.exchange

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
        fun newInstance(): ExchangeListFragment {
            return ExchangeListFragment()
        }
    }
    private val adatpter by lazy { GoodsAdatpter() }
    override fun initView() {}
    override fun initData() {
        binding.rvList.layoutManager= StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        adatpter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
        binding.rvList.adapter=adatpter
        val datas= arrayListOf<GoodsBean>()
        val title=StringBuffer("Title")
        for (i in 0..30){
            title.append("Title$i>>")
            val item=GoodsBean(i,if(i%3>0)"Title$i" else "$title")
            datas.add(item)
        }
        adatpter.setList(datas)
        adatpter.setOnItemChildClickListener { _, _, position ->
            GoodsDetailsActivity.start(requireContext(),"$position")
        }
    }
}