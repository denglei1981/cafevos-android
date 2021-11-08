package com.changanford.shop.ui.exchange

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsList
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.databinding.FragmentExchangeBinding
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class ExchangeListFragment: BaseFragment<FragmentExchangeBinding, GoodsViewModel>() {
    companion object{
        fun newInstance(itemId:String): ExchangeListFragment {
            val bundle = Bundle()
            bundle.putString("typeId", itemId)
            val fragment= ExchangeListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private var parentSmartRefreshLayout: SmartRefreshLayout?=null
    private var pageNo=1
    private val mAdapter by lazy { GoodsAdapter() }
    private var typeId="-1"
    private var isRequest=false
    override fun initView() {
        if(arguments!=null){
            typeId=arguments?.getString("typeId","0")!!
            viewModel.getGoodsList(typeId,pageNo)
            isRequest=true
        }
        viewModel.goodsListData.observe(this,{
            isRequest=false
            bindingData(it)
        })
        binding.smartRl.setOnLoadMoreListener {
            pageNo++
            viewModel.getGoodsList(typeId,pageNo)
        }
    }
    override fun initData() {
        binding.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemClickListener { _, _, position ->
            val itemData=mAdapter.data[position]
            GoodsDetailsActivity.start(itemData.mallMallSpuId)
        }
    }
    private fun bindingData(it:GoodsList?){
        if(1==pageNo){
            mAdapter.setList(it?.dataList)
            parentSmartRefreshLayout?.finishRefresh()
        } else if(it?.dataList != null)mAdapter.addData(it.dataList)

        if(null==it||mAdapter.data.size>=it.total)binding.smartRl.setEnableLoadMore(false)
        else {
            binding.smartRl.finishLoadMore()
            binding.smartRl.setEnableLoadMore(true)
        }
//        if(null==it|| it.dataList.isEmpty())pageNo--
//        binding.smartRl.finishLoadMore()
    }
    fun setParentSmartRefreshLayout(parentSmartRefreshLayout:SmartRefreshLayout?){
        this.parentSmartRefreshLayout=parentSmartRefreshLayout
    }
    /**
     * 切换tab时如果当前fragment 没有数据则自动刷新
    * */
    fun startRefresh(){
        if(isAdded&&"-1"!=typeId&&mAdapter.data.size<1&&!isRequest){
            pageNo=1
            viewModel.getGoodsList(typeId,pageNo)
        }
    }
}