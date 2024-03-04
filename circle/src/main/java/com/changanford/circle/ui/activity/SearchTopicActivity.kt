package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.SearchTopicAdapter
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ActivitySearchTopicBinding
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.viewmodel.SearchTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose 话题搜索
 */
@Route(path = ARouterCirclePath.SearchTopicActivity)
class SearchTopicActivity : BaseActivity<ActivitySearchTopicBinding, SearchTopicViewModel>() {

    private var page = 1
    private var section = 0
    private var needCallback: Boolean = false
    private val adapter by lazy {
        SearchTopicAdapter()
    }

    override fun initView() {
        title = "搜索页"
        GioPageConstant.prePageType = "搜索页"
        GioPageConstant.prePageTypeName = "搜索页"
        section = intent.getIntExtra(IntentKey.TOPIC_SECTION, 0)
        AppUtils.setStatusBarMarginTop(binding.layoutSearch.root, this)
        binding.ryTopic.adapter = adapter
        intent.extras?.getBoolean(ChooseConversationActivity.needCallback)?.let {
            needCallback = it
        }
        binding.layoutSearch.cancel.setTextColor(ContextCompat.getColor(this, R.color.color_9916))
        initListener()
    }

    private fun initListener() {
        binding.run {
            layoutSearch.cancel.setOnClickListener { finish() }
            layoutSearch.searchContent.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = v.text.toString()
                    if (content.isEmpty()) {
                        "请输入关键字".toast()
                    } else {
                        page = 1
                        adapter.searchContent = content
                        viewModel.getData(content, section, page)
                        // 埋点
                        BuriedUtil.instant?.circleTopicSearch(content)
                    }
                    HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
                }
                false
            }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            val content = binding.layoutSearch.searchContent.text.toString()
            viewModel.getData(content, section, page)
        }
        adapter.setOnItemClickListener { _, view, position ->
            if (needCallback) {
                val bean = adapter.getItem(position)
                bean.isSearch = true
                LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
                    .postValue(bean)
                if (bean.isBuyCarDiary == 1) {
                    startARouter(ARouterCirclePath.ChooseCarActivity)
                } else {
                    finish()
                }
            } else {
                val bundle = Bundle()
                bundle.putString("topicId", adapter.getItem(position).topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        }
    }

    override fun initData() {
        viewModel.topicBean.observe(this) {
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
        }
        LiveDataBus.get().withs<SpecialCarListBean>(LiveDataBusKey.CHOOSE_CAR_POST).observe(this) {
            finish()
        }
    }
}