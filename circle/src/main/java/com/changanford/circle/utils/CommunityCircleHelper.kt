package com.changanford.circle.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleDetailsPersonalAdapter
import com.changanford.circle.adapter.CircleMainViewPagerAdapter
import com.changanford.circle.adapter.circle.CircleDetailsActivityAdapter
import com.changanford.circle.adapter.circle.CircleDetailsNoticeAdapter
import com.changanford.circle.adapter.circle.CircleDetailsTopicAdapter
import com.changanford.circle.adapter.circle.TagAdapter
import com.changanford.circle.databinding.LayoutCircleHomeRyBinding
import com.changanford.circle.ui.fragment.HomeCircleDetailsFragment
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.widget.pop.CircleDetailsMenuNewPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
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
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.common.widget.control.BannerControl
import com.changanford.common.wutil.ScreenUtils
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

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
//    private val circleSquareAdapter by lazy {
//        CircleHomeMyAdapter(
//            fragment,
//            fragment.requireContext(),
//            viewModel,
//        )
//    }
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


//    val topFragments = arrayListOf(
//        CircleMainFragment.newInstance("0"),
//        CircleMainFragment.newInstance("1")
//    )

    private var mCircleId = ""
    private var hasLookNotice = false

    fun initCommunity(circleId: String) {
        val list = arrayListOf("", "")
//        setItems(list)
//        binding.ryCircle.apply {
//            val linearLayoutManager=binding.ryCircle.layoutManager as LinearLayoutManager
//            linearLayoutManager.isSmoothScrollbarEnabled = true;
//            linearLayoutManager.isAutoMeasureEnabled = true;
//        }
//        binding.ryCircle.adapter = circleSquareAdapter
        this.mCircleId = circleId
        viewModel.getCircleDetails(circleId)
        observeData()
        initMagicIndicator()
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
            initTabAndViewPager(
                it.userId.toString(),
                it.circleId.toString()
            )
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

    fun initTabAndViewPager(userId: String, circleId: String) {
        val fragmentList = ArrayList<Fragment>()
        viewModel.circleType.forEachIndexed { index, s ->
            val fragment = HomeCircleDetailsFragment.newInstance(
                viewModel.circleType[index],
                "",
                circleId, userId
            )
            fragmentList.add(fragment)
        }
        binding.apply {
            viewPager.offscreenPageLimit = 3
            viewPager.adapter = CircleMainViewPagerAdapter(fragment, fragmentList)
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

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        val commonNavigator = CommonNavigator(fragment.requireContext())
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return viewModel.tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = viewModel.tabList[index]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setPadding(12.toIntPx(), 0, 12.toIntPx(), 10)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_8016)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.color_1700F4)
                simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight =
                    UIUtil.dip2px(context, 0.0).toFloat()
                indicator.lineWidth =
                    UIUtil.dip2px(context, 32.0).toFloat()
                indicator.roundRadius =
                    UIUtil.dip2px(context, 0.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(
                    ContextCompat.getColor(
                        context,
                        R.color.color_1700F4
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                magicIndicator.onPageScrollStateChanged(state)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                magicIndicator.onPageSelected(position)
            }

        })
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