package com.changanford.shop.ui.coupon

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.databinding.ActUseCouponsBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2022/4/8 0008
 * @Description : 使用优惠券
 */
@Route(path = ARouterShopPath.UseCouponsActivity)
class UseCouponsActivity:BaseActivity<ActUseCouponsBinding,GoodsViewModel>(),
    OnRefreshLoadMoreListener {
    companion object{
        fun start(itemBean:CouponsItemBean?) {
            itemBean?.apply {
                val bundle=Bundle()
                bundle.putString("itemBean",Gson().toJson(this))
                startARouter(ARouterShopPath.UseCouponsActivity, bundle)
            }
        }
    }
    private val mAdapter by lazy { GoodsAdapter() }
    private var itemBean: CouponsItemBean?=null
    private var searchKey=""
    private var pageNo=1
    override fun initView() {
        binding.apply {
            topBar.setActivity(this@UseCouponsActivity)
            sml.setOnRefreshLoadMoreListener(this@UseCouponsActivity)
            recyclerView.adapter=mAdapter
        }
        intent.getStringExtra("itemBean")?.apply {
            itemBean=Gson().fromJson(this,CouponsItemBean::class.java)
            getData()
        }
    }
    override fun initData() {
        viewModel.goodsListData.observe(this){
            mAdapter.setList(it?.dataList)
        }
    }
    private fun getData(){
        itemBean?.couponRecordId?.apply {
            viewModel.useCoupons(couponRecordId = this,searchKey=searchKey, pageNo = pageNo)
        }
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