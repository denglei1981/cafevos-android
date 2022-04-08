package com.changanford.shop.ui.coupon

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.HideKeyboardUtil
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
    OnRefreshLoadMoreListener, TextView.OnEditorActionListener {
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
            edtSearch.setOnEditorActionListener(this@UseCouponsActivity)
        }
        intent.getStringExtra("itemBean")?.apply {
            itemBean=Gson().fromJson(this,CouponsItemBean::class.java).apply {
                binding.tvCouponsDes.text=when(discountType){
                    //折扣
                    "DISCOUNT"->"限时促销：以下商品可使用满${conditionMoney}打${couponRatio}折优惠券，最多减${couponMoney}"
                    //满减
                    "FULL_MINUS"->"限时促销：以下商品可使用满${conditionMoney}减${couponMoney}优惠券"
                    //立减
                    "LEGISLATIVE_REDUCTION"->"限时促销：以下商品可使用立减${couponMoney}优惠券"

                    else ->""
                }
            }
            getData()
        }
    }
    override fun initData() {
        viewModel.goodsListData.observe(this){
            mAdapter.setList(it?.dataList)
            if(1==pageNo)mAdapter.setList(it?.dataList)
            else if(it?.dataList != null)mAdapter.addData(it.dataList)

            if(null==it||mAdapter.data.size>=it.total)binding.sml.setEnableLoadMore(false)
            else binding.sml.setEnableLoadMore(true)

            binding.sml.finishLoadMore()
            binding.sml.finishRefresh()
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

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            HideKeyboardUtil.hideKeyboard(binding.edtSearch.windowToken)
            searchKey = v.text.toString()
            getData()
        }
        return false
    }
}