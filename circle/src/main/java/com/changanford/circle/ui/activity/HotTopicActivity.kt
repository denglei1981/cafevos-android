package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.HotMainTopicAdapter
import com.changanford.circle.adapter.HotTopicAdapter
import com.changanford.circle.databinding.ActivityHotTopicBinding
import com.changanford.circle.viewmodel.HotTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose 热门话题、圈内话题(IntentKey.TOPIC_TYPE==1圈内话题、0热门话题)
 */
@Route(path = ARouterCirclePath.HotTopicActivity)
class HotTopicActivity : BaseActivity<ActivityHotTopicBinding, HotTopicViewModel>() {

    private val adapter by lazy {
        HotMainTopicAdapter()
    }

    private var type = 0
    private var circleId :String?=null

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.root, this)
        type = intent.getIntExtra(IntentKey.TOPIC_TYPE, 0)
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID)
        binding.title.run {
            when (type) {
                0 -> {
                    tvTitle.text = "热门话题"
                }
                1 -> {
                    tvTitle.text = "圈内话题"
                }
            }
            ivBack.setOnClickListener { finish() }
        }
        binding.ryTopic.adapter = adapter
        binding.tvSearch.setOnClickListener {
            startARouter(ARouterCirclePath.SearchTopicActivity)
        }

        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.page++
            viewModel.getData(circleId)
        }
        adapter.setOnItemClickListener { _, view, position ->
            // 埋点
            BuriedUtil.instant?.circleHotTopicClick(adapter.getItem(position).name)
            val bundle = Bundle()
            bundle.putString("topicId", adapter.getItem(position).topicId.toString())
            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)

        }

    }

    override fun initData() {
        viewModel.getData(circleId)
    }

    override fun observe() {
        super.observe()
        viewModel.hotTopicBean.observe(this) {
            if (viewModel.page == 1) {
                if (it.dataList.size == 0) {
                    adapter.setEmptyView(R.layout.circle_empty_layout)
                }
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}