package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.HotTopicAdapter
import com.changanford.circle.databinding.ActivityHotTopicBinding
import com.changanford.circle.viewmodel.HotTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose 热门话题
 */
@Route(path = ARouterCirclePath.HotTopicActivity)
class HotTopicActivity : BaseActivity<ActivityHotTopicBinding, HotTopicViewModel>() {

    private val adapter by lazy {
        HotTopicAdapter(this)
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.root, this)
        binding.title.run {
            tvTitle.text = "热门话题"
            ivBack.setOnClickListener { finish() }
        }
        binding.ryTopic.adapter = adapter

        binding.refreshLayout.run {
            setOnRefreshListener {
                viewModel.page = 1
                viewModel.getData()
                finishRefresh()
            }
            setOnLoadMoreListener {
                viewModel.page++
                viewModel.getData()
                finishLoadMore()
            }
        }

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val bundle = Bundle()
                bundle.putString("topicId", adapter.getItem(position)?.topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        })

    }

    override fun initData() {
        viewModel.getData()
    }

    override fun observe() {
        super.observe()
        viewModel.hotTopicBean.observe(this, {
            if (viewModel.page == 1) {
                adapter.setItems(it.dataList)
                adapter.notifyDataSetChanged()
            } else {
                val oldCount = adapter.itemCount
                adapter.getItems()?.addAll(it.dataList)
                adapter.notifyItemRangeChanged(oldCount, adapter.itemCount)
            }
            binding.refreshLayout.setEnableLoadMore(it.dataList.size == 20)
        })
    }
}