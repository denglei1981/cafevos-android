package com.changanford.my

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.Imag
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.ui.dialog.SelectCoverDialog
import com.changanford.common.ui.dialog.SelectMapDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils.showToast
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.changanford.common.widget.SelectDialog
import com.changanford.evos.databinding.ActivityPersonCenterBinding
import com.changanford.my.adapter.LabelAdapter
import com.changanford.my.adapter.MtViewPagerAdapter
import com.changanford.my.fragment.HomePageFragment
import com.changanford.my.fragment.MyCollectFragment
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.request.PersonCenterViewModel
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.utils.toIntPx
import com.changanford.widget.ScaleTransitionPagerTitleView
import com.google.android.material.appbar.AppBarLayout
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.util.Observer
import kotlin.math.abs


// 个人主页
@Route(path = ARouterMyPath.PersonCenterActivity)
class PersonCenterActivity : BaseActivity<ActivityPersonCenterBinding, PersonCenterViewModel>() {
    private val tabList = arrayListOf("主页", "帖子")

    val userId = MConstant.userId

    var taUserId = ""

    val postFragment: PostFragment by lazy {
        if (TextUtils.isEmpty(taUserId)) {
            PostFragment.newInstance("centerPost", userId)
        } else {
            PostFragment.newInstance("centerPost", taUserId)
        }
    }
    val homePageFragment: HomePageFragment by lazy {
        // 日了狗了。
        if (TextUtils.isEmpty(taUserId)) {
            HomePageFragment.newInstance("centerPost", userId)
        } else {
            HomePageFragment.newInstance("centerPost", taUserId)
        }
    }
    val myCollectFragment: MyCollectFragment by lazy {
        if (TextUtils.isEmpty(taUserId)) {
            MyCollectFragment.newInstance("centerPost", userId)
        } else {
            MyCollectFragment.newInstance("centerPost", taUserId)
        }
    }
    var fragmentList: ArrayList<Fragment> = arrayListOf()
    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            offscreenPageLimit = 1
        }
        binding.backImg.setOnClickListener {
            onBackPressed()
        }
        fragmentList.add(homePageFragment)
        fragmentList.add(postFragment)
        if (TextUtils.isEmpty(taUserId) || taUserId == userId) {
            fragmentList.add(myCollectFragment)
        }
        binding.viewPager.adapter = MtViewPagerAdapter(this, fragmentList)
        if (TextUtils.isEmpty(taUserId) || taUserId == userId) { //是自己
            binding.topContent.tvEditInfo.visibility = View.VISIBLE
            binding.topContent.btnFollow.visibility = View.GONE
            binding.topContent.tvEditInfo.setOnClickListener {
                JumpUtils.instans?.jump(34)
            }
            binding.topContent.ivCover.visibility = View.VISIBLE
            binding.topContent.ivCover.setOnClickListener {
                try {
                    SelectCoverDialog(
                        BaseApplication.curActivity,
                        object : SelectCoverDialog.CheckedView {
                            override fun checkGaoDe() {
                                changCover()
                                selectIcon()
                            }

                            override fun checkCancel() {

                            }
                        }).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } else {
            binding.topContent.tvEditInfo.visibility = View.GONE
            binding.topContent.ivCover.visibility = View.GONE
            binding.topContent.btnFollow.visibility = View.VISIBLE
        }
    }

    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.toolbar, this)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutEmptyUser.collectToolbar.conTitle, this)
        binding.layoutEmptyUser.collectToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        setAppbarPercent()
        intent.extras?.let { bundle ->
            bundle.getString("value")?.let {
                taUserId = it
            }
        }
        binding.topContent.ivHead.setOnClickListener {
            if(TextUtils.isEmpty(taUserId)||taUserId==userId){
                JumpUtils.instans?.jump(34)
            }

        }
        binding.topContent.tvUserLevel.setOnClickListener {
            if(TextUtils.isEmpty(taUserId)||taUserId==userId){
                JumpUtils.instans?.jump(32)
            }

        }
        binding.topContent.llMedal.setOnClickListener {
            if(TextUtils.isEmpty(taUserId)||taUserId==userId){
                JumpUtils.instans?.jump(29)
            }


        }
    }

    override fun initData() {
        initTabAndViewPager()
        initMagicIndicator()
        if (TextUtils.isEmpty(taUserId)) {
            viewModel.queryOtherInfo(userId) {
                it.onSuccess { user ->
                    showUserInfo(user)
                }
                it.onWithMsgFailure { e ->
                    e?.toast()
                }
            }
        } else {
            viewModel.queryOtherInfo(taUserId) {
                it.onSuccess { user ->
                    showUserInfo(user)
                }
                it.onWithMsgFailure { e ->
                    e?.toast()
                }
            }
        }


    }

    lateinit var dialog: LoadDialog
    var headIconUrl: String = ""//头像Http地址
    fun changCover() {
        dialog = LoadDialog(this)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
    }

    var headIconPath: String = ""//头像地址

    /**
     * 拍照
     */
    private fun takePhoto() {
        PictureUtils.opencarcme(this, object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                // 结果回调
                if (result.isNotEmpty()) {
                    for (media in result) {
                        val path: String = PictureUtil.getFinallyPath(media)

                        headIconPath = path
                        saveHeadIcon()
                    }
                }
            }

            override fun onCancel() {
                // 取消
            }
        })
    }

    /**
     * 新的保存
     */
    val twoBtnPop: ConfirmTwoBtnPop by lazy {
        ConfirmTwoBtnPop(this)
    }

    private fun saveUserInfo(isShowDialog: Boolean, map: HashMap<String, String>) {
        viewModel.saveUniUserInfoV1(map) { response ->
            response.onSuccess {
                dialog.dismiss()
                if (isShowDialog) {
                    twoBtnPop.apply {
                        contentText.text = response.msg
                        btnCancel.visibility = View.GONE
                        btnConfirm.text = "我知道了"
                        btnConfirm.setOnClickListener {
                            dismiss()
                        }
                    }.showPopupWindow()
                } else {
                    GlideUtils.loadBD(map["frontCover"], binding.topContent.ivBg)
                    "保存成功".toast()
                }
            }
            response.onFailure {
                dialog.dismiss()

            }
        }
    }

    private fun saveHeadIcon() {
        //保存
        if (headIconPath.isNotEmpty()) {
            dialog.show()
            viewModel.uploadFile(this, arrayListOf(headIconPath), object : UploadPicCallback {
                override fun onUploadSuccess(files: ArrayList<String>) {
                    println(files)
                    dialog.dismiss()
                    if (files.size > 0) headIconUrl = files[0]
                    var map = HashMap<String, String>()
                    map["frontCover"] = headIconUrl
                    saveUserInfo(false, map)
                }

                override fun onUploadFailed(errCode: String) {
                    dialog.dismiss()
                }

                override fun onuploadFileprogress(progress: Long) {
                }
            })
        }
    }

    /**
     * 选择图片
     */
    private fun pic() {
        PictureUtils.openGarlly(this, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                for (media in result) {
                    var path: String? = ""
                    path = PictureUtil.getFinallyPath(media)
//                    loadCircleFilePath(path, binding.editIcon)
                    headIconPath = path
                    saveHeadIcon()
                }
            }

            override fun onCancel() {}
        })
    }

    /**
     * 点击头像
     */
    private fun selectIcon() {
        SelectDialog(
            this,
            R.style.transparentFrameWindowStyle,
            MineUtils.listPhoto,
            "",
            1,
            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->

                when (i) {
                    0 -> takePhoto()
                    1 -> pic()
                }
            }
        ).show()
    }

    private fun showUserInfo(userInfoBean: UserInfoBean?) {
        userInfoBean?.let {
            when (userInfoBean.status) {
                2 -> { // 用户已注销。
                    binding.layoutEmptyUser.llEmptyUser.visibility = View.VISIBLE
                }
                else -> {
                    isFollow = userInfoBean.isFollow
                    showFollowState(userInfoBean.isFollow)
                    binding.topContent.btnFollow.setOnClickListener {
                        if (!TextUtils.isEmpty(taUserId)) {
                            cancel(taUserId, if (isFollow == 0) "1" else "2")
                        }
                    }

                    binding.layoutEmptyUser.llEmptyUser.visibility = View.GONE
                    GlideUtils.loadBD(userInfoBean.avatar, binding.topContent.ivHead) // 头像
                    binding.topContent.tvNickname.text = userInfoBean.nickname
                    binding.topContent.ddLikes.setPageTitleText(userInfoBean.count.likeds.toString())
                    binding.topContent.ddFans.setPageTitleText(userInfoBean.count.fans.toString())
                    binding.topContent.ddFollow.setPageTitleText(userInfoBean.count.follows.toString())
                    binding.topContent.tvUserLevel.text = userInfoBean.ext.growSeriesName
                    binding.topContent.tvCarName.text = userInfoBean.ext.carOwner
                    if (TextUtils.isEmpty(userInfoBean.ext.carOwner)) {
                        binding.topContent.tvCarName.visibility = View.GONE
                    } else {
                        binding.topContent.tvCarName.visibility = View.VISIBLE
                    }
                    if (TextUtils.isEmpty(userInfoBean.ext.memberIcon)) {
                        binding.topContent.ivVip.visibility = View.GONE
                    } else {
                        GlideUtils.loadBD(userInfoBean.ext.memberIcon, binding.topContent.ivVip)
                        binding.topContent.ivVip.visibility = View.VISIBLE
                    }
                    binding.topContent.tvSign.text =
                        if (userInfoBean.brief.isNullOrEmpty()) "这个人很懒~" else userInfoBean.brief


                    //用户图标
                    userInfoBean.userMedalList.let { imgs ->
                        var imgList = arrayListOf<Imag>()
                        imgs?.forEach { i ->
                            imgList.add(Imag(i.medalImage, -1, ""))

                        }
                        binding.topContent.rvMedal.visibility = View.VISIBLE
                        binding.topContent.rvMedal.adapter = LabelAdapter(20).apply {
                            addData(imgList)
                        }
                    }
                    if (!TextUtils.isEmpty(userInfoBean.frontCover)) {
                        GlideUtils.loadBD(userInfoBean.frontCover, binding.topContent.ivBg)
                    }
                    binding.topContent.tvTotal.text =
                        "共".plus(userInfoBean.medalCount.toString().plus("枚"))
                    binding.topContent.ddFollow.setOnClickListener {
                        if (taUserId == userId || TextUtils.isEmpty(taUserId)) {
                            JumpUtils.instans?.jump(25)
                        } else {
                            mapOf(
                                RouterManger.KEY_TO_ID to 2,
                                "userId" to userId,
                                "title" to "TA的关注"
                            )
                            RouterManger.param(RouterManger.KEY_TO_ID, 2)
                                .param(RouterManger.KEY_TO_OBJ, userId)
                                .param("title", "TA的关注")
                                .startARouter(ARouterMyPath.TaFansUI)
                        }
                    }
//                    binding.topContent.ddPublish.setOnClickListener {
//                        JumpUtils.instans?.jump(23)
//                    }
                    binding.topContent.ddFans.setOnClickListener {
                        if (taUserId == userId || TextUtils.isEmpty(taUserId)) {
                            JumpUtils.instans?.jump(40)
                        } else {
                            RouterManger.param(RouterManger.KEY_TO_ID, 1)
                                .param(RouterManger.KEY_TO_OBJ, userId)
                                .param("title", "TA的粉丝")
                                .startARouter(ARouterMyPath.TaFansUI)
                        }
                    }
                }
            }

        }


    }

    private var isWhite = true//是否是白色状态
    private fun setAppbarPercent() {
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.backImg.setImageResource(com.changanford.circle.R.mipmap.whit_left)

                StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.backImg.setImageResource(com.changanford.circle.R.mipmap.back_xhdpi)
                StatusBarUtil.setStatusBarColor(this, R.color.color_F4)
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
    }

    private fun initMagicIndicator() {
        if ((TextUtils.isEmpty(taUserId) && tabList.size < 3) || taUserId == userId) {
            tabList.add("收藏")
        }
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.color_F4))
        val commonNavigator = CommonNavigator(this)

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
                simplePagerTitleView.setPadding(20.toIntPx(), 0, 20.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.color_00095B)
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
                        R.color.color_00095B
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

    // 1 关注 2 取消关注
    fun cancel(followId: String, type: String) {
        if (MineUtils.getBindMobileJumpDataType(true)) {
            return
        }
        if (type == "1") {
            viewModel.cancelFans(followId, type)
        } else {
            QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_two_btn)
                .config(
                    QuickPopupConfig()
                        .gravity(Gravity.CENTER)
                        .withClick(R.id.btn_comfir, View.OnClickListener {
                            viewModel.cancelFans(followId, type)
                        }, true)
                        .withClick(R.id.btn_cancel, View.OnClickListener {

                        }, true)
                )
                .show()
        }
    }

    var isFollow = 0
    override fun observe() {
        super.observe()
        viewModel.cancelTip.observe(this, androidx.lifecycle.Observer {
            if (it == "true") {
                isFollow = if (isFollow == 0) {
                    1
                } else {
                    0
                }
                showFollowState(isFollow)
                LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(isFollow)
            } else {
                it.toast()
            }

        })
    }

    fun showFollowState(isFollow: Int) {
        if (isFollow == 0) {
            binding.topContent.btnFollow.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_00095B
                )
            )
            binding.topContent.btnFollow.text = "关注"
            binding.topContent.btnFollow.background=ContextCompat.getDrawable(this,R.drawable.shape_r_8_c_5c_13)
        } else {
            binding.topContent.btnFollow.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.topContent.btnFollow.background=ContextCompat.getDrawable(this,R.drawable.shape_ddd)
            binding.topContent.btnFollow.text = "已关注"
        }

    }


}