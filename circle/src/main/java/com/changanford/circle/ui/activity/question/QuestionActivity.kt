package com.changanford.circle.ui.activity.question

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityQuestionBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.compose.ComposeQuestionTop
import com.changanford.circle.ui.fragment.question.QuestionFragment
import com.changanford.circle.viewmodel.question.QuestionViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CirCleHotList
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.StatusBarUtil
import com.google.android.material.appbar.AppBarLayout
import com.luck.picture.lib.tools.ScreenUtils
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import kotlin.math.abs

/**
 * @Author : wenke
 * @Time : 2022/1/24
 * @Description : 我的问答、TA的问答
 */
@Route(path = ARouterCirclePath.QuestionActivity)
class QuestionActivity:BaseActivity<ActivityQuestionBinding, QuestionViewModel>() {
    companion object{
        /**
         * [conQaUjId]被查看人的问答参与表id
         * [type]0普通用户问答个人页面 、1车主问答个人页面、2技师问答个人页面  3 TA的问答
         * [personalPageType]
        * */
        fun start(conQaUjId:String?=null,type:Int?=0,personalPageType:Int?=0){
            startARouter(ARouterCirclePath.QuestionActivity)
            JumpUtils.instans?.jump(114,"{\"conQaUjId\": \"$conQaUjId\",\"type\": \"${type?:0}\",\"personalPageType\":\"${personalPageType?:0}\"}")
        }
        /**
         * [conQaUjId]被查看人的问答参与表id
         * */
        fun start(conQaUjId:String?=null){
            startARouter(ARouterCirclePath.QuestionActivity)
            JumpUtils.instans?.jump(114,conQaUjId)
        }
    }
    private var isWhite = true//是否是白色状态
    private var conQaUjId:String=""
    private var type=0
    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.transparent)
        intent.getStringExtra("value")?.apply {
            if(this.startsWith("{")){
                JSON.parseObject(this)?.apply {
                    conQaUjId=getString("conQaUjId")
                    type=getIntValue("type")
                }
            }else{
                conQaUjId=this
            }
        }
        binding.inHeader.run {
            imgBack.setOnClickListener { finish() }
            topBar.setPadding(0,ScreenUtils.getStatusBarHeight(this@QuestionActivity)+10,0,ScreenUtils.dip2px(this@QuestionActivity,10f))
            tvAskQuestions.setOnClickListener {
                startARouter(ARouterCirclePath.CreateQuestionActivity)
            }
        }
        initAppbarLayout()
    }
    override fun initData() {
        initTestData()
        viewModel.questionInfoBean.observe(this){
            binding.composeView.setContent {
                ComposeQuestionTop(it)
            }
        }
        viewModel.personalQA(conQaUjId)
    }
    private fun initTestData(){
        val tabs= arrayListOf<CirCleHotList>()
        val tabName= arrayListOf("我的提问","我的回答","回答被采纳")
        for (i in 0..2){
            tabs.add(CirCleHotList(topName = tabName[i]))
        }
        initTabAndViewPager(tabs)
        initMagicIndicator(tabs)
    }
    private fun initTabAndViewPager(tabs:MutableList<CirCleHotList>) {
        binding.viewPager.apply {adapter = @SuppressLint("WrongConstant")
        object : FragmentPagerAdapter(supportFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getCount(): Int {
                    return tabs.size
                }
                override fun getItem(position: Int): Fragment {
                    return QuestionFragment.newInstance(tabs[position].topId)
                }
            }
            offscreenPageLimit = 3
        }
    }

    private fun initMagicIndicator(tabs:MutableList<CirCleHotList>) {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundResource(R.color.color_F4)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabs.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.apply {
                    gravity= Gravity.CENTER_HORIZONTAL
                    text = tabs[index].topName
                    textSize = 18f
                    setPadding(10.toIntPx(), 0, 10.toIntPx(), 0)
                    width= com.changanford.common.wutil.ScreenUtils.getScreenWidth(this@QuestionActivity)/3
                    normalColor = ContextCompat.getColor(this@QuestionActivity, R.color.color_33)
                    selectedColor = ContextCompat.getColor(this@QuestionActivity, R.color.circle_app_color)
                    setOnClickListener { binding.viewPager.currentItem = index }
                    return this
                }
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                    lineWidth = UIUtil.dip2px(context, 22.0).toFloat()
                    roundRadius = UIUtil.dip2px(context, 1.5).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(2.0f)
                    setColors(
                        ContextCompat.getColor(this@QuestionActivity, R.color.circle_app_color)
                    )
                    return this
                }

            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }
    private fun initAppbarLayout(){
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.inHeader.apply {
                    imgBack.setImageResource(R.mipmap.whit_left)
                    tvAskQuestions.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.colorWhite))
                    tvTitle.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.colorWhite))
                }
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.inHeader.apply {
                    imgBack.setImageResource(R.mipmap.back_xhdpi)
                    tvAskQuestions.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.color_33))
                    tvTitle.setTextColor(ContextCompat.getColor(this@QuestionActivity,R.color.color_33))
                }
                isWhite = false
            }
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.inHeader.topBar.background.mutate().alpha = mAlpha
//                binding.tvTitle.alpha = mAlpha / 255.0F
            } else {
                binding.inHeader.topBar.background.mutate().alpha = 255
//                binding.tvTitle.alpha = 1.0F
            }
        })
    }
}