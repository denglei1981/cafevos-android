package com.changanford.my

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.Imag
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.changanford.evos.databinding.ActivityPersonCenterBinding
import com.changanford.my.adapter.LabelAdapter
import com.changanford.my.adapter.MtViewPagerAdapter
import com.changanford.my.fragment.HomePageFragment
import com.changanford.my.request.PersonCenterViewModel
import com.changanford.my.utils.toIntPx
import com.changanford.widget.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView


// 个人主页
@Route(path = ARouterMyPath.PersonCenterActivity)
class PersonCenterActivity :BaseActivity<ActivityPersonCenterBinding, PersonCenterViewModel>() {
    private val tabList = arrayListOf("主页", "帖子", "收藏")

    val  userId=  MConstant.userId

    var  taUserId =""

    val   postFragment: PostFragment by  lazy{
        if(TextUtils.isEmpty(taUserId)){
            PostFragment.newInstance("centerPost", userId)
        }else{
            PostFragment.newInstance("centerPost", taUserId)
        }
    }
    val homePageFragment: HomePageFragment by lazy {
        // 日了狗了。
        if(TextUtils.isEmpty(taUserId)){
            HomePageFragment.newInstance("centerPost", userId)
        }else{
            HomePageFragment.newInstance("centerPost", taUserId)
        }
    }
//    val  askRecommendFragment:AskRecommendFragment by lazy{
//        AskRecommendFragment.newInstance()
//    }
//    val   newCircleFragment:NewCircleFragment by lazy{
//        NewCircleFragment()
//
//    }
    var fragmentList: ArrayList<Fragment> = arrayListOf()
    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            offscreenPageLimit = 1
        }
       fragmentList.add(homePageFragment)
        fragmentList.add(postFragment)
//        fragmentList.add(newCircleFragment)
//        fragmentList.add(askRecommendFragment)
        binding.viewPager.adapter=   MtViewPagerAdapter(this,fragmentList)

    }
    override fun initView() {
        intent.extras?.let { bundle ->
            bundle.getString("value")?.let {
                taUserId = it
            }
        }

    }

    override fun initData() {
        initTabAndViewPager()
        initMagicIndicator()
        viewModel.queryOtherInfo(userId) {
            it.onSuccess { user ->
                 showUserInfo(user)
            }
            it.onWithMsgFailure {e->
                e?.toast()
            }
        }
    }

    private fun showUserInfo(userInfoBean: UserInfoBean?) {
        userInfoBean?.let {
            when(userInfoBean.status){
                2->{ // 用户已注销。

                }
                else->{
                    GlideUtils.loadBD(userInfoBean.avatar,binding.topContent.ivHead) // 头像
                    binding.topContent.tvNickname.text=userInfoBean.nickname
                    binding.topContent.ddPublish.setPageTitleText(userInfoBean.count.releases.toString())
                    binding.topContent.ddFans.setPageTitleText(userInfoBean.count.fans.toString())
                    binding.topContent.ddFollow.setPageTitleText(userInfoBean.count.follows.toString())
                    binding.topContent.tvUserLevel.text = userInfoBean.ext.growSeriesName
                    binding.topContent.tvCarName.text = userInfoBean.ext.carOwner
                    if (TextUtils.isEmpty(userInfoBean.ext.carOwner)) {
                        binding.topContent.tvCarName.visibility = View.GONE
                    } else {
                        binding.topContent.tvCarName.visibility = View.VISIBLE
                    }
                    if(TextUtils.isEmpty(userInfoBean.ext.memberIcon)){
                        binding.topContent.ivVip.visibility=View.GONE
                    }else{
                        GlideUtils.loadBD(userInfoBean.ext.memberIcon,binding.topContent.ivVip)
                        binding.topContent.ivVip.visibility=View.VISIBLE
                    }
                    binding.topContent.tvSign.text =
                        if (userInfoBean.brief.isNullOrEmpty()) "这个人很懒~" else userInfoBean.brief


                        //用户图标
                        userInfoBean.userMedalList.let { imgs ->
                            var imgList= arrayListOf<Imag>()
                            imgs?.forEach { i->
                                imgList.add(Imag(i.medalImage,-1,""))

                            }
                            binding.topContent.rvMedal.visibility = View.VISIBLE
                            binding.topContent.rvMedal.adapter = LabelAdapter(20).apply {
                                addData(imgList)
                            }
                        }
                        binding.topContent.tvTotal.text = "共".plus(userInfoBean.medalCount.toString().plus("枚"))
                    binding.topContent.ddFollow.setOnClickListener {
                        if(taUserId==userId||TextUtils.isEmpty(taUserId)){
                            JumpUtils.instans?.jump(25)
                        }else{
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
                    binding.topContent.ddPublish.setOnClickListener {
                        JumpUtils.instans?.jump(23)
                    }
                    binding.topContent.ddFans.setOnClickListener {
                        if(taUserId==userId||TextUtils.isEmpty(taUserId)){
                            JumpUtils.instans?.jump(40)
                        }else{
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


    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
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
                simplePagerTitleView.setPadding(10.toIntPx(), 0, 10.toIntPx(), 0)
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
}