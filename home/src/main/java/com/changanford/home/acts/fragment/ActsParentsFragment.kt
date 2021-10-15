package com.changanford.home.acts.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.utilext.toastShow
import com.changanford.home.acts.adapter.ActsMainAdapter
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.databinding.FragmentActsParentBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class ActsParentsFragment : BaseLoadSirFragment<FragmentActsParentBinding, ActsListViewModel>(),OnRefreshListener {


    companion object {
        fun newInstance(): ActsParentsFragment {
            val fg = ActsParentsFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    private val adapter by lazy {
        ActsMainAdapter(requireContext(), this,requireActivity())
    }
    override fun initView() {
        binding.ryActs.setStickyHeight(0)
        binding.ryActs.adapter = adapter
        adapter.setItems(arrayListOf("",""))
    }
    override fun initData() {
        viewModel.getBanner()
    }

    override fun onRetryBtnClick() {

    }
    override fun observe() {
        super.observe()
        viewModel.bannerLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isSuccess) {
                binding.refreshLayout.finishRefresh()
                adapter.setViewPagerData(it.data as ArrayList<CircleHeadBean>)
            } else {
                toastShow(it.message)
            }
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getBanner()
        adapter.actsChildFragment.getActList(false,adapter.allUnitCode,adapter.allActsCode)
    }
}