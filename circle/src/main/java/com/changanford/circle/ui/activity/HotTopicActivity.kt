package com.changanford.circle.ui.activity

import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.HotMainTopicAdapter
import com.changanford.circle.databinding.ActivityHotTopicBinding
import com.changanford.circle.viewmodel.HotTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateMainGio
import com.gyf.immersionbar.ImmersionBar
import kotlin.math.abs

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

    private var isWhite = true//是否是白色状态
    private var type = 0
    private var section = 0
    private var circleId: String? = null
    private var circleName: String? = null

    override fun onResume() {
        super.onResume()
        GIOUtils.topicListPageView()
        if (type == 0) {
            updateMainGio("热门话题页", "热门话题页")
        }
    }

    override fun initView() {
        isDarkFont = false
        title = "话题列表页"
        AppUtils.setStatusBarPaddingTop(binding.toolbar, this)
        type = intent.getIntExtra(IntentKey.TOPIC_TYPE, 0)
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID)
        circleName = intent.getStringExtra("circleName")
        binding.titleTv.run {
            when (type) {
                0 -> {
                    binding.tvTopName.text = "热门话题"
                    text = "热门话题"
                    section = 0
                }

                1 -> {
                    binding.tvTopName.text = "圈内话题"
                    text = "圈内话题"
                    section = 1
                }
            }

        }
        binding.backImg.setOnClickListener { finish() }
        binding.ryTopic.adapter = adapter
        binding.tvSearch.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(IntentKey.TOPIC_SECTION, section)
            startARouter(ARouterCirclePath.SearchTopicActivity, bundle)
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
            circleId?.let {
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                bundle.putString("circleName", circleName)
            }

            when (type) {
                0 -> {
                    GioPageConstant.topicEntrance = "热门话题列表页"
                }

                1 -> {
                    GioPageConstant.topicEntrance = "圈内话题列表页"
                }
            }
            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)

        }

        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 4.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.9F && !isWhite) {
                binding.backImg.setColorFilter(Color.parseColor("#ffffff"))
                ImmersionBar.with(this).statusBarDarkFont(false).init()
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.9F && isWhite) {
                binding.backImg.setColorFilter(Color.parseColor("#000000"))
                ImmersionBar.with(this).statusBarDarkFont(true).init()
                //图片变色
                isWhite = false
            }
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.toolbar.background.mutate().alpha = mAlpha
                binding.titleTv.alpha = mAlpha / 255.0F
            } else {
                binding.toolbar.background.mutate().alpha = 255
                binding.titleTv.alpha = 1.0F
            }
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