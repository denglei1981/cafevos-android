package com.changanford.home.acts.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.acts.adapter.ActsMainAdapter
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.databinding.FragmentActsParentBinding

class ActsParentsFragment : BaseLoadSirFragment<FragmentActsParentBinding, ActsListViewModel>() {


    companion object {
        fun newInstance(): ActsParentsFragment {
            val fg = ActsParentsFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    private val adapter by lazy {
        ActsMainAdapter(requireContext(), this, requireActivity())
    }

    override fun initView() {
        binding.refreshLayout.setEnableRefresh(false)
        binding.ryActs.setStickyHeight(0)
        binding.ryActs.adapter = adapter
        adapter.setItems(arrayListOf("", ""))
        binding.ryActs.setStickyListener {
            if (it) {
                adapter.stopViewPagerLoop()
            } else {
                adapter.startViewPagerLoop()
            }
        }
    }

    override fun initData() {
        viewModel.getBanner()
    }
    override fun onRetryBtnClick() {
    }
    override fun onResume() {
        super.onResume()
//        viewModel.getBanner()
        try {
            adapter.startViewPagerLoop()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun observe() {
        super.observe()
        viewModel.bannerLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isSuccess) {
                (parentFragment as HomeV2Fragment).stopRefresh()
                adapter.setViewPagerData(it.data as ArrayList<CircleHeadBean>)
            } else {
                toastShow(it.message)
            }
        })
    }

    fun homeRefersh() {
        viewModel.getBanner()
        adapter.actsChildFragment.getActList(false, adapter.allUnitCode, adapter.allActsCode)
    }

    override fun onPause() {
        super.onPause()
        try {
            adapter.stopViewPagerLoop()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            adapter.stopViewPagerLoop()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}