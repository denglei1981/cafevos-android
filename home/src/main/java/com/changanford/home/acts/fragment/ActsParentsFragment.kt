package com.changanford.home.acts.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.databinding.FragmentActsParentBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


class ActsParentsFragment : BaseLoadSirFragment<FragmentActsParentBinding, ActsListViewModel>(),
    OnRefreshListener {

    private var isFirst = true
    private val actsChildFragment by lazy {
        ActsChildListFragment.newInstance()
    }

    companion object {
        fun newInstance(): ActsParentsFragment {
            val fg = ActsParentsFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

//    private val adapter by lazy {
//        ActsMainAdapter(requireContext(), this, requireActivity(), lifecycle)
//    }

    override fun initView() {
        replaceFragment(actsChildFragment)
    }


    override fun initData() {

    }

    override fun onRetryBtnClick() {
    }

    override fun onResume() {
        super.onResume()
//        viewModel.getBanner()
        if (isFirst) {
            isFirst = false
            initMyView()
        }
    }

    private fun initMyView() {
        binding.refreshLayout.setEnableRefresh(true)
        binding.refreshLayout.setOnRefreshListener(this)
        actsChildFragment.initMyView()

    }

    override fun observe() {
        super.observe()
    }

    private fun homeRefersh() {
//        viewModel.getBanner()
//        actsChildFragment.getActList(false)
        actsChildFragment.initMyView()
        binding.refreshLayout.finishRefresh()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(com.changanford.home.R.id.fragment, fragment) //framelayout1使用的是帧布局中的id
        //模拟返回栈
        transaction.addToBackStack(null) //模拟返回栈,再次点击按钮的时候，返回原本的布局，但是我这里没起作用，不知道为什么。
        transaction.commit()
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        homeRefersh()
    }
}