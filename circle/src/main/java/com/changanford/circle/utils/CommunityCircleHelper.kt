package com.changanford.circle.utils

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleDetailsPersonalAdapter
import com.changanford.circle.adapter.circle.CircleDetailsActivityAdapter
import com.changanford.circle.adapter.circle.CircleDetailsNoticeAdapter
import com.changanford.circle.adapter.circle.CircleDetailsTopicAdapter
import com.changanford.circle.adapter.circle.TagAdapter
import com.changanford.circle.databinding.LayoutCircleHomeRyBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.widget.pop.CircleDetailsMenuNewPop
import com.changanford.common.bean.AdBean
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateCircleDetailsData
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.common.widget.control.BannerControl
import com.changanford.common.wutil.ScreenUtils

/**
 * @author: niubobo
 * @date: 2024/3/14
 * @description：
 */
class CommunityCircleHelper(
    private val binding: LayoutCircleHomeRyBinding,
    private val fragment: Fragment,
) {

    private val viewModel = CircleDetailsViewModel()
    private var mCircleId = ""
    private var hasLookNotice = false

    val tagAdapter by lazy { TagAdapter(true) }

    val personalAdapter by lazy {
        CircleDetailsPersonalAdapter(fragment.requireContext())
    }

    //公告
    val noticeAdapter by lazy {
        CircleDetailsNoticeAdapter()
    }

    //活动
    val activityAdapter by lazy {
        CircleDetailsActivityAdapter()
    }

    //话题
    val topicAdapter by lazy {
        CircleDetailsTopicAdapter()
    }
    
    fun initCommunity(circleId: String) {
        this.mCircleId = circleId
        viewModel.getCircleDetails(circleId)
        observeData()
        binding.apply {
            recyclerView.apply {
                adapter = tagAdapter
            }
            ryNotice.adapter = noticeAdapter
            ryActivity.adapter = activityAdapter
            val topicLayoutManager =
                FlexboxLayoutManagerCustom(fragment.requireContext(), 2)
            ryTopic.layoutManager = topicLayoutManager
            ryTopic.adapter = topicAdapter
        }
    }


    private fun observeData() {
        viewModel.circleDetailsBean.observe(fragment) {
            if (it == null) {
                "服务器开小差，请稍候再试".toast()
                return@observe
            }
            binding.vLine2.visibility = View.VISIBLE
            if (!it.wonderfulControls.isNullOrEmpty()) {
                if (it.wonderfulControls.size > 3) {
                    activityAdapter.setList(
                        it.wonderfulControls.subList(
                            0,
                            3
                        )
                    )
                } else {
                    activityAdapter.setList(it.wonderfulControls)
                }
                binding.vLine2.visibility = View.GONE
                binding.clActivity.visibility = View.VISIBLE
            } else {
                binding.clActivity.visibility = View.GONE
            }
            binding.clNotice.isVisible = !it.circleNotices.isNullOrEmpty()
            binding.ivAuth.isVisible = it.manualAuth == 1
            binding.ivAuth.load(it.manualAuthImg)
            if (it.circleNotices.isNullOrEmpty()) {
                binding.vLine2.isVisible = false
                noticeAdapter.setEmptyView(R.layout.empty_notice)
            } else {
                noticeAdapter.setList(it.circleNotices)
            }
            if (it.circleTopics.isNullOrEmpty()) {
                binding.clTopic.visibility = View.GONE
            } else {
                binding.clTopic.visibility = View.VISIBLE
                binding.vLine2.visibility = View.GONE
                topicAdapter.setList(it.circleTopics)
            }
            if (it.isApply == 1) {//审核中
                binding.tvJoinText.isVisible = true
            } else if (it.permissions.isNullOrEmpty()) {//是否有发布权限
                binding.llPost.visibility = View.GONE
            } else {
                it.permissions.forEach { item ->
                    if (item.dictValue == "ANNOUNCEMENT") {
                        hasLookNotice = true
                    }
                }
                binding.llPost.visibility = View.VISIBLE
            }
            initListener(it.circleId.toString(), it.name, hasLookNotice)
            tagAdapter.setList(it.tags)
            binding.run {
                ivIcon.setCircular(12)
                ivIcon.loadImage(it.pic)
                tvTitle.text = it.name
                tvContent.text = it.description
                tvNum.text = "${it.postsCount} 帖子"
                ryPersonal.adapter = personalAdapter
                tvPersonal.text = "${it.userCount}成员"
                personalAdapter.setItems(it.users)
                personalAdapter.notifyDataSetChanged()

                tvPersonal.setOnClickListener { _ ->
                    updateCircleDetailsData("成员页", "成员页")
                    val bundle = Bundle()
                    bundle.putString("circleId", mCircleId)
                    bundle.putString("isApply", it.isApply.toString())
                    startARouter(ARouterCirclePath.PersonalActivity, bundle)
                }
                setBannerList(it.ads)
            }
//            initTabAndViewPager(
//                it.userId.toString(),
//                it.circleId.toString()
//            )
        }
    }


    fun initListener(mCircleId: String, circleName: String, hasLookNotice: Boolean) {

        binding.llPost.setOnClickListener {
            showMenuPop(mCircleId)
        }
        binding.apply {
            tvNoticeMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, mCircleId)
                bundle.putBoolean(IntentKey.HAS_LOOK_NOTICE, hasLookNotice)
                startARouter(ARouterCirclePath.CircleNoticeActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("公告栏", "0", "")
                updateCircleDetailsData("公告栏页", "公告栏页")
            }
            tvTopicMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(IntentKey.TOPIC_TYPE, 1)
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, mCircleId)
                bundle.putString("circleName", circleName)
                startARouter(ARouterCirclePath.HotTopicActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("圈内话题", "0", "")
                updateCircleDetailsData("圈内话题页", "圈内话题页")
            }
            clTopContent.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("circleId", mCircleId)
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }
            tvActivityMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, mCircleId)
                startARouter(ARouterCirclePath.CircleActivityListActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("圈内活动", "0", "")
                updateCircleDetailsData("圈内活动页", "圈内活动页")
            }

        }
        noticeAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = noticeAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, mCircleId)
            bundle.putBoolean(IntentKey.HAS_LOOK_NOTICE, hasLookNotice)
            bundle.putString(IntentKey.NOTICE_ID, bean.noticeId)
            startARouter(ARouterCirclePath.CircleNoticeActivity, bundle)
            GIOUtils.circleDetailPageResourceClick(
                "公告栏",
                (position + 1).toString(),
                bean.noticeName
            )
            updateCircleDetailsData("公告栏页", "公告栏页")
        }
        activityAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = activityAdapter.getItem(position)
            JumpUtils.instans?.jump(bean.jumpDto.jumpCode, bean.jumpDto.jumpVal)
            GIOUtils.circleDetailPageResourceClick(
                "圈内活动",
                (position + 1).toString(),
                bean.title
            )
            updateCircleDetailsData(bean.title, "圈内活动详情页")
        }

        topicAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = topicAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putString("topicId", bean.topicId)
            bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, mCircleId)
            bundle.putString("circleName", circleName)
            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            GIOUtils.circleDetailPageResourceClick(
                "圈内话题",
                (position + 1).toString(),
                bean.name
            )
            updateCircleDetailsData(bean.name, "圈内话题详情页")
        }
    }

    private fun showMenuPop(mCircleId: String) {
        viewModel.circleDetailsBean.value?.permissions?.let {
            CircleDetailsMenuNewPop(fragment.requireContext(), mCircleId, it).run {
                setBlurBackgroundEnable(false)
                showPopupWindow(binding.llPost)
                initData()
            }
        }
    }

    private fun setBannerList(ads: ArrayList<AdBean>) {
        binding.banner.isVisible = ads.isNullOrEmpty()
        BannerControl.bindingBanner(
            binding.banner,
            ads,
            ScreenUtils.dp2px(fragment.requireContext(), 4f), true
        )
    }


}