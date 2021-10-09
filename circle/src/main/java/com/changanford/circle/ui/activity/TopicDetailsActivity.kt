package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityTopicDetailsBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleDetailsFragment
import com.changanford.circle.viewmodel.TopicDetailsViewModel
import com.changanford.circle.widget.pop.CircleDetailsPop
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.utilext.GlideUtils
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
 *Time on 2021/9/23
 *Purpose 话题详情
 */
@Route(path = ARouterCirclePath.TopicDetailsActivity)
class TopicDetailsActivity : BaseActivity<ActivityTopicDetailsBinding, TopicDetailsViewModel>() {

    private var isWhite = true//是否是白色状态
    private var topicId = ""
    private var isOpenMenuPop = false

    override fun initView() {
        initMagicIndicator()

        topicId = intent.getStringExtra("topicId").toString()

        binding.run {
            AppUtils.setStatusBarPaddingTop(binding.toolbar, this@TopicDetailsActivity)
            backImg.setOnClickListener { finish() }
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

    override fun initData() {
        viewModel.getData(topicId)
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

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.topPicDetailsTopBean.observe(this, {
            binding.barTitleTv.text = it.name
            binding.topContent.run {
                Glide.with(this@TopicDetailsActivity)
                    .load(GlideUtils.handleImgUrl(it.pic))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
                    .into(ivBg)
                ivIcon.setCircular(5)
                ivIcon.loadImage(it.pic)
                tvNum.text = "${it.postsCount}帖子       ${it.likesCount}热度"
                tvType.text = it.name
                tvContent.text = it.description
            }
            binding.barTitleTv.text = it.name
        })
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
                    val type = when (position) {
                        0 -> {
                            4
                        }
                        1 -> {
                            2
                        }
                        else -> {
                            3
                        }
                    }
                    return CircleDetailsFragment.newInstance(type.toString(), topicId)
                }

            }

            offscreenPageLimit = 3
        }

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
                    ContextCompat.getColor(this@TopicDetailsActivity, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@TopicDetailsActivity, R.color.circle_app_color)
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
                        this@TopicDetailsActivity,
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }
}