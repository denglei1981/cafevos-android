package com.changanford.circle.ui.activity.question

import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityQuestionBinding
import com.changanford.circle.ui.compose.ComposeQuestionTop
import com.changanford.circle.viewmodel.QuestionViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.StatusBarUtil
import com.google.android.material.appbar.AppBarLayout
import com.luck.picture.lib.tools.ScreenUtils
import kotlin.math.abs

/**
 * @Author : wenke
 * @Time : 2022/1/24
 * @Description : 我的问答、TA的问答
 */
@Route(path = ARouterCirclePath.QuestionActivity)
class QuestionActivity:BaseActivity<ActivityQuestionBinding, QuestionViewModel>() {
    companion object{
        fun start(){
            startARouter(ARouterCirclePath.QuestionActivity)
        }
    }
    private var isWhite = true//是否是白色状态
    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.transparent)
        binding.run {
            imgBack.setOnClickListener { finish() }
            topBar.setPadding(0,ScreenUtils.getStatusBarHeight(this@QuestionActivity)+10,0,ScreenUtils.dip2px(this@QuestionActivity,10f))
        }
        initAppbarLayout()
        binding.composeView.setContent {
            ComposeQuestionTop()
        }
    }

    override fun initData() {

    }
    private fun initAppbarLayout(){
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.apply {
                    imgBack.setImageResource(R.mipmap.whit_left)
                    tvAskQuestions.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.colorWhite))
                    tvTitle.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.colorWhite))
                }
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.apply {
                    imgBack.setImageResource(R.mipmap.back_xhdpi)
                    tvAskQuestions.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.color_33))
                    tvTitle.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.color_33))
                }
                isWhite = false
            }
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.topBar.background.mutate().alpha = mAlpha
//                binding.tvTitle.alpha = mAlpha / 255.0F
            } else {
                binding.topBar.background.mutate().alpha = 255
//                binding.tvTitle.alpha = 1.0F
            }
        })
    }
}