package com.changanford.shop.ui.goods

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsList
import com.changanford.common.buried.WBuriedUtil
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.databinding.FragmentExchangeBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class ExchangeListFragment: BaseFragment<FragmentExchangeBinding, GoodsViewModel>() {
    companion object{
        fun newInstance(itemId:String,tagType:String?=null): ExchangeListFragment {
            val bundle = Bundle()
            bundle.putString("tagId", itemId)
            bundle.putString("tagType", tagType)
            val fragment= ExchangeListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private var parentSmartRefreshLayout: SmartRefreshLayout?=null
    private var pageNo=1
    private val mAdapter by lazy { GoodsAdapter() }
    private var tagId="-1"
    private var tagType:String?=null
    private var isRequest=false
    override fun initView() {
        arguments?.apply{
            tagId=getString("tagId","0")
            tagType=getString("tagType",null)
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType)
            isRequest=true
        }
        viewModel.goodsListData.observe(this) {
            isRequest = false
            bindingData(it)
        }
        binding.smartRl.setOnLoadMoreListener {
            pageNo++
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType)
        }
    }
    override fun initData() {
        binding.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                val price=if(spuPageTagType=="MEMBER_DISCOUNT"||spuPageTagType=="MEMBER_EXCLUSIVE")vipFb else normalFb
                WBuriedUtil.clickShopItem(spuName,price)
                GoodsDetailsActivity.start(getJdType(),getJdValue())
            }
        }
    }
    private fun bindingData(it:GoodsList?){
        if(1==pageNo){
            mAdapter.setList(it?.dataList)
            parentSmartRefreshLayout?.finishRefresh()
        } else if(it?.dataList != null)mAdapter.addData(it.dataList)
        if(null==it||mAdapter.data.size>=it.total)binding.smartRl.setEnableLoadMore(false)
        else binding.smartRl.setEnableLoadMore(true)
        binding.smartRl.finishLoadMore()
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
        if(isAdded&&"-1"!=tagId&&mAdapter.data.size<1&&!isRequest){
            pageNo=1
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType)
        }
    }
}