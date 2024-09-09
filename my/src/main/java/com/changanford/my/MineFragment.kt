package com.changanford.my

import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx
import com.changanford.my.adapter.MineMenuAdapter
import com.changanford.my.adapter.MineMidRyAdapter
import com.changanford.my.bean.MineMenuData
import com.changanford.my.databinding.FooterMineBinding
import com.changanford.my.databinding.FragmentMineV2Binding
import com.changanford.my.databinding.HeaderMineBinding
import com.changanford.my.viewmodel.MineViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


class MineFragment : BaseFragment<FragmentMineV2Binding, MineViewModel>(), OnRefreshListener {

    private var headNewBinding: HeaderMineBinding? = null
    private var couponNum = 0
    private var scrollY = 0
    private val mineMenuAdapter by lazy {
        MineMenuAdapter()
    }
    private val midMenuAdapter by lazy {
        MineMidRyAdapter()
    }

    private var footerAdBinding: FooterMineBinding? = null
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.toolbar, requireActivity())
        binding.toolbar.background.mutate().alpha = 0
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollY += dy
                MConstant.mineTabIsBlack = scrollY > 90.toIntPx()
                if (scrollY > 90.toIntPx()) {
                    StatusBarUtil.setLightStatusBar(requireActivity(), MConstant.mineTabIsBlack)
                    binding.toolbar.background.mutate().alpha = 255
                    binding.myScan.setColorFilter(Color.parseColor("#d9000000"))
                    binding.myMsg.setColorFilter(Color.parseColor("#d9000000"))
                    binding.ivSetting.setColorFilter(Color.parseColor("#d9000000"))
                } else {
                    StatusBarUtil.setLightStatusBar(requireActivity(), MConstant.mineTabIsBlack)
                    binding.toolbar.background.mutate().alpha = 0
                    binding.myScan.setColorFilter(Color.parseColor("#ffffff"))
                    binding.myMsg.setColorFilter(Color.parseColor("#ffffff"))
                    binding.ivSetting.setColorFilter(Color.parseColor("#ffffff"))
                }
            }
        })
        binding.recyclerView.adapter = mineMenuAdapter
        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setOnRefreshListener(this)
        addHeadView()
        addFooterView()
        LiveDataBus.get().with(LiveDataBusKey.MINE_SIGN_SIGNED).observe(this) {
            show7Day()
        }
    }

    var notSign = true

    /**
     * 处理点击事件
     */
    private fun initClick() {
        headNewBinding?.let { h ->
//            h.daySign.setOnClickListener {
//                if (notSign) {
//                    JumpUtils.instans?.jump(37)
//                } else {
//                    JumpUtils.instans?.jump(55)
//                }
//
//            }
            binding.ivSetting.setOnClickListener {
                JumpUtils.instans?.jump(21)

            }
            binding.myMsg.setOnClickListener {
                JumpUtils.instans?.jump(24)
            }
            binding.myScan.setOnClickListener {
                JumpUtils.instans?.jump(61)
            }
            h.ivHead.setOnClickListener {
                JumpUtils.instans?.jump(34)
            }
            h.tvUserLevel.setOnClickListener {
                JumpUtils.instans?.jump(32)
            }
            h.tvUserTags.setOnClickListener {
                JumpUtils.instans?.jump(29)

            }
            h.tvNickname.setOnClickListener {
                JumpUtils.instans?.jump(34)
            }
            h.tvNotLogin.setOnClickListener {
                JumpUtils.instans?.jump(34)

            }
//            h.vCarBg.setOnClickListener {
//                JumpUtils.instans?.jump(41)
//            }
//            h.tvCoupon.setOnClickListener {
//                if (MConstant.token.isNullOrEmpty()) {
//                    startARouter(ARouterMyPath.SignUI)
//                } else {
//                    var bundle = Bundle()
//                    bundle.putInt("couponNum", couponNum)
//                    startARouter(ARouterShopPath.CouponMiddleActivity, bundle)
//                }
//            }
//            h.flImgYouhui.setOnClickListener {
//                if (MConstant.token.isNullOrEmpty()) {
//                    startARouter(ARouterMyPath.SignUI)
//                } else {
//                    var bundle = Bundle()
//                    bundle.putInt("couponNum", couponNum)
//                    startARouter(ARouterShopPath.CouponMiddleActivity, bundle)
//                }
//            }
//            h.tvGold.setOnClickListener {
//                JumpUtils.instans?.jump(30)
//
//            }
//            h.flImgFb.setOnClickListener {
//                JumpUtils.instans?.jump(30)
//            }
//            h.tvTuijian.setOnClickListener {
//                JumpUtils.instans?.jump(106, tuijiangou)
//            }
//            h.flImgTuijian.setOnClickListener {
//                JumpUtils.instans?.jump(106, tuijiangou)
//            }
            h.ddFollow.setOnClickListener {
                JumpUtils.instans?.jump(25)
            }
            h.ddPublish.setOnClickListener {
                JumpUtils.instans?.jump(23)
            }
            h.fubilayout.setOnClickListener {
                JumpUtils.instans?.jump(16)
            }
            h.llFb.setOnClickListener {
                JumpUtils.instans?.jump(30)
            }
            h.rlExpire.setOnClickListener {
                startARouter(ARouterMyPath.JFExpireUI)
            }
            h.llUp.setOnClickListener {
                JumpUtils.instans?.jump(32)
            }
            h.ddFans.setOnClickListener {
                JumpUtils.instans?.jump(40)
            }
            h.tvCarName.setOnClickListener {
                JumpUtils.instans?.jump(17)
            }
            h.tvNextPerson.setOnClickListener {
                JumpUtils.instans?.jump(35)
            }
            h.tvGoSign.setOnClickListener {
                if (MConstant.token.isEmpty()) {
                    startARouter(ARouterMyPath.SignUI)
                    return@setOnClickListener
                }
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    RouterManger.needLogin(true).param("isSign", true)
                        .startARouter(ARouterMyPath.MineTaskListUI)
                }
            }
        }
    }

    private fun setHeaderUserView(userInfoBean: UserInfoBean?) {
        headNewBinding?.let { h ->
            if (userInfoBean != null) {
                GlideUtils.loadBD(userInfoBean.avatar, h.ivHead, R.mipmap.head_default)
                h.rlExpire.isVisible = userInfoBean.totalScore != 0
                h.tvExpireHint.text = userInfoBean.integralExpireMes
                h.tvNickname.text = userInfoBean.nickname
                h.ddPublish.setPageTitleText(userInfoBean.count.releases.toString())
                h.ddFans.setPageTitleText(userInfoBean.count.fans.toString())
                h.ddFollow.setPageTitleText(userInfoBean.count.follows.toString())
                h.tvUserLevel.visibility = View.VISIBLE

                h.tvUserTags.visibility = View.VISIBLE
                val couponStr = "优惠券"/*.plus("\t\t${userInfoBean.couponCount}")*/
                couponNum = userInfoBean.couponCount
                val goldStr = "福币".plus("\t${userInfoBean.ext.totalIntegral}")
                h.tvFbNum.text = userInfoBean.ext.totalIntegral
                h.tvUpNum.text = userInfoBean.ext.totalGrowth.toString()
//                h. tvUpNum.text = it.additionGrowth.toString()
//                h.tvCoupon.text = SpannableStringUtils.colorSpan(couponStr, 0, 3, R.color.color_66)
//                h.tvGold.text = SpannableStringUtils.colorSpan(goldStr, 0, 2, R.color.color_66)
                h.tvUserLevel.text = userInfoBean.ext.growSeriesName

                h.tvCarName.text = userInfoBean.ext.carOwner
                if (TextUtils.isEmpty(userInfoBean.ext.carOwner)) {
                    h.tvCarName.visibility = View.GONE
                } else {
                    h.tvCarName.visibility = View.VISIBLE
                }
                h.tvUserTags.text = userInfoBean.medalCount.toString().plus("枚勋章")

//                h.daySign.text = if (userInfoBean.isSignIn == 1) "已签到" else "签到"
                binding.messageStatus.visibility =
                    if (userInfoBean.isUnread == 1) View.VISIBLE else View.GONE
                LiveDataBus.get().with(LiveDataBusKey.SHOULD_SHOW_MY_MSG_DOT)
                    .postValue(userInfoBean.isUnread == 1)
                if (TextUtils.isEmpty(userInfoBean.ext.memberIcon)) {
                    h.ivVip.visibility = View.GONE
                } else {
                    GlideUtils.loadBD(userInfoBean.ext.memberIcon, h.ivVip)
                    h.ivVip.visibility = View.VISIBLE
                }
                h.tvNickname.visibility = View.VISIBLE
                h.tvNotLogin.visibility = View.GONE
                h.tvNextPerson.visibility = View.VISIBLE
            } else {
                h.tvNickname.text = ""
                h.tvNickname.visibility = View.GONE
                h.tvNotLogin.visibility = View.VISIBLE
                h.ivHead.load(R.mipmap.head_default)
                h.ddPublish.setPageTitleText("-")
                h.ddFans.setPageTitleText("-")
                h.ddFollow.setPageTitleText("-")
                h.tvUserLevel.visibility = View.GONE
                h.tvCarName.visibility = View.GONE
                h.tvUserTags.visibility = View.GONE
                val couponStr = "优惠券"/*.plus("\t\t0")*/
                val goldStr = "福币".plus("\t0")
                h.tvFbNum.text = "-"
                h.tvUpNum.text = "-"
//                h.tvCoupon.text = SpannableStringUtils.colorSpan(couponStr, 0, 3, R.color.color_66)
//                h.tvGold.text = SpannableStringUtils.colorSpan(goldStr, 0, 2, R.color.color_66)
//                h.daySign.text = "签到"
                binding.messageStatus.visibility = View.GONE
                h.tvUserTags.text = "0枚勋章"
                h.ivVip.visibility = View.GONE
                h.tvNextPerson.visibility = View.GONE
            }


        }

    }

    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_mine,
                binding.recyclerView,
                false
            )
            headNewBinding?.let {
                mineMenuAdapter.addHeaderView(it.root, 0)
                val spacingInPx = 8.toIntPx()
                it.ryMid.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position < 3) {
                            outRect.right = spacingInPx / 2
                        } else {
                            outRect.right = 0
                        }
                        if (position > 0) {
                            outRect.left = spacingInPx / 2
                        } else {
                            outRect.left = 0
                        }
//                        outRect.right = spacingInPx - (column + 1) * spacingInPx / 4; // spacing - (column + 1) * ((spacing / spanCount))
                    }
                })
                it.ryMid.adapter = midMenuAdapter
            }
            initClick()
        }

    }

    private fun addFooterView() {
        if (footerAdBinding == null) {
            footerAdBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.footer_mine,
                binding.recyclerView,
                false
            )
            footerAdBinding?.let {
                mineMenuAdapter.addFooterView(it.root)
            }
        }
    }

    override fun initData() {
        getUserInfo()
        viewModel.getMenuList()
//        viewModel.getAuthCarInfo()
        viewModel.getBottomAds()
    }

    private var loginState: MutableLiveData<Boolean> = MutableLiveData()
    private var authState: MutableLiveData<Int> = MutableLiveData()

    private var isRefreshUserInfo: Boolean = true //是否刷新用户信息

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {
        if (MConstant.token.isNullOrEmpty()) {
            setData(null)
        }
        viewModel.userInfo.observe(this) {
            setData(it)
        }
    }

    override fun observe() {
        super.observe()

        viewModel.menuBean.observe(this) {
            if (it.isNullOrEmpty()) return@observe
            val subSize = if (it.size > 4) 4 else it.size
            val midList = it.subList(0, subSize)
            val bottomList = it.subList(subSize, it.size)
            midMenuAdapter.setList(midList)
            val menu = MineMenuData("常用功能", bottomList)
            val list = arrayListOf<MineMenuData>()
            list.add(menu)
            viewModel.getOrderKey(list)


        }
        viewModel.updateOrderAdLiveData.observe(this, Observer {
            mineMenuAdapter.setList(it.data)
//            viewModel.getCarListAds(it.data as ArrayList<MineMenuData>)

        })
        viewModel.updateCarAdLiveData.observe(this, Observer {
            mineMenuAdapter.setList(it.data)
//            mineMenuAdapter.setNewInstance(it.data as MutableList<MineMenuData>?)
        })
        viewModel.adListLiveData.observe(this, Observer {
            showAdView(it)

        })
        viewModel.mOrderTypesLiveData.observe(this, Observer {


        })
        LiveDataBus.get().with(MConstant.REFRESH_USER_INFO, Boolean::class.java)
            .observe(this) {
                if (it) {
                    viewModel.getUserInfo()
                }
            }
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                // 收到 登录状态改变回调都要刷新页面
//                viewModel.getAuthCarInfo()// 登录状态改变
            }
    }

    fun showAdView(list: ArrayList<AdBean>?) {
        footerAdBinding?.let { f ->
            list?.let { l ->
                when (l.size) {
                    0 -> {
                        f.gThree.visibility = View.GONE
                        f.gTwo.visibility = View.GONE
                        f.ivOne.visibility = View.GONE
                    }

                    1 -> {
                        f.gThree.visibility = View.GONE
                        f.gTwo.visibility = View.GONE
                        f.ivOne.visibility = View.VISIBLE
                        GlideUtils.loadBD(l[0].getAdImgUrl(), f.ivOne)
                        f.ivOne.setOnClickListener {
                            JumpUtils.instans?.jump(l[0].jumpDataType, l[0].jumpDataValue)
                        }

                    }

                    2 -> {
                        f.gThree.visibility = View.GONE
                        f.gTwo.visibility = View.VISIBLE
                        f.ivOne.visibility = View.GONE
                        GlideUtils.loadBD(l[0].getAdImgUrl(), f.ivTwoAd)
                        f.ivTwoAd.setOnClickListener {
                            JumpUtils.instans?.jump(l[0].jumpDataType, l[0].jumpDataValue)

                        }
                        GlideUtils.loadBD(l[1].getAdImgUrl(), f.ivTwoAdTwo)
                        f.ivTwoAdTwo.setOnClickListener {
                            JumpUtils.instans?.jump(l[1].jumpDataType, l[1].jumpDataValue)

                        }

                    }

                    else -> {
                        if (l.size >= 3) {
                            f.gThree.visibility = View.VISIBLE
                            f.gTwo.visibility = View.GONE
                            f.ivOne.visibility = View.GONE
                            GlideUtils.loadBD(l[0].getAdImgUrl(), f.ivThreeOne)
                            f.ivThreeOne.setOnClickListener {
                                JumpUtils.instans?.jump(l[0].jumpDataType, l[0].jumpDataValue)

                            }
                            GlideUtils.loadBD(l[1].getAdImgUrl(), f.ivThreeTwo)
                            f.ivThreeTwo.setOnClickListener {
                                JumpUtils.instans?.jump(l[1].jumpDataType, l[1].jumpDataValue)

                            }
                            GlideUtils.loadBD(l[2].getAdImgUrl(), f.ivThreeThree)
                            f.ivThreeThree.setOnClickListener {
                                JumpUtils.instans?.jump(l[2].jumpDataType, l[2].jumpDataValue)
                            }
                        }

                    }
                }


            }

        }

    }


    private fun setData(userInfoBean: UserInfoBean?) {
        if (userInfoBean == null) {
            loginState.postValue(false)
            authState.postValue(0)//未登录
            setHeaderUserView(null)
        } else { // 已登录
            isRefreshUserInfo = false
            loginState.postValue(true)
            notSign = userInfoBean.isSignIn != 1
            setHeaderUserView(userInfoBean)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo()
        viewModel.getMenuList()
        viewModel.getAuthCarInfo()
//        viewModel.getCircleInfo()
//        viewModel.getTuijianGou {
//            it.onSuccess {
//                if (it?.data.isNullOrEmpty()) {
//                    headNewBinding?.let { h ->
//                        h.tvTuijian.isVisible = false
//                        h.flImgTuijian.isVisible = false
//                    }
//                } else {
//                    headNewBinding?.let { h ->
//                        h.tvTuijian.isVisible = true
//                        h.flImgTuijian.isVisible = true
//                    }
//                    tuijiangou = it?.data ?: ""
//                }
//            }
//
//        }
//        headNewBinding?.vFlipper?.startFlipping()
        show7Day()
    }

    override fun onPause() {
        super.onPause()
        // 停止播放
//        headNewBinding?.vFlipper?.stopFlipping()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun show7Day() {
        if (MConstant.token.isNullOrEmpty()) {
            headNewBinding?.apply {
                tvFbNum.text = "-"
                tvUpNum.text = "-"
                tvGoSign.isVisible = true
                llSignDays.isVisible = false
            }
        } else {
            viewModel.getDay7Sign {
                var canSign = it == null || MConstant.token.isNullOrEmpty()
                it?.sevenDays?.forEach {
                    if (it.signStatus == 2) {
                        canSign = true
                    }
                }
                headNewBinding?.apply {
                    if (canSign) {
                        tvGoSign.isVisible = true
                        llSignDays.isVisible = false
                    } else {
                        tvGoSign.isVisible = false
                        llSignDays.isVisible = true
                        tvSignDayNum.text = it.ontinuous.toString()
                    }
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        initData()
//        viewModel.getUserInfo()
//        viewModel.getAuthCarInfo()
//        show7Day()
        refreshLayout.finishRefresh()
    }
}