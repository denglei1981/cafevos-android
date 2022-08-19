package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityTopicDetailsBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular

import com.changanford.circle.ui.fragment.CircleDetailsFragment
import com.changanford.circle.ui.fragment.CircleDetailsFragmentV2
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.TopicDetailsViewModel
import com.changanford.circle.widget.pop.CircleDetailsPop
import com.changanford.circle.widget.pop.CircleMainMenuPop

import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.manger.RouterManger
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.BindDialog
import com.changanford.common.ui.dialog.PostDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx
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

    private var postEntity: ArrayList<PostEntity>? = null//草稿

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
        initTabAndViewPager()

        PostDatabase.getInstance(this).getPostDao().findAll().observe(
            this
        ) {
            postEntity = it as ArrayList<PostEntity>
        }
    }

    override fun initData() {
        viewModel.getData(topicId)
    }

    private fun initListener(topicName: String) {
        binding.ivPostBar.setOnClickListener {
            if (MConstant.token.isEmpty()) {
                startARouter(ARouterMyPath.SignUI)
                return@setOnClickListener
            }
            if (MineUtils.getBindMobileJumpDataType()) {
                BindDialog(this).show()
                return@setOnClickListener
            }
            if (postEntity?.size == 0) {
                initPop(topicName)
            } else {
                this.let { it1 ->
                    PostDialog(
                        it1,
                        "发现您还有草稿未发布",
                        postButtonListener = object : PostDialog.PostButtonListener {
                            override fun save() { //继续编辑 2 图片 3 视频 4 图文长帖
                                var postEntity = postEntity?.last()
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
                                initPop(topicName)
                            }


                        }).show()
                }
//                AlertDialog(this).builder().setGone().setMsg("发现您有草稿还未发布")
//                    .setNegativeButton("继续编辑") {
//                        startARouter(ARouterMyPath.MyPostDraftUI)
//                    }.setPositiveButton("不使用草稿") {
//                        initPop(topicName)
//                    }.show()
            }
        }
    }

    private fun initPop(topicName: String) {
        if (isOpenMenuPop) {
            return
        }

        val bundle = Bundle()
        bundle.putString("topId", topicId)
        bundle.putBoolean("isTopPost", true)
        bundle.putString("topName", topicName)

        CircleDetailsPop(this, object : CircleMainMenuPop.CheckPostType {
            override fun checkLongBar() {
                startARouter(ARouterCirclePath.LongPostAvtivity, bundle, true)
            }

            override fun checkPic() {
                startARouter(ARouterCirclePath.PostActivity, bundle, true)
            }

            override fun checkVideo() {
                startARouter(ARouterCirclePath.VideoPostActivity, bundle, true)
            }

            override fun checkQuestion() {

            }

        }).run {
            //无透明背景
            setBackgroundColor(Color.TRANSPARENT)
            //背景模糊false
            setBlurBackgroundEnable(false)
            showPopupWindow(binding.ivPostBar)
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

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.topPicDetailsTopBean.observe(this) {
            initListener(it.name)
            binding.barTitleTv.text = it.name
            binding.topContent.run {
                //加暗
                ivBg.setColorFilter(
                    ContextCompat.getColor(
                        this@TopicDetailsActivity,
                        R.color.color_00_a30
                    )
                )
                Glide.with(this@TopicDetailsActivity)
                    .load(GlideUtils.handleImgUrl(it.pic))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
                    .into(ivBg)
                ivIcon.setCircular(5)
                ivIcon.loadImage(it.pic)
                tvNum.text = "${it.postsCount}帖子       ${it.heat}浏览量"
                tvType.text = it.name
                tvContent.text = it.description
            }
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
                    return CircleDetailsFragmentV2.newInstance(type.toString(), topicId)
                }

            }

            offscreenPageLimit = 3
        }

    }

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
//        magicIndicator.setBackgroundColor(Color.WHITE)
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