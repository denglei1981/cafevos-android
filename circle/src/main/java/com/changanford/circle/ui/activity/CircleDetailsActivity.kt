package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
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
import com.changanford.circle.ui.fragment.CircleDetailsFragmentV2
import com.changanford.circle.utils.FlexboxLayoutManagerCustom
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.widget.dialog.ApplicationCircleManagementDialog
import com.changanford.circle.widget.pop.CircleDetailsMenuNewPop
import com.changanford.circle.widget.pop.CircleDetailsMenuPop
import com.changanford.circle.widget.pop.CircleDetailsPop
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.circle.widget.pop.CircleManagementPop
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.CircleShareBean
import com.changanford.common.bean.GioPreBean
import com.changanford.common.constant.IntentKey
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
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateCircleDetailsData
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.common.widget.control.BannerControl
import com.changanford.common.wutil.ScreenUtils
import com.changanford.common.wutil.ShowPopUtils
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
class CircleDetailsActivity :
    BaseLoadSirActivity<ActivityCircleDetailsBinding, CircleDetailsViewModel>() {
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
    private var circleName: String? = null
    private var isOpenMenuPop = false
    private var shareBeanVO: CircleShareBean? = null
    private var isFirst = true
    private var hasLookNotice = false
    private var gioPreBean = GioPreBean()

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

    override fun onRetryBtnClick() {

    }

    override fun initView() {
        title = "圈子详情页"
        GioPageConstant.circleDetailTabName = "推荐"
        circleId = intent.getStringExtra("circleId").toString()
        initMagicIndicator()
        setLoadSir(binding.clContent)
        binding.run {
            backImg.setOnClickListener { finish() }
            AppUtils.setStatusBarPaddingTop(binding.topContent.clTopOne, this@CircleDetailsActivity)
            AppUtils.setStatusBarPaddingTop(binding.toolbar, this@CircleDetailsActivity)
            topContent.recyclerView.apply {
//                layoutManager = FlowLayoutManager(this@CircleDetailsActivity, true)
                adapter = tagAdapter
            }

            topContent.apply {
                ryNotice.adapter = noticeAdapter
                ryActivity.adapter = activityAdapter
                val topicLayoutManager =
                    FlexboxLayoutManagerCustom(this@CircleDetailsActivity, 2)
                ryTopic.layoutManager = topicLayoutManager
                ryTopic.adapter = topicAdapter
            }
        }
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 4.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.3F && !isWhite) {
                binding.backImg.setImageResource(R.mipmap.whit_left)
//                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v)
                binding.shareImg.setColorFilter(Color.parseColor("#ffffff"))
                binding.tvPost.setTextColor(ContextCompat.getColor(this, R.color.white))
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.3F && isWhite) {
                binding.backImg.setImageResource(R.mipmap.back_xhdpi)
//                binding.shareImg.setImageResource(R.mipmap.circle_share_image_v_b)
                binding.shareImg.setColorFilter(Color.parseColor("#000000"))
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
        }
        PostDatabase.getInstance(this).getPostDao().findAll().observe(
            this
        ) {
            postEntity = it as ArrayList<PostEntity>
        }
        LiveDataBus.get().withs<GioPreBean>(LiveDataBusKey.UPDATE_CIRCLE_DETAILS_GIO)
            .observe(this) {
                gioPreBean = it
            }
//        viewModel.getBannerData()
    }

    private fun initListener(circleName: String) {

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> {
                        GioPageConstant.circleDetailTabName = "推荐"
                    }

                    1 -> {
                        GioPageConstant.circleDetailTabName = "最新"
                    }

                    2 -> {
                        GioPageConstant.circleDetailTabName = "精华"
                    }

                    3 -> {
                        GioPageConstant.circleDetailTabName = "圈主专区"
                    }
                }
                GIOUtils.circleDetailPageResourceClick(
                    "tab名称",
                    (position + 1).toString(),
                    GioPageConstant.circleDetailTabName
                )
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

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
                val bundle = Bundle()
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                bundle.putBoolean(IntentKey.HAS_LOOK_NOTICE, hasLookNotice)
                startARouter(ARouterCirclePath.CircleNoticeActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("公告栏", "0", "")
                updateCircleDetailsData("公告栏页", "公告栏页")
            }
            tvTopicMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(IntentKey.TOPIC_TYPE, 1)
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                bundle.putString("circleName", binding.barTitleTv.text.toString())
                startARouter(ARouterCirclePath.HotTopicActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("圈内话题", "0", "")
                updateCircleDetailsData("圈内话题页", "圈内话题页")
            }
            tvActivityMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                startARouter(ARouterCirclePath.CircleActivityListActivity, bundle)
                GIOUtils.circleDetailPageResourceClick("圈内活动", "0", "")
                updateCircleDetailsData("圈内活动页", "圈内活动页")
            }
            clTop.setOnClickListener {
                GIOUtils.circleDetailPageResourceClick("圈子简介", "1", "圈子简介")
            }
        }

        noticeAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = noticeAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
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
            bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
            bundle.putString("circleName", binding.barTitleTv.text.toString())
            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            GIOUtils.circleDetailPageResourceClick(
                "圈内话题",
                (position + 1).toString(),
                bean.name
            )
            updateCircleDetailsData(bean.name, "圈内话题详情页")
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
//                    binding.ivPostBar.setImageResource(R.mipmap.circle_post_bar_icon)
                    binding.ivPostBar.rotation = 0f
                }

            }
            setOnPopupWindowShowListener {
                isOpenMenuPop = true
                binding.ivPostBar.rotation = 45f
//                binding.ivPostBar.setImageResource(R.mipmap.circle_post_bar_open_icon)
            }
        }
    }

    private fun showMenuPop() {
        viewModel.circleDetailsBean.value?.permissions?.let {
            CircleDetailsMenuNewPop(this, circleId, it).run {
                setBlurBackgroundEnable(false)
                showPopupWindow(binding.tvPost)
                initData()
            }
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

    private val circleNameData = MutableLiveData<String>()

    override fun onResume() {
        super.onResume()
        viewModel.getCircleDetails(circleId)
        circleNameData.observe(this) {
            updateMainGio(it, "圈子详情页")
        }
    }

    private var onlyAuthJoin = 0

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.circleDetailsBean.observe(this) {
            if (it == null) {
                showFailure("服务器开小差，请稍候再试")
                return@observe
            }
            circleName = it.name
            circleNameData.value = it.name
            onlyAuthJoin = it.onlyAuthJoin
            if (isFirst) {
                initMyView(it.userId.toString())
                isFirst = false
            }
            if (!it.wonderfulControls.isNullOrEmpty()) {
                if (it.wonderfulControls.size > 3) {
                    activityAdapter.setList(it.wonderfulControls.subList(0, 3))
                } else {
                    activityAdapter.setList(it.wonderfulControls)
                }
                binding.topContent.clActivity.visibility = View.VISIBLE
            } else {
                binding.topContent.clActivity.visibility = View.GONE
            }
            binding.topContent.clNotice.visibility = View.VISIBLE
            if (it.circleNotices.isNullOrEmpty()) {
                noticeAdapter.setEmptyView(R.layout.empty_notice)
            } else {
                noticeAdapter.setList(it.circleNotices)
            }

            if (it.circleTopics.isNullOrEmpty()) {
                binding.topContent.clTopic.visibility = View.GONE
            } else {
                binding.topContent.clTopic.visibility = View.VISIBLE
                topicAdapter.setList(it.circleTopics)
            }

            if (it.permissions.isNullOrEmpty()) {
                binding.tvPost.visibility = View.GONE
            } else {
                it.permissions.forEach { item ->
                    if (item.dictValue == "ANNOUNCEMENT") {
                        hasLookNotice = true
                    }
                }
                binding.tvPost.visibility = View.VISIBLE
            }
            setJoinType(it.isApply)
            initListener(it.name)
            tagAdapter.setList(it.tags)
            if (it.isOwner == 1) {//是圈主
                binding.topContent.tvJoin.visibility = View.GONE
            } else {
                if (it.isApply == 0 || it.isApply == 1) {//未加入和审核中
                    binding.topContent.tvJoin.visibility = View.VISIBLE
                } else {
                    if (it.isViewApplyMan == 1) {//是否显示申请管理
                        binding.topContent.tvJoin.visibility = View.VISIBLE
                    } else {
                        binding.topContent.tvJoin.visibility = View.GONE
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
                    .load(
                        if (it.bgImg.isNullOrEmpty()) R.mipmap.c_d_t_bg else GlideUtils.handleImgUrl(
                            it.bgImg
                        )
                    )
//                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
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
                    updateCircleDetailsData("成员页", "成员页")
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
            GIOUtils.circleDetailPageView(
                it.circleId.toString(),
                it.name,
                gioPreBean.prePageName,
                gioPreBean.prePageType
            )
            setBannerList(it.ads)
            setPersonalMarginTop()
            showContent()
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
        viewModel.joinCheckBean.observe(this) {
            if (it.canJoin) {
                viewModel.joinCircle(circleId)
            } else {
                it.alertMes?.let { it1 -> ShowPopUtils.showJoinCircleAuPop(it1) }
            }
        }
//        viewModel.advertisingList.observe(this) {
//            binding.topContent.banner.isVisible = it.isNullOrEmpty()
//            BannerControl.bindingBanner(
//                binding.topContent.banner,
//                it,
//                ScreenUtils.dp2px(this, 4f), true
//            )
//        }
    }

    private fun setPersonalMarginTop() {
        binding.topContent.llPersonal.post {
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val iconTop = binding.topContent.ivIcon.bottom
            val tvNumTop = binding.topContent.tvNum.bottom
            layoutParams.width = 0
            layoutParams.height = 40.toIntPx()
            layoutParams.startToStart = R.id.cl_top_one
            layoutParams.endToEnd = R.id.cl_top_one
            layoutParams.topToBottom = R.id.cl_top_one
            if (tvNumTop > iconTop) {
                layoutParams.setMargins(0, 11.toIntPx(), 0, 0)
            } else {
                layoutParams.setMargins(0, 28.toIntPx(), 0, 0)
            }
            binding.topContent.llPersonal.layoutParams = layoutParams
//            binding.topContent.llPersonal.background =
//                ContextCompat.getDrawable(this, R.drawable.circle_people_top_bg)
        }
    }

    private fun setBannerList(ads: ArrayList<AdBean>) {
        binding.topContent.banner.isVisible = ads.isNullOrEmpty()
        BannerControl.bindingBanner(
            binding.topContent.banner,
            ads,
            ScreenUtils.dp2px(this, 4f), true
        )
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
                    ContextCompat.getColor(this@CircleDetailsActivity, R.color.color_9916)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@CircleDetailsActivity, R.color.color_1700F4)
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
                        R.color.color_1700F4
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
                        if (onlyAuthJoin == 0) {//仅车主可以加圈，要调用接口判断是不是车主
                            viewModel.checkJoin(circleId)
                        } else {
                            viewModel.joinCircle(circleId)
                        }
                        GIOUtils.joinCircleClick(
                            "圈子详情页-${GioPageConstant.circleDetailTabName}",
                            circleId,
                            circleName
                        )
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