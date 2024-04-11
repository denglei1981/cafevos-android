package com.changanford.circle.adapter

import android.content.Context
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleDetailsActivityAdapter
import com.changanford.circle.adapter.circle.CircleDetailsNoticeAdapter
import com.changanford.circle.adapter.circle.CircleDetailsTopicAdapter
import com.changanford.circle.adapter.circle.TagAdapter
import com.changanford.circle.databinding.LayoutMyCircleBinding
import com.changanford.circle.databinding.LayoutMyCircleContentBinding
import com.changanford.circle.ui.fragment.HomeCircleDetailsFragment
import com.changanford.circle.utils.FlexboxLayoutManagerCustom
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.widget.pop.CircleDetailsMenuNewPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateCircleDetailsData
import com.changanford.common.utilext.toIntPx
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleHomeMyAdapter(
    private val fragment: Fragment,
    private val context: Context,
    private val viewModel: CircleDetailsViewModel,
) : BaseAdapter<String>(
    context,
    Pair(R.layout.layout_my_circle_content, 0),
    Pair(R.layout.layout_my_circle, 1)
) {

    val tagAdapter by lazy { TagAdapter(true) }

    val personalAdapter by lazy {
        CircleDetailsPersonalAdapter(context)
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

    lateinit var circleBinding: LayoutMyCircleContentBinding
    private lateinit var circleBottomBinding: LayoutMyCircleBinding

    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                val binding = vdBinding as LayoutMyCircleContentBinding
                circleBinding = binding
                initView()
                binding.ryTopic.adapter = topicAdapter
            }

            1 -> {
                val binding = vdBinding as LayoutMyCircleBinding
                circleBottomBinding = binding
                initMagicIndicator(binding)
            }
        }
    }


    private fun initView() {
        circleBinding.run {
            recyclerView.apply {
                adapter = tagAdapter
            }
            ryNotice.adapter = noticeAdapter
            ryActivity.adapter = activityAdapter
            val topicLayoutManager =
                FlexboxLayoutManagerCustom(context, 2)
            ryTopic.layoutManager = topicLayoutManager
            ryTopic.adapter = topicAdapter
        }
    }

    fun initListener(mCircleId: String, circleName: String, hasLookNotice: Boolean) {

        circleBinding.llPost.setOnClickListener {
            showMenuPop(mCircleId)
        }
        circleBinding.apply {
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


    private fun initMagicIndicator(binding: LayoutMyCircleBinding) {
        val magicIndicator = binding.magicTab
        val commonNavigator = CommonNavigator(context)
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
        circleBottomBinding.apply {
            viewPager.offscreenPageLimit = 3
            viewPager.adapter = CircleMainViewPagerAdapter(fragment, fragmentList)
        }

    }

    private fun showMenuPop(mCircleId: String) {
        viewModel.circleDetailsBean.value?.permissions?.let {
            CircleDetailsMenuNewPop(context, mCircleId, it).run {
                setBlurBackgroundEnable(false)
                showPopupWindow(circleBinding.llPost)
                initData()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}