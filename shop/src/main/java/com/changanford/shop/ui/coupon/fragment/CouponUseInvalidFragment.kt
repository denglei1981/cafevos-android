package com.changanford.shop.ui.coupon.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.loadmore.TheHellLoadMoreView
import com.changanford.shop.databinding.BaseRecyclerViewBinding
import com.changanford.shop.databinding.BaseRecyclerViewGrayBinding
import com.changanford.shop.ui.coupon.adapter.CouponCanUseAdapter
import com.changanford.shop.ui.coupon.adapter.CouponUseInvaildAdapter
import com.changanford.shop.ui.coupon.request.CouponViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


class CouponUseInvalidFragment : BaseLoadSirFragment<BaseRecyclerViewGrayBinding, CouponViewModel>(),
    OnRefreshListener {

    val couponCanUseAdapter: CouponUseInvaildAdapter by lazy {
        CouponUseInvaildAdapter()

    }
    private val customLoadMoreView: TheHellLoadMoreView by lazy {
        TheHellLoadMoreView()
    }

    companion object {
        fun newInstance(type: String): CouponUseInvalidFragment {
            val fg = CouponUseInvalidFragment()
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
        binding.recyclerView.adapter = couponCanUseAdapter
        binding.smartLayout.setEnableLoadMore(false)
        couponCanUseAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getCouponList(true, 3)
        }
        couponCanUseAdapter.loadMoreModule.loadMoreView = customLoadMoreView
        viewModel.getCouponList(false, 3)
    }

    override fun initData() {

    }
    override fun onStart() {
        super.onStart()
        viewModel.getCouponList(true, 3)
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
        viewModel.getCouponList(false, 3)
    }

    override fun onRetryBtnClick() {

    }
}