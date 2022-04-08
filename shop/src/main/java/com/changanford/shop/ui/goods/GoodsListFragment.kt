package com.changanford.shop.ui.goods

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsListBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.control.SortControl
import com.changanford.shop.databinding.FragmentExchangeBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class GoodsListFragment: BaseFragment<FragmentExchangeBinding, GoodsViewModel>(),
    SortControl.OnSelectSortListener {
    companion object{
        fun newInstance(itemId:String,tagType:String?=null): GoodsListFragment {
            val bundle = Bundle()
            bundle.putString("tagId", itemId)
            bundle.putString("tagType", tagType)
            val fragment= GoodsListFragment()
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
    private var sortControl:SortControl?=null
    private var mallSortType="COMPREHENSIVE"
    private var ascOrDesc="DESC"
    override fun initView() {
        arguments?.apply{
            tagId=getString("tagId","0")
            tagType=getString("tagType",null)
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType,mallSortType=mallSortType, ascOrDesc = ascOrDesc)
            isRequest=true
        }
        sortControl=SortControl(requireContext(),binding,this)
        viewModel.goodsListData.observe(this) {
            isRequest = false
            bindingData(it)
        }
        binding.smartRl.setOnLoadMoreListener {
            pageNo++
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType,mallSortType=mallSortType, ascOrDesc = ascOrDesc)
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
    private fun bindingData(it: GoodsListBean?){
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
            viewModel.getGoodsList(tagId,pageNo,tagType=tagType,mallSortType=mallSortType, ascOrDesc = ascOrDesc)
        }
    }
    /**
     * 排序选中的回调
    * */
    override fun onSelectSortListener(mallSortType: String, ascOrDesc: String) {
        this.mallSortType=mallSortType
        this.ascOrDesc=ascOrDesc
        pageNo=1
        viewModel.getGoodsList(tagId,pageNo,tagType=tagType,mallSortType=mallSortType, ascOrDesc = ascOrDesc)
    }

}