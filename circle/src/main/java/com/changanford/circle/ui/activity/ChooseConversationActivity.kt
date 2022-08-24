package com.changanford.circle.ui.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.HotTopicAdapter
import com.changanford.circle.bean.ButtomTypeBean
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ChooseconversationBinding
import com.changanford.circle.ui.activity.ChooseConversationActivity.Companion.needCallback
import com.changanford.circle.viewmodel.HotTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey

/**
 * 选择话题
 */
@Route(path = ARouterCirclePath.ChooseConversationActivity)
class ChooseConversationActivity : BaseActivity<ChooseconversationBinding, HotTopicViewModel>() {
    private var needBack: Boolean = false
    private val adapter by lazy {
        HotTopicAdapter()
    }

    companion object {
        var needCallback: String = "callback"
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "选择话题"

        intent.extras?.getBoolean(needCallback)?.let {
            needBack = it
        }
        binding.tvsearch.setOnClickListener {

            startARouter(ARouterCirclePath.SearchTopicActivity, Bundle().apply {
                putBoolean(needCallback, true)
            })
        }
        binding.recconvers.layoutManager = LinearLayoutManager(this)
        binding.recconvers.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.page++
            viewModel.getData()
        }
        adapter.setOnItemClickListener { _, view, position ->
            LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
                .postValue(adapter.getItem(position))
            finish()
        }
        binding.tvNocy.setOnClickListener {
            finish()
        }
        binding.title.barImgBack.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        viewModel.getData()
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

    override fun observe() {
        super.observe()
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java).observe(this,
            Observer {
                finish()
            })
    }
}