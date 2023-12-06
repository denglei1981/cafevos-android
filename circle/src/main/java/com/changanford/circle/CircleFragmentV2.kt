package com.changanford.circle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.circle.adapter.CircleMainViewPagerAdapter
import com.changanford.circle.databinding.FragmentCircleV2Binding
import com.changanford.circle.ui.ask.fragment.AskRecommendFragment
import com.changanford.circle.ui.fragment.CircleSquareFragment
import com.changanford.circle.ui.fragment.circle.NewCircleFragment
import com.changanford.circle.utils.GlideImageLoader
import com.changanford.circle.viewmodel.CircleViewModel
import com.changanford.circle.widget.assninegridview.AssNineGridView
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseFragment
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath.SplashActivity
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.BindDialog
import com.changanford.common.ui.dialog.PostDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.MColor
import com.changanford.common.utilext.toIntPx
import com.changanford.common.widget.pop.PermissionTipsPop
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.lang.reflect.Field


/**
 * 社区
 */
class CircleFragmentV2 : BaseFragment<FragmentCircleV2Binding, CircleViewModel>() {

    private var postEntity: ArrayList<PostEntity>? = null//草稿

    private val tabList = listOf("广场", "圈子", "问答")


    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (null != savedInstanceState) {
            ARouter.getInstance().build(SplashActivity)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation()
        }
    }

    override fun initView() {
        initTabAndViewPager()
        binding.ivMenu.post {
            initMagicIndicator()
        }
        easyViewPager()
        bus()
        AppUtils.setStatusBarMarginTop(binding.rlTitle, requireActivity())
        PostDatabase.getInstance(requireActivity()).getPostDao().findAll().observe(this,
            {
                postEntity = it as ArrayList<PostEntity>
            })
        binding.ivMenu.setOnClickListener {
            if (MConstant.token.isNotEmpty()) {
                if (!MineUtils.getBindMobileJumpDataType()) {
                    if (postEntity?.size == 0) {
                        showMenuPop()
                    } else {
                        activity?.let { it1 ->
                            PostDialog(
                                it1,
                                "发现您还有草稿未发布",
                                postButtonListener = object : PostDialog.PostButtonListener {
                                    override fun save() { //继续编辑 2 图片 3 视频 4 图文长帖
                                        val postEntity = postEntity?.last()
                                        when (postEntity?.type) {
                                            "2" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.PostActivity)
                                            }

                                            "3" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.VideoPostActivity)
                                            }

                                            "4" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.LongPostAvtivity)
                                            }
                                        }
                                    }

                                    override fun cancle() {  //不使用草稿
                                        showMenuPop()
                                    }


                                }).show()
                        }

                    }
                } else {
                    BindDialog(binding.ivMenu.context).show()
                }
            } else {
                startARouter(ARouterMyPath.SignUI)
            }

        }
        binding.tvSearch.setOnClickListener {
            if (binding.viewPager.currentItem == 2) {
                JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_ASK.toString())
            } else {
                JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_POST.toString())
            }
        }
        viewModel.getInitQuestion()
    }

    open fun setCurrentItem(valueItem: String?) {
        try {
            if (!TextUtils.isEmpty(valueItem)) {
                binding.viewPager.currentItem = valueItem!!.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showMenuPop() {
        CircleMainMenuPop(
            requireContext(),
            object : CircleMainMenuPop.CheckPostType {
                override fun checkLongBar() {
                    startARouter(ARouterCirclePath.LongPostAvtivity, true)
                }

                override fun checkPic() {
                    startARouter(ARouterCirclePath.PostActivity, true)
                }

                override fun checkVideo() {
                    startARouter(ARouterCirclePath.VideoPostActivity, true)
                }

                override fun checkQuestion() {
                    GioPageConstant.askSourceEntrance = "右上角+号"
                    JumpUtils.instans?.jump(116)
                }

            }).run {
            setBlurBackgroundEnable(false)
            showPopupWindow(binding.ivMenu)
            initData()
        }
    }

    private fun initRecyclerData() {
//        val list = arrayListOf("", "")
//        circleAdapter.setItems(list)
//        binding.ryCircle.adapter = circleAdapter


    }

    override fun initData() {
        AssNineGridView.setImageLoader(GlideImageLoader())


    }


    override fun observe() {
        super.observe()
        viewModel.popupLiveData.observe(this, Observer {
            // 保存用户技师相关信息
            try {
                if (it.identityType != null) {
                    SPUtils.setParam(requireContext(), "identityType", it.identityType!!)
                }
            } catch (e: Exception) {
                e.toString()
            }
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        viewModel.getInitQuestion()
                    }

                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        SPUtils.setParam(requireContext(), "identityType", "")
                    }

                    else -> {}
                }
            }

    }

    private fun bus() {

    }

    private fun easyViewPager() {
        try {
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView: RecyclerView =
                recyclerViewField.get(binding.viewPager) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 4) //6 is empirical value
        } catch (ignore: Exception) {
        }
    }

    val circleSquareFragment: CircleSquareFragment by lazy {
        CircleSquareFragment.newInstance()

    }
    val askRecommendFragment: AskRecommendFragment by lazy {
        AskRecommendFragment.newInstance()
    }
    val newCircleFragment: NewCircleFragment by lazy {
        NewCircleFragment()

    }
    var fragmentList: ArrayList<Fragment> = arrayListOf()
    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            offscreenPageLimit = 1
        }
        fragmentList.add(circleSquareFragment)
        fragmentList.add(newCircleFragment)
        fragmentList.add(askRecommendFragment)
        binding.viewPager.adapter = CircleMainViewPagerAdapter(this, fragmentList)

    }

    private var isFirstToGio = true

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = tabList[index]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setPadding(10.toIntPx(), 0, 10.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.circle_app_color)
                simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight =
                    UIUtil.dip2px(context, 3.0).toFloat()
                indicator.lineWidth =
                    UIUtil.dip2px(context, 22.0).toFloat()
                indicator.roundRadius =
                    UIUtil.dip2px(context, 1.5).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(
                    ContextCompat.getColor(
                        context,
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
//        ViewPagerHelper.bind(magicIndicator, binding.viewPager)

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
                // 埋点
                GioPageConstant.prePageType = GioPageConstant.mainTabName
                GioPageConstant.prePageTypeName = GioPageConstant.mainSecondPageName()
                GioPageConstant.mainTabName = "社区页"
                when (position) {
                    0 -> {
                        GioPageConstant.communitySecondPageName = "社区页-广场"
                        BuriedUtil.instant?.communityMainTopMenu("广场")
                    }

                    1 -> {
                        GioPageConstant.communitySecondPageName = "社区页-圈子"
                        BuriedUtil.instant?.communityMainTopMenu("圈子")
                    }

                    2 -> {
                        GioPageConstant.communitySecondPageName = "社区页-问答"
                        BuriedUtil.instant?.communityMainTopMenu("问答")
                    }
                }
                if (!isFirstToGio) {
                    GIOUtils.homePageView()
                } else {
                    isFirstToGio = false
                }
            }

        })
    }

}