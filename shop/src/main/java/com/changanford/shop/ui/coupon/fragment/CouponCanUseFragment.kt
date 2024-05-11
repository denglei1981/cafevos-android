package com.changanford.shop.ui.coupon.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.utilext.toastShow
import com.changanford.shop.R
import com.changanford.shop.databinding.BaseRecyclerViewGrayBinding
import com.changanford.shop.ui.coupon.adapter.CouponCanUseAdapter
import com.changanford.shop.ui.coupon.request.CouponViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


class CouponCanUseFragment : BaseLoadSirFragment<BaseRecyclerViewGrayBinding, CouponViewModel>(),
    OnRefreshListener {

    private val couponCanUseAdapter: CouponCanUseAdapter by lazy {
        CouponCanUseAdapter()
    }

    companion object {
        fun newInstance(type: String): CouponCanUseFragment {
            val fg = CouponCanUseFragment()
            val bundle = Bundle()
//            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
//            bundle.putString(JumpConstant.SEARCH_TAG_ID,tagId)
            fg.arguments = bundle

            return fg
        }
    }

    override fun initView() {

        setLoadSir(binding.smartLayout)
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setEnableLoadMore(false)
        binding.recyclerView.adapter = couponCanUseAdapter
        couponCanUseAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getCouponList(true, 1)
        }


        viewModel.getCouponList(false, 1)
        couponCanUseAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.iv_extends->{

                }
            }
        }

    }

    override fun initData() {

    }

    override fun onStart() {
        super.onStart()
        viewModel.getCouponList(true, 1)
    }

    override fun observe() {
        super.observe()
        viewModel.couponListLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    couponCanUseAdapter.loadMoreModule.loadMoreComplete()
                    it.data.dataList?.let { it1 -> couponCanUseAdapter.addData(it1) }
                } else {
                    if(it.data==null||it.data.dataList==null||it.data.dataList!!.size==0){
                        showEmpty()
                    }else{
                        showContent()
                    }
                    binding.smartLayout.finishRefresh()
                    couponCanUseAdapter.setNewInstance(it.data.dataList)
                }
                if (it.data.dataList == null || it.data.dataList?.size!! < 20) {
                    couponCanUseAdapter.loadMoreModule.loadMoreEnd()

                }
            } else {
                toastShow(it.message)
            }
        })
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getCouponList(false, 1)
    }

    override fun onRetryBtnClick() {

    }
}