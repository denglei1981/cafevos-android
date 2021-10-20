package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.HotTopicAdapter
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ActivitySearchTopicBinding
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.viewmodel.SearchTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose 话题搜索
 */
@Route(path = ARouterCirclePath.SearchTopicActivity)
class SearchTopicActivity : BaseActivity<ActivitySearchTopicBinding, SearchTopicViewModel>() {

    private var page = 1
    private var needCallback:Boolean =false
    private val adapter by lazy {
        HotTopicAdapter()
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.llTitle, this)
        binding.ryTopic.adapter = adapter
        intent.extras?.getBoolean(ChooseConversationActivity.needCallback)?.let {
            needCallback =it
        }
        initListener()
    }

    private fun initListener() {
        binding.run {
            tvCancel.setOnClickListener { finish() }
            tvSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = v.text.toString()
                    if (content.isEmpty()) {
                        "请输入关键字".toast()
                    } else {
                        page = 1
                        viewModel.getData(content, page)
                    }
                    HideKeyboardUtil.hideKeyboard(tvSearch.windowToken)
                }
                false
            }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            val content = binding.tvSearch.text.toString()
            viewModel.getData(content, page)
        }
        adapter.setOnItemClickListener { _, view, position ->
            if (needCallback){
                LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
                    .postValue(adapter.getItem(position))
                finish()
            }else{
                val bundle = Bundle()
                bundle.putString("topicId", adapter.getItem(position).topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        }
    }

    override fun initData() {
        viewModel.topicBean.observe(this, {
            if (page == 1) {
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
        })
    }
}