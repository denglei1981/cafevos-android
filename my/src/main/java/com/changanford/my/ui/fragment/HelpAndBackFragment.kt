package com.changanford.my.ui.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.FragmentHelpAndBackBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 * @author: niubobo
 * @date: 2024/5/11
 * @description：
 */
class HelpAndBackFragment : BaseLoadSirFragment<FragmentHelpAndBackBinding, SignViewModel>() {

    private var adapter = MineCommAdapter.FeedbackAdapter(R.layout.item_feedback_list)
    private var position = 0
    private var page = 1

    companion object {
        //0取5条 1全部取
        fun newInstance(position: Int): HelpAndBackFragment {
            val bundle = Bundle()
            bundle.putInt("position", position)
            val medalFragment = HelpAndBackFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun onRetryBtnClick() {
        initData()
    }

    override fun initView() {
        setLoadSir(binding.refreshLayout)
        arguments?.let {
            position = it.getInt("position")
            if (position == 0) {
                adapter.isShowContent = true
            }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            initData()
        }
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
        }
        binding.ryHelpAndBack.adapter = adapter
    }

    override fun observe() {
        super.observe()
        viewModel._feedBackBean.observe(this) {
            showContent()
            if (position == 0) {
                if (it.dataList.isNullOrEmpty()) {
                    showEmpty()
                    return@observe
                }
                val useData = if (it.dataList!!.size > 5) {
                    it.dataList?.subList(0, 5)
                } else {
                    it.dataList
                }
                adapter.setList(useData)
                binding.refreshLayout.finishRefresh()
            } else {
                if (page == 1) {
                    binding.refreshLayout.finishRefresh()
                    adapter.setList(it.dataList)
                    if (it.dataList?.size == 0) {
                        showEmpty()
                    }
                } else {
                    it.dataList?.let { it1 -> adapter.addData(it1) }
                    adapter.loadMoreModule.loadMoreComplete()
                }
            }
            if (it.dataList?.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
    }

    override fun initData() {
        viewModel.getFeedbackPage(page)
    }
}