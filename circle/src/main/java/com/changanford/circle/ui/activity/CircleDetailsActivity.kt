package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleDetailsPersonalAdapter
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityCircleDetailsBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleDetailsFragment
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.widget.dialog.ApplicationCircleManagementDialog
import com.changanford.circle.widget.pop.CircleDetailsPop
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.circle.widget.pop.CircleManagementPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.google.android.material.appbar.AppBarLayout
import jp.wasabeef.glide.transformations.BlurTransformation
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import razerdp.basepopup.BasePopupWindow
import kotlin.math.abs

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 圈子详情
 */
@Route(path = ARouterCirclePath.CircleDetailsActivity)
class CircleDetailsActivity : BaseActivity<ActivityCircleDetailsBinding, CircleDetailsViewModel>() {

    private var isWhite = true//是否是白色状态

    private var circleId = ""
    private var isOpenMenuPop = false

    private val personalAdapter by lazy {
        CircleDetailsPersonalAdapter(this)
    }

    override fun initView() {
        circleId = intent.getStringExtra("circleId").toString()
        initMagicIndicator()
        binding.run {
            backImg.setOnClickListener { finish() }
            AppUtils.setStatusBarPaddingTop(binding.topContent.vLine, this@CircleDetailsActivity)
            AppUtils.setStatusBarPaddingTop(binding.toolbar, this@CircleDetailsActivity)
        }
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.backImg.setImageResource(R.mipmap.whit_left)
                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v)
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.backImg.setImageResource(R.mipmap.back_xhdpi)
                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v_b)
                isWhite = false
            }
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.toolbar.background.mutate().alpha = mAlpha
                binding.barTitleTv.alpha = mAlpha / 255.0F
            } else {
                binding.toolbar.background.mutate().alpha = 255
                binding.barTitleTv.alpha = 1.0F
            }
        })
        initListener()
        initTabAndViewPager()
    }

    private fun initListener() {
        binding.ivPostBar.setOnClickListener {
            if (isOpenMenuPop) {
                return@setOnClickListener
            }
            CircleDetailsPop(this, object : CircleMainMenuPop.CheckPostType {
                override fun checkLongBar() {

                }

                override fun checkPic() {

                }

                override fun checkVideo() {

                }

            }).run {
                //无透明背景
                setBackgroundColor(Color.TRANSPARENT)
                //背景模糊false
                setBlurBackgroundEnable(false)
                showPopupWindow(it)
                onDismissListener = object : BasePopupWindow.OnDismissListener() {
                    override fun onDismiss() {
                        isOpenMenuPop = false
                        binding.ivPostBar.setImageResource(R.mipmap.circle_post_bar_icon)
                    }

                }
                setOnPopupWindowShowListener {
                    isOpenMenuPop = true
                    binding.ivPostBar.setImageResource(R.mipmap.circle_post_bar_open_icon)
                }
            }
        }

    }

    private fun initTabAndViewPager() {
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return viewModel.tabList.size
                }

                override fun getItem(position: Int): Fragment {
                    return CircleDetailsFragment.newInstance(position.toString(), "", circleId)
                }

            }

            offscreenPageLimit = 3
        }

    }

    override fun initData() {
        viewModel.getCircleDetails(circleId)
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.circleDetailsBean.observe(this, {
            setJoinType(it.isApply)
            binding.barTitleTv.text = it.name
            binding.shareImg.setOnClickListener { _ ->
                CircleShareModel.shareDialog(
                    this,
                    0,
                    it.shareBeanVO,
                    null,
                    null,
                    null,
                    null
                )
            }
            binding.topContent.run {
                Glide.with(this@CircleDetailsActivity)
                    .load(GlideUtils.handleImgUrl(it.pic))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
                    .into(ivBg)
                ivIcon.setCircular(5)
                ivIcon.loadImage(it.pic)
                tvTitle.text = it.name
                tvContent.text = it.description
                tvNum.text = "${it.postsCount} 帖子"
                ryPersonal.adapter = personalAdapter
                tvPersonal.text = "${it.userCount}成员"
                personalAdapter.setItems(it.users)
                personalAdapter.notifyDataSetChanged()
                tvPersonal.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("circleId", circleId)
                    startARouter(ARouterCirclePath.PersonalActivity, bundle)
                }
            }
        })
        viewModel.joinBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                val data = viewModel.circleDetailsBean.value
                data?.let { circleBean ->
                    if (circleBean.isApply == 0) {
                        circleBean.isApply = 1
                        setJoinType(1)
                    }
                }
            }
        })
    }

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
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
                simplePagerTitleView.setPadding(15.toIntPx(), 0, 15.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(this@CircleDetailsActivity, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@CircleDetailsActivity, R.color.circle_app_color)
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
                        this@CircleDetailsActivity,
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }

    private fun setJoinType(applyType: Int) {
        when (applyType) {
            0 -> {
                binding.topContent.run {
                    tvJoin.setBackgroundResource(R.drawable.circle_follow_bg)
                    tvJoinText.text = "加入"
                    tvJoinText.setTextColor(
                        ContextCompat.getColor(
                            this@CircleDetailsActivity,
                            R.color.white
                        )
                    )
                    tvJoin.setOnClickListener {
                        viewModel.joinCircle(circleId)
                    }
                }
            }
            1 -> {
                binding.topContent.run {
                    tvJoin.setBackgroundResource(R.drawable.circle_ee_12_bg)
                    tvJoinText.text = "申请中"
                    tvJoinText.setTextColor(
                        ContextCompat.getColor(
                            this@CircleDetailsActivity,
                            R.color.color_99
                        )
                    )
                    tvJoin.setOnClickListener {
                        "您已申请这个圈子,无须再次申请".toast()
                    }
                }
            }
            2 -> {
                binding.topContent.run {
                    tvJoin.setBackgroundResource(R.drawable.circle_follow_bg)
                    tvJoinText.text = "申请圈管"
                    tvJoinText.setTextColor(
                        ContextCompat.getColor(
                            this@CircleDetailsActivity,
                            R.color.white
                        )
                    )
                    tvJoin.setOnClickListener {
                        CircleManagementPop(this@CircleDetailsActivity,
                            object : CircleManagementPop.ClickListener {
                                override fun checkPosition(bean: String) {
                                    ApplicationCircleManagementDialog(
                                        this@CircleDetailsActivity,
                                        1
                                    ).show()
                                }
                            }).run {
                            //pop背景对齐
//                    setAlignBackground(true)
                            //无透明背景
//                    setBackgroundColor(Color.TRANSPARENT)
                            //背景模糊false
                            setBlurBackgroundEnable(false)
                            //弹出位置 基于绑定的view 默认BOTTOM
//                    popupGravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                            showPopupWindow(tvJoinText)
                            setData(arrayListOf("星推官", "星推官助手"))
                            onDismissListener =
                                object : BasePopupWindow.OnDismissListener() {
                                    override fun onDismiss() {

                                    }

                                }
                            setOnPopupWindowShowListener {

                            }
                        }
                    }
                }
            }
        }
    }

}