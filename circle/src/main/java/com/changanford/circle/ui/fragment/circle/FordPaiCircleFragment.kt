package com.changanford.circle.ui.fragment.circle

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.databinding.FragmentFordPaiCircleBinding
import com.changanford.circle.utils.CommunityCircleHelper
import com.changanford.circle.utils.CommunityHotHelper
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.CirceHomeBean
import com.changanford.common.manger.UserManger
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx
import com.changanford.common.widget.pop.MyCirclePop
import com.google.android.material.imageview.ShapeableImageView


/**
 * @author: niubobo
 * @date: 2024/3/12
 * @description：
 */
class FordPaiCircleFragment : BaseFragment<FragmentFordPaiCircleBinding, NewCircleViewModel>() {

    private var selectTab = MutableLiveData<Int>()
    private lateinit var communityHotHelper: CommunityHotHelper
    private lateinit var communityCircleHelper: CommunityCircleHelper
    private val leftViews = arrayListOf<ShapeableImageView>()
    private val rightViews = arrayListOf<ShapeableImageView>()
    private var nowCircleId: String = ""
    private var isLoginChange = false

    override fun initView() {
        communityHotHelper = CommunityHotHelper(
            binding.layoutHot,
            binding,
            viewModel,
            this@FordPaiCircleFragment
        )
        communityCircleHelper =
            CommunityCircleHelper(binding.layoutCircle, this)
        communityHotHelper.initCommunity()
        if (MConstant.token.isEmpty()) {
            selectTab.value = 1
        }
        initListener()
        setTopCircleData()
        addLiveDataBus()
    }

    private fun initListener() {
        binding.apply {
            vLeft.setOnClickListener { selectTab.value = 0 }
            vRight.setOnClickListener { selectTab.value = 1 }
            tvJoinNum.setOnClickListener { showMyCirclePop() }
            ivIconRight.setOnClickListener { showMyCirclePop() }
        }
        binding.srl.setOnRefreshListener {
            communityHotHelper.initData()
            it.finishRefresh()
        }
    }

    private fun setCircleNumSize(isLeft: Boolean, num: String) {
        val content = if (isLeft) "已加入" else "待加入"
        val all = "$content${num}个"
        val spanString = SpannableString(all)
        val span = AbsoluteSizeSpan(45)

        spanString.setSpan(
            span,
            content.length,
            content.length + num.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        if (isLeft) {
            binding.tvJoinNum.text = spanString
        } else {
            binding.tvHotNum.text = spanString
        }
    }

    override fun initData() {

    }


    override fun observe() {
        super.observe()
        LiveDataBus.get().withs<String>(LiveDataBusKey.HOME_CIRCLE_CHECK_ID).observe(this) {
            nowCircleId = it
            communityCircleHelper.initCommunity(it)
        }
        selectTab.observe(this) {
            binding.apply {
                if (it == 0) {
                    isShowCircle(false)
                    ivTabBg.setImageResource(com.changanford.circle.R.mipmap.ic_circle_tab_my_circle)
                    tvMyCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.color_16
                        )
                    )
                    tvJoinNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.color_16
                        )
                    )
                    tvHotCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.white
                        )
                    )
                    tvHotNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.white
                        )
                    )
                    ivIconRight.setImageResource(com.changanford.circle.R.mipmap.ic_circle_jion_num_right_b)

                    val layoutParam = tvMyCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam.topMargin = 45.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 18f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 60.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 14f

                } else {
                    isShowCircle(true)
                    ivTabBg.setImageResource(com.changanford.circle.R.mipmap.ic_circle_tab_hot_circle)
                    tvMyCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.white
                        )
                    )
                    tvJoinNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.white
                        )
                    )
                    tvHotCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.color_16
                        )
                    )
                    tvHotNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.changanford.circle.R.color.color_16
                        )
                    )
                    ivIconRight.setImageResource(com.changanford.circle.R.mipmap.ic_circle_jion_num_right_w)

                    val layoutParam = tvMyCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam.topMargin = 50.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 14f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 35.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 18f
                }
            }

        }

        communityHotHelper.myCircles.observe(this) {
            if (!it.isNullOrEmpty()) {
                communityCircleHelper.initCommunity(it[0].circleId)

                nowCircleId = it[0].circleId
                val useList = if (it.size > 3) {
                    it.subList(0, 3)
                } else it
                useList.forEachIndexed { index, data ->
                    leftViews[index].setCircular(4)
                    leftViews[index].load(data.pic)
                }
            }
        }
        LiveDataBus.get().withs<CirceHomeBean?>(LiveDataBusKey.HOME_CIRCLE_HOT_BEAN).observe(this) {
            it?.let {
                setCircleNumSize(false, it.noJoinCircleNum.toString())
                setCircleNumSize(true, it.joinCircleNum.toString())
                val useList = if (it.noJoinCirclePics.size > 3) {
                    it.noJoinCirclePics.subList(0, 3)
                } else it.noJoinCirclePics
                useList.forEachIndexed { index, data ->
                    rightViews[index].setCircular(4)
                    rightViews[index].load(data)
                }
            }
        }
    }

    private fun isShowCircle(isShowLeft: Boolean) {
        binding.layoutHot.root.isVisible = isShowLeft
        binding.layoutCircle.root.isVisible = !isShowLeft
    }

    private fun setTopCircleData() {

        binding.layoutLeftThree.apply {
            leftViews.add(ivOne)
            leftViews.add(ivTwo)
            leftViews.add(ivThree)
        }
        binding.layoutRightThree.apply {
            rightViews.add(ivOne)
            rightViews.add(ivTwo)
            rightViews.add(ivThree)
        }

    }

    private fun showMyCirclePop() {
        MyCirclePop(requireContext()).run {
            setBackgroundColor(Color.TRANSPARENT)
            showPopupWindow(binding.vPop)
            communityHotHelper.myCircles.value?.let { initPopData(it, nowCircleId) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLoginChange) {
            isLoginChange = false
            selectTab.value = 0
            communityHotHelper.initData()
        }
    }

    private fun addLiveDataBus() {
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        isLoginChange = true
                    }

                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        isLoginChange = true
                    }

                    else -> {}
                }
            }
    }
}