package com.changanford.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.DisplayUtil
import com.changanford.common.utilext.logE
import com.changanford.home.acts.fragment.ActsListFragment
import com.changanford.home.callback.ICallback
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentHomeRecommendBinding
import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.shot.fragment.BigShotFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@Deprecated("使用HomeV2Fragment,布局也过时了")
class HomeFragment : BaseFragment<FragmentHomeRecommendBinding, EmptyViewModel>() {

    var pagerAdapter: HomeViewPagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()

    var immersionBar: ImmersionBar? = null

    override fun initView() {
        //Tab+Fragment
        immersionBar = ImmersionBar.with(requireActivity())
        immersionBar?.fitsSystemWindows(true)

        fragmentList.add(RecommendFragment.newInstance())
        fragmentList.add(ActsListFragment.newInstance())
        fragmentList.add(NewsListFragment.newInstance())
        fragmentList.add(BigShotFragment.newInstance())

        titleList.add(getString(R.string.home_recommend))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_big_shot))

        pagerAdapter = HomeViewPagerAdapter(this, fragmentList)
        binding.homeViewpager.adapter = pagerAdapter

        binding.homeViewpager.isSaveEnabled = false

        binding.homeTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.homeTab.tabRippleColor = null
//        setAppbarPercent()

        TabLayoutMediator(binding.homeTab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }
        binding.homeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTab(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.recommendContent.ivMore.setOnClickListener {
            showPublish(binding.recommendContent.ivMore)
        }
        binding.layoutTopBar.ivScan.setOnClickListener {
            showPublish(binding.layoutTopBar.ivScan)
        }

    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        var mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 15f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗
        }
    }

    var itemPunchWhat: Int = 0

    //初始化tab
    private fun initTab() {
        for (i in 0 until binding.homeTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_home, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)

            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 15f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
            //更改选中项样式
            //设置样式
            binding.homeTab.getTabAt(i)?.customView = view
        }
    }

    override fun onResume() {
        super.onResume()
        setAppbarPercent()
    }

    private fun showPublish(publishLocationView : ImageView) {
        val location = IntArray(2)
        var height = DisplayUtil.getDpi(requireContext())
        publishLocationView.getLocationOnScreen(location)
        height -= location[1]
        val publishView = layoutInflater.inflate(R.layout.popup_home_publish, null)
        val publishPopup = PublishPopup(
            requireContext(),
            publishView,
            Constraints.LayoutParams.WRAP_CONTENT,
            Constraints.LayoutParams.WRAP_CONTENT,
            object :ICallback{
                override fun onResult(result: ResultData) {
                }
            }
        )
        publishPopup.contentView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
        publishPopup.showAsDropDown(publishLocationView)
    }
    private fun setAppbarPercent() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            "verticalOffset=$verticalOffset".logE()
//            if (verticalOffset <= -50) {
//                binding.layoutTopBar.conContent.visibility = View.VISIBLE
//            } else if (verticalOffset >= -600) {
//                binding.layoutTopBar.conContent.visibility = View.GONE
//            }
            val percent: Float = -verticalOffset / appBarLayout.totalScrollRange.toFloat()//滑动比例
            "percent=$percent".logE()
            if (percent > 0.8) {
                binding.layoutTopBar.conContent.visibility = View.VISIBLE
//                    val alpha = 1 - (1 - percent) * 5 //渐变变换
//                    binding.layoutTopBar.conContent.alpha = alpha
                "conContent=visiable".logE()

            } else {
                binding.layoutTopBar.conContent.visibility = View.GONE
                "conContent=gone".logE()

            }


        })

    }

    override fun initData() {

    }


}