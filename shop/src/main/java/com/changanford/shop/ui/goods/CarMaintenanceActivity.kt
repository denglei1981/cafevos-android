package com.changanford.shop.ui.goods

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsListBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.control.SortControl
import com.changanford.shop.databinding.ActCarMaintenanceBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2022/6/6
 * @Description : 爱车养护
 */
@Route(path = ARouterShopPath.CarMaintenanceActivity)
class CarMaintenanceActivity:BaseActivity<ActCarMaintenanceBinding,GoodsViewModel>(),
    SortControl.OnSelectSortListener, OnRefreshLoadMoreListener {
    private var pageNo=1
    private val mAdapter by lazy { GoodsAdapter() }
    private var sortControl: SortControl?=null
    private var mallSortType="COMPREHENSIVE"
    private var ascOrDesc="DESC"
    private val smartRl by lazy { binding.layoutList.smartRl }
    private var tagId="-1"
    override fun initView() {
        binding.apply {
            topBar.setActivity(this@CarMaintenanceActivity)
            val viewArr = arrayOf(layoutList.inSort.rb0,
                layoutList.inSort.rb1,
                layoutList.inSort.rb2)
            sortControl = SortControl(this@CarMaintenanceActivity, viewArr, this@CarMaintenanceActivity)
            smartRl.setOnRefreshLoadMoreListener(this@CarMaintenanceActivity)
        }
    }
    override fun initData() {
        binding.layoutList.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                val price=if(spuPageTagType=="MEMBER_DISCOUNT"||spuPageTagType=="MEMBER_EXCLUSIVE")vipFb else normalFb
                WBuriedUtil.clickShopItem(spuName,price)
                GoodsDetailsActivity.start(getJdType(),getJdValue())
            }
        }
        viewModel.goodsListData.observe(this) {
            bindingData(it)
        }
        getData(true)
    }
    private fun getData(showLoading:Boolean=false){
        viewModel.getMaintenanceGoodsList(tagId,pageNo,mallSortType=mallSortType, ascOrDesc = ascOrDesc,showLoading=showLoading)
    }
    private fun bindingData(it: GoodsListBean?){
        if(1==pageNo)mAdapter.setList(it?.dataList)
        else if(it?.dataList != null)mAdapter.addData(it.dataList)
        if(null==it||mAdapter.data.size>=it.total)smartRl.setEnableLoadMore(false)
        else smartRl.setEnableLoadMore(true)
        smartRl.finishRefresh()
        smartRl.finishLoadMore()
    }
    override fun onSelectSortListener(mallSortType: String, ascOrDesc: String) {
        this.mallSortType=mallSortType
        this.ascOrDesc=ascOrDesc
        pageNo=1
        getData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        getData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        getData()
    }
}