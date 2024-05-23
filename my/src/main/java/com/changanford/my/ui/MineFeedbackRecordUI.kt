package com.changanford.my.ui

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.FeedbackMineListItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.UiMineFeedbackRecordBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 * @Author: lcw
 * @Date: 2020/9/1
 * @Des: 意见反馈列表
 */
@Route(path = ARouterMyPath.MineFeedbackListUI)
class MineFeedbackRecordUI :
    BaseMineUI<UiMineFeedbackRecordBinding, SignViewModel>() {

    private var pageNo = 1

    private val mAdapter by lazy {
        MineCommAdapter.MineFeedbackRecordAdapter(this, viewModel)
    }


    override fun initView() {
        setLoadSir(binding.refreshLayout)
        binding.adToolbar.toolbarTitle.text = "反馈记录"
        binding.adToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        /**
         * 不需要常见问题
         */
//        binding.adToolbar.toolbarSave.text = "常见问题"
//        binding.adToolbar.toolbarSave.setOnClickListener {
//            startARouter(ARouterMyPath.MineFeedbackUI)
//        }
        binding.submit.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MineEditFeedbackUI::class.java
                ).putExtra("isBack", true)
            )
        }
        binding.refreshRv.run {
            layoutManager = LinearLayoutManager(this@MineFeedbackRecordUI)
            adapter = mAdapter
        }
        binding.refreshLayout.setOnRefreshListener {
            pageNo = 1
            getData(true)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            pageNo++
            getData(false)
        }

        LiveDataBus.get().with(LiveDataBusKey.MINE_SUBMIT_FEEDBACK_SUCCESS, Boolean::class.java)
            .observe(this, Observer {
                if (it) {
//                    if (mAdapter.getItems()!!.size == (pageNo * 20)) {
//                        pageNo++
//                    }
//                    addData()
                    pageNo = 1
                    getData(true)
                }
            })
    }

    var isFresh = false
    override fun initData() {
        pageNo = 1
        getData(true)
        viewModel._feedbackMineListBean.observe(this) {
            val list = it.dataList as ArrayList
            if (isFresh) {
                mAdapter.getItems()?.clear()
                if (list.isNullOrEmpty()) {
                    showEmptyLoadView()
                } else {
                    showContent()
                }
                mAdapter.setItems(list)
                binding.refreshLayout.finishRefresh()
            } else {
                mAdapter.getItems()?.addAll(list)
                binding.refreshLayout.finishLoadMore()
            }
            mAdapter.notifyDataSetChanged()
            val canLoadMore = it.total > mAdapter.getItems()!!.size
            binding.refreshLayout.setEnableLoadMore(canLoadMore)
        }
    }

    private fun getData(isFresh: Boolean) {
        this.isFresh = isFresh
        viewModel.getMineFeedback(pageNo)

    }


    //比较两个list
    //取出存在menuOneList中，但不存在resourceList中的数据，差异数据放入differentList
    private fun listCompare(
        menuOneList: ArrayList<FeedbackMineListItem>,
        resourceList: ArrayList<FeedbackMineListItem>
    ): ArrayList<FeedbackMineListItem> {
        val map: MutableMap<FeedbackMineListItem, Int> = HashMap(resourceList.size)
        val differentList: ArrayList<FeedbackMineListItem> = ArrayList()
        for (resource in resourceList) {
            map[resource] = 1
        }
        for (resource1 in menuOneList) {
            if (map[resource1] == null) {
                differentList.add(resource1)
            }
        }
        return differentList
    }
}
