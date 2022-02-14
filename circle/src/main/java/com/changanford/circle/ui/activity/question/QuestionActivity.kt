package com.changanford.circle.ui.activity.question

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.changanford.circle.BuildConfig
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityQuestionBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.compose.BtnQuestionCompose
import com.changanford.circle.ui.compose.ComposeQuestionTop
import com.changanford.circle.ui.fragment.question.QuestionFragment
import com.changanford.circle.viewmodel.question.QuestionViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.QuestionTagBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.StatusBarUtil
import com.google.android.material.appbar.AppBarLayout
import com.luck.picture.lib.tools.ScreenUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
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
class QuestionActivity:BaseActivity<ActivityQuestionBinding, QuestionViewModel>(),
    OnRefreshListener {
    companion object{
        /**
         * [conQaUjId]被查看人的问答参与表id
         * [type]0普通用户问答个人页面 、1车主问答个人页面、2技师问答个人页面  3 TA的问答
         * [personalPageType]
        * */
        fun start(conQaUjId:String?=null,type:Int?=0,personalPageType:Int?=0){
//            startARouter(ARouterCirclePath.QuestionActivity)
            JumpUtils.instans?.jump(114,"{\"conQaUjId\": \"$conQaUjId\",\"type\": \"${type?:0}\",\"personalPageType\":\"${personalPageType?:0}\"}")
        }
        /**
         * [conQaUjId]被查看人的问答参与表id
         * */
        fun start(conQaUjId:String?=null){
            if(conQaUjId==null&&BuildConfig.DEBUG)JumpUtils.instans?.jump(114,"15")
            else JumpUtils.instans?.jump(114,conQaUjId)
        }
    }
    private var isWhite = true//是否是白色状态
    private var conQaUjId:String="15"
    private var type=0
    private val fragments= arrayListOf<QuestionFragment>()
    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.transparent)
        initSmartRefreshLayout()
        initAppbarLayout()
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
                JumpUtils.instans?.jump(116)
            }
        }
        binding.composeViewQuestion.setContent {
            BtnQuestionCompose()
        }
    }
    override fun initData() {
        viewModel.questionInfoBean.observe(this){
            it?.apply {
                //是否显示提问入口
                binding.inHeader.tvAskQuestions.visibility=if(it.getIsQuestion())View.VISIBLE else View.GONE
                binding.composeView.setContent {
                    ComposeQuestionTop(this@QuestionActivity,this)
                }
                if(fragments.size>0){
                    fragments[binding.viewPager.currentItem].startRefresh()
                }else{
                    val tabs=it.getTabs(this@QuestionActivity)
                    initTabAndViewPager(tabs,isOneself(),getIdentity())
                    initMagicIndicator(tabs)
                }
            }
            binding.smartRl.finishRefresh()
        }
        viewModel.personalQA(conQaUjId)
    }
    private fun initSmartRefreshLayout(){
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { _: AppBarLayout?, i: Int ->
            binding.smartRl.isEnabled = i >= 0
        } as AppBarLayout.BaseOnOffsetChangedListener<*>)
        binding.smartRl.setOnRefreshListener(this)
    }
    private fun initTabAndViewPager(tabs:MutableList<QuestionTagBean>,isOneself:Boolean,identity:Int) {
        for(position in 0 until tabs.size){
            fragments.add(QuestionFragment.newInstance(conQaUjId,tabs[position].tag?:"",isOneself,identity))
        }
        binding.viewPager.apply {
            removeAllViews()
            adapter = @SuppressLint("WrongConstant")
        object : FragmentPagerAdapter(supportFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getCount(): Int {
                    return tabs.size
                }
                override fun getItem(position: Int): Fragment {
                    return fragments[position]
                }
            }
            binding.composeViewQuestion.visibility=if(isOneself&&tabs[currentItem].tag=="QUESTION"){
                addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                    override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        binding.composeViewQuestion.visibility=if(tabs[position].tag=="QUESTION")View.VISIBLE else View.GONE
                    }
                    override fun onPageScrollStateChanged(state: Int) {}
                })
                View.VISIBLE
            }else View.GONE
        }
    }

    private fun initMagicIndicator(tabs:MutableList<QuestionTagBean>) {
        val magicIndicator = binding.magicTab
        magicIndicator.removeAllViews()
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
                    text = tabs[index].tagName
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.personalQA(conQaUjId)
    }
}