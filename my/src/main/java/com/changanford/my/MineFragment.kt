package com.changanford.my

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.my.adapter.MineFastUsedAdapter
import com.changanford.my.adapter.MineMenuAdapter
import com.changanford.my.bean.MineMenuData
import com.changanford.my.databinding.FooterMineBinding
import com.changanford.my.databinding.FragmentMineV2Binding
import com.changanford.my.databinding.HeaderMineBinding
import com.changanford.my.viewmodel.MineViewModel


class MineFragment : BaseFragment<FragmentMineV2Binding, MineViewModel>() {


    var headNewBinding: HeaderMineBinding? = null

    val mineMenuAdapter: MineMenuAdapter by lazy {
        MineMenuAdapter()
    }

    var footerAdBinding: FooterMineBinding? = null
    override fun initView() {


        binding.recyclerView.adapter = mineMenuAdapter
        binding.smartLayout.setEnableLoadMore(false)
        addHeadView()
        addFooterView()

    }

    var notSign = true

    /**
     * 处理点击事件
     */
    private fun initClick() {
        headNewBinding?.let { h ->
            h.daySign.setOnClickListener {
                if (notSign) {
                    JumpUtils.instans?.jump(37)
                } else {
                    JumpUtils.instans?.jump(55)
                }

            }
            h.ivSetting.setOnClickListener {
                JumpUtils.instans?.jump(21)

            }
            h.myMsg.setOnClickListener {
                JumpUtils.instans?.jump(24)
            }
            h.myScan.setOnClickListener {
                JumpUtils.instans?.jump(61)
            }
            h.ivHead.setOnClickListener {
                JumpUtils.instans?.jump(34)
            }
            h.tvNickname.setOnClickListener {
                JumpUtils.instans?.jump(34)
            }
            h.vCarBg.setOnClickListener {
                JumpUtils.instans?.jump(17)
            }
            h.tvCoupon.setOnClickListener {
                JumpUtils.instans?.jump(118)
            }
            h.tvGold.setOnClickListener {
                JumpUtils.instans?.jump(30)

            }
        }
    }

    private fun setHeaderUserView(userInfoBean: UserInfoBean?) {
        headNewBinding?.let { h ->
            if (userInfoBean != null) {
                GlideUtils.loadBD(userInfoBean.avatar, h.ivHead, R.mipmap.head_default)
                h.tvNickname.text = userInfoBean.nickname
                h.ddPublish.setPageTitleText(userInfoBean.count.releases.toString())
                h.ddFans.setPageTitleText(userInfoBean.count.fans.toString())
                h.ddFollow.setPageTitleText(userInfoBean.count.follows.toString())
                h.tvUserLevel.visibility = View.VISIBLE
                h.tvLoveCarTips.visibility = View.VISIBLE
                h.tvUserTags.visibility = View.VISIBLE
                val couponStr = "优惠券".plus("\t\t${userInfoBean.couponCount}")
                val goldStr = "福币".plus("\t\t${userInfoBean.ext.totalIntegral}")
                h.tvCoupon.text = SpannableStringUtils.colorSpan(couponStr, 0, 3, R.color.black)
                h.tvGold.text = SpannableStringUtils.colorSpan(goldStr, 0, 2, R.color.black)
                h.tvUserLevel.text = userInfoBean.ext.growSeriesName
                h.tvCarName.text = userInfoBean.ext.carOwner
                h.tvCarName.visibility = View.VISIBLE
                h.daySign.text = if (userInfoBean.isSignIn == 1) "已签到" else "签到"
                h.messageStatus.visibility =
                    if (userInfoBean.isUnread == 1) View.VISIBLE else View.GONE
                LiveDataBus.get().with(LiveDataBusKey.SHOULD_SHOW_MY_MSG_DOT)
                    .postValue(userInfoBean.isUnread == 1)
            } else {
                h.tvNickname.text = "登录/注册"
                h.ddPublish.setPageTitleText("0")
                h.ddFans.setPageTitleText("0")
                h.ddFollow.setPageTitleText("0")
                h.tvUserLevel.visibility = View.GONE
                h.tvLoveCarTips.visibility = View.GONE
                h.tvCarName.visibility = View.GONE
                h.tvUserTags.visibility = View.GONE
                val couponStr = "优惠券".plus("\t\t0")
                val goldStr = "福币".plus("\t\t0")
                h.tvCoupon.text = SpannableStringUtils.colorSpan(couponStr, 0, 3, R.color.black)
                h.tvGold.text = SpannableStringUtils.colorSpan(goldStr, 0, 2, R.color.black)
                h.daySign.text = "签到"
                h.messageStatus.visibility = View.GONE
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
        viewModel.getAuthCarInfo()
        viewModel.getCircleInfo()
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
        viewModel.carAuthBean.observe(this, Observer {
            setCarInfo(it)

        })
    }

    override fun observe() {
        super.observe()
        viewModel.menuBean.observe(this, Observer {
            val menu = MineMenuData("常用功能", it)
            val list = arrayListOf<MineMenuData>()
            list.add(viewModel.myOrders())
            list.add(menu)
            mineMenuAdapter.setNewInstance(list)
        })
        viewModel.adListLiveData.observe(this, Observer {
            showAdView(it)

        })
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

    fun setCarInfo(carAuthBean: CarAuthBean?) {
        headNewBinding?.let { h ->
            if (carAuthBean?.isCarOwner == 1 && carAuthBean.carList != null) {
                carAuthBean.carList?.let { l ->
                    var carInfo = l.get(0)
                    GlideUtils.loadBD(carInfo.modelUrl, h.ivCarPic, R.mipmap.head_default)
                    h.tvAddLoveCar.text = carInfo.seriesName
                }

            } else {
                GlideUtils.loadBD(
                    carAuthBean?.carAuthConfVo?.img,
                    h.ivCarPic,
                    R.mipmap.head_default
                )
                h.tvAddLoveCar.text = "我的爱车"
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
    }
}