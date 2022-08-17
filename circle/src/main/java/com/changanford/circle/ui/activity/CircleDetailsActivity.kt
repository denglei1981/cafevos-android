package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleDetailsPersonalAdapter
import com.changanford.circle.adapter.circle.CircleDetailsActivityAdapter
import com.changanford.circle.adapter.circle.CircleDetailsNoticeAdapter
import com.changanford.circle.adapter.circle.CircleDetailsTopicAdapter
import com.changanford.circle.adapter.circle.TagAdapter
import com.changanford.circle.bean.CircleStarRoleDto
import com.changanford.circle.bean.GetApplyManageBean
import com.changanford.circle.databinding.ActivityCircleDetailsBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleDetailsFragment
import com.changanford.circle.ui.fragment.CircleDetailsFragmentV2
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.widget.dialog.ApplicationCircleManagementDialog
import com.changanford.circle.widget.pop.CircleDetailsMenuPop
import com.changanford.circle.widget.pop.CircleDetailsPop
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.circle.widget.pop.CircleManagementPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CircleShareBean
import com.changanford.common.bean.TestBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.BindDialog
import com.changanford.common.ui.dialog.PostDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.FlowLayoutManager
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
    companion object {
        fun start(circleId: String?) {
            circleId?.apply {
                val bundle = Bundle()
                bundle.putString("circleId", this)
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }
        }
    }

    private var isWhite = true//是否是白色状态

    private var circleId = ""
    private var isOpenMenuPop = false
    private var shareBeanVO: CircleShareBean? = null
    private var isFirst = true

    private var postEntity: ArrayList<PostEntity>? = null//草稿

    private val tagAdapter by lazy { TagAdapter() }

    private val personalAdapter by lazy {
        CircleDetailsPersonalAdapter(this)
    }

    //公告
    private val noticeAdapter by lazy {
        CircleDetailsNoticeAdapter()
    }

    //活动
    private val activityAdapter by lazy {
        CircleDetailsActivityAdapter()
    }

    //话题
    private val topicAdapter by lazy {
        CircleDetailsTopicAdapter()
    }

    override fun initView() {
        circleId = intent.getStringExtra("circleId").toString()
        initMagicIndicator()
        binding.run {
            backImg.setOnClickListener { finish() }
            AppUtils.setStatusBarPaddingTop(binding.topContent.vLine, this@CircleDetailsActivity)
            AppUtils.setStatusBarPaddingTop(binding.toolbar, this@CircleDetailsActivity)
            topContent.recyclerView.apply {
                layoutManager = FlowLayoutManager(this@CircleDetailsActivity, true)
                adapter = tagAdapter
            }

            topContent.apply {
                ryNotice.adapter = noticeAdapter
                ryActivity.adapter = activityAdapter
                ryTopic.layoutManager = FlowLayoutManager(this@CircleDetailsActivity, true)
                ryTopic.adapter = topicAdapter
            }
        }
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.backImg.setImageResource(R.mipmap.whit_left)
                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v)
                binding.tvPost.setTextColor(ContextCompat.getColor(this, R.color.white))
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.backImg.setImageResource(R.mipmap.back_xhdpi)
                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v_b)
                binding.tvPost.setTextColor(ContextCompat.getColor(this, R.color.black))
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
        PostDatabase.getInstance(this).getPostDao().findAll().observe(
            this
        ) {
            postEntity = it as ArrayList<PostEntity>
        }
        initTestData()
    }

    private fun initTestData() {
        val noticeList = arrayListOf(
            TestBean("福域探险者有活动啦，赶快参加吧福域探险者有"),
            TestBean("福域探险者有活动啦，赶快参加吧福域探险者有"),
            TestBean("福域探险者有活动啦，赶快参加吧福域探险者有")
        )
        noticeAdapter.setList(noticeList)

        val activityList = arrayListOf(
            TestBean("长安福特未来活动长安福特未…"),
            TestBean("长安福特未来活动长安福特未…"),
            TestBean("长安福特未来活动长安福特未…"),
            TestBean("长安福特未来活动长安福特未…"),
        )
        activityAdapter.setList(activityList)

        val topicList = arrayListOf(
            TestBean("#最新之野"),
            TestBean("#探寻仲夏之野"),
            TestBean("#最新之野菜"),
            TestBean("#“橙”风破浪 热力出逃"),
            TestBean("#探寻仲夏之野"),
        )
        topicAdapter.setList(topicList)
    }

    private fun initListener(circleName: String) {
        binding.ivPostBar.setOnClickListener {
            if (MineUtils.getBindMobileJumpDataType()) {
                BindDialog(this).show()
                return@setOnClickListener
            }
            if (postEntity?.size == 0) {
                initPop(circleName)
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
                                initPop(circleName)
                            }


                        }).show()
                }
//                AlertDialog(this).builder().setGone().setMsg("发现您有草稿还未发布")
//                    .setNegativeButton("继续编辑") {
//                        RouterManger.startARouter(ARouterMyPath.MyPostDraftUI)
//                    }.setPositiveButton("不使用草稿") {
//                        initPop(circleName)
//                    }.show()
            }

        }

        binding.tvPost.setOnClickListener {
            showMenuPop()
        }
        binding.topContent.apply {
            tvNoticeMore.setOnClickListener {
                startARouter(ARouterCirclePath.CircleNoticeActivity)
            }
        }
    }

    private fun initMyView(userId: String) {
        initTabAndViewPager(userId)
    }

    private fun initPop(circleName: String) {
        if (isOpenMenuPop) {
            return
        }

        val bundle = Bundle()
        bundle.putString("circleId", circleId)
        bundle.putBoolean("isCirclePost", true)
        bundle.putString("circleName", circleName)

        CircleDetailsPop(this, object : CircleMainMenuPop.CheckPostType {
            override fun checkLongBar() {
                startARouter(ARouterCirclePath.LongPostAvtivity, bundle)
            }

            override fun checkPic() {
                startARouter(ARouterCirclePath.PostActivity, bundle)
            }

            override fun checkVideo() {
                startARouter(ARouterCirclePath.VideoPostActivity, bundle)
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

    private fun showMenuPop() {
        CircleDetailsMenuPop(this).run {
            setBlurBackgroundEnable(false)
            showPopupWindow(binding.tvPost)
            initData()
        }
    }

    private fun initTabAndViewPager(userId: String) {
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return viewModel.tabList.size
                }

                override fun getItem(position: Int): Fragment {
                    return CircleDetailsFragmentV2.newInstance(
                        viewModel.circleType[position],
                        "",
                        circleId, userId
                    )
                }

            }

            offscreenPageLimit = 3
        }

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        viewModel.getCircleDetails(circleId)
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.circleDetailsBean.observe(this) {
            if (isFirst) {
                initMyView(it.userId.toString())
                isFirst = false
            }
            setJoinType(it.isApply)
            initListener(it.name)
            tagAdapter.setList(it.tags)
            if (it.isOwner == 1) {//是圈主
                binding.topContent.tvJoin.visibility = View.INVISIBLE
            } else {
                if (it.isApply == 0 || it.isApply == 1) {//未加入和审核中
                    binding.topContent.tvJoin.visibility = View.VISIBLE
                } else {
                    if (it.isViewApplyMan == 1) {//是否显示申请管理
                        binding.topContent.tvJoin.visibility = View.VISIBLE
                    } else {
                        binding.topContent.tvJoin.visibility = View.INVISIBLE
                    }
                }
            }

            binding.barTitleTv.text = it.name
            binding.shareImg.setOnClickListener { _ ->
                shareBeanVO = it.shareBeanVO
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
                //加暗
                ivBg.setColorFilter(
                    ContextCompat.getColor(
                        this@CircleDetailsActivity,
                        R.color.color_00_a30
                    )
                )
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
                tvPersonal.setOnClickListener { _ ->
                    val bundle = Bundle()
                    bundle.putString("circleId", circleId)
                    bundle.putString("isApply", it.isApply.toString())
                    startARouter(ARouterCirclePath.PersonalActivity, bundle)
                }

                if (it.isApply == 2) {
                    binding.ivPostBar.visibility = View.VISIBLE
                } else {
                    binding.ivPostBar.visibility = View.GONE
                }
            }
        }
        viewModel.joinBean.observe(this) {
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
        }
        viewModel.applyBean.observe(this) {
            //response.data.status  1待审核 2审核通过 3审核不过
            if (it.code == 0) {
                val mBean = it.data
                var type = 0
                when (mBean?.status) {
                    2 -> {
                        "您申请的${mBean.memo}已通过，无需再次申请".toast()
                        return@observe
                    }
                    1 -> {
                        type = 1
                    }
                    3 -> {
                        type = 2
                    }
                }
                ApplicationCircleManagementDialog(
                    this@CircleDetailsActivity,
                    type, mBean?.memo, this,
                    bean = mBean
                ).show()
            } else {
                it.msg.toast()
            }
        }
        viewModel.circleRolesBean.observe(this) {
            CircleManagementPop(this@CircleDetailsActivity,
                object : CircleManagementPop.ClickListener {
                    override fun checkPosition(bean: CircleStarRoleDto) {
                        if (bean.isApply == 0) {//没有申请过
                            ApplicationCircleManagementDialog(
                                this@CircleDetailsActivity,
                                bean.isApply, bean.starName, this@CircleDetailsActivity,
                                GetApplyManageBean(
                                    circleId = circleId,
                                    circleStarRoleId = bean.circleStarRoleId
                                )
                            ).show()
                        } else {
                            viewModel.applyManagerInfo(circleId, bean.circleStarRoleId)
                        }

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
                showPopupWindow(binding.topContent.tvJoinText)
                setData(it)
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
                    tvJoinText.setText(R.string.str_underReview)
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
                        viewModel.getCircleRoles(circleId)
                    }
                }
            }
        }
    }


}