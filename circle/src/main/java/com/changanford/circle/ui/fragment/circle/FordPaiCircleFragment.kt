package com.changanford.circle.ui.fragment.circle

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.databinding.FragmentFordPaiCircleBinding
import com.changanford.circle.utils.CommunityHotHelper
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
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

    private val testList = arrayListOf(
        "https://ask.qcloudimg.com/http-save/yehe-9812395/89d0648d46512f3f6062a7b65e237b99.png",
        "https://ask.qcloudimg.com/http-save/yehe-9812395/89d0648d46512f3f6062a7b65e237b99.png",
        "https://ask.qcloudimg.com/http-save/yehe-9812395/89d0648d46512f3f6062a7b65e237b99.png"
    )


    override fun initView() {

        binding.apply {
            CommunityHotHelper(binding.layoutHot, viewModel, this@FordPaiCircleFragment).initCommunity()
        }
        setCircleNumSize(true, 3.toString())
        setCircleNumSize(false, 81.toString())
        initListener()
        setTopCircleData()
    }

    private fun initListener() {
        binding.apply {
            vLeft.setOnClickListener { selectTab.value = 0 }
            vRight.setOnClickListener { selectTab.value = 1 }
            tvJoinNum.setOnClickListener { showMyCirclePop() }
            ivIconRight.setOnClickListener { showMyCirclePop() }
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
        selectTab.observe(this) {
            binding.apply {
                if (it == 0) {

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
                    layoutParam.topMargin = 15.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 18f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 27.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 14f

                } else {

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
                    layoutParam.topMargin = 27.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 14f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 15.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 18f
                }
            }

        }


    }


    private fun setTopCircleData() {
        val leftViews = arrayListOf<ShapeableImageView>()
        val rightViews = arrayListOf<ShapeableImageView>()
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
        leftViews.forEachIndexed { index, shapeableImageView ->
            shapeableImageView.setCircular(4)
            shapeableImageView.load(testList[index])
        }
        rightViews.forEachIndexed { index, shapeableImageView ->
            shapeableImageView.setCircular(4)
            shapeableImageView.load(testList[index])
        }
    }

    private fun showMyCirclePop() {
        MyCirclePop(requireContext()).run {
            setBackgroundColor(Color.TRANSPARENT)
            showPopupWindow(binding.vPop)
            initPopData(testList, "")
        }
    }
}