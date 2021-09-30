package com.changanford.home.acts.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.home.R
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.acts.dialog.HomeActsScreenDialog
import com.changanford.home.acts.dialog.UnitActsPop
import com.changanford.home.callback.ICallback
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentActsListBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
import com.changanford.home.search.data.SearchData
import com.zhpan.bannerview.BannerViewPager
import razerdp.basepopup.BasePopupWindow
import java.util.*

/**
 *  活动列表
 * */
class ActsListFragment : BaseFragment<FragmentActsListBinding, EmptyViewModel>() {

    var shopLists = mutableListOf<SearchData>()
    val searchActsResultAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter(mutableListOf())
    }
    var mPictureList: MutableList<String> = ArrayList() // 图片存储位置

    companion object {
        fun newInstance(): ActsListFragment {
            val fg = ActsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.homeCrv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        binding.homeCrv.adapter = searchActsResultAdapter
        searchActsResultAdapter.addData(shopLists)
        initViewPager()
        setIndicator()
        searchActsResultAdapter.setOnItemClickListener { adapter, view, position ->

            startARouter(ARouterHomePath.NewsVideoDetailActivity)

        }
    }

    var homeActsDialog: HomeActsScreenDialog? = null
    var unitActsPop: UnitActsPop? = null

    override fun initData() {
        binding.layoutHomeScreen.tvSrceen.setOnClickListener {
            if (homeActsDialog == null) {
                homeActsDialog = HomeActsScreenDialog(requireActivity(), object : ICallback {
                    override fun onResult(result: ResultData) {

                    }
                })
            }
            homeActsDialog?.show()
        }
        binding.layoutHomeScreen.tvAllActs.setOnClickListener {
            setPopu(it)
        }
        binding.layoutHomeScreen.tvDesc.setOnClickListener {

            setPopu(it)
        }


    }

    fun setPopu(view: View) {
        if (unitActsPop == null) {
            unitActsPop = UnitActsPop(this,
                object : ICallback {
                    override fun onResult(result: ResultData) {

                    }
                })
        }
        unitActsPop?.showPopupWindow(view)
        unitActsPop?.setAlignBackground(true)
        unitActsPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }


    private fun initViewPager() {
        binding.layoutViewpager.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            setIndicatorView(binding.layoutViewpager.drIndicator)
            setRoundCorner(20)
            setOnPageClickListener(object : BannerViewPager.OnPageClickListener {
                override fun onPageClick(position: Int) {
                }
            })
            setIndicatorSliderColor(
                ContextCompat.getColor(context, R.color.blue_tab),
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setIndicatorView(binding.layoutViewpager.drIndicator)
        }.create(getPicList(4))
    }

    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.layoutViewpager.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
            .setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    private fun getPicList(count: Int): MutableList<String> {
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        return mPictureList
    }

}