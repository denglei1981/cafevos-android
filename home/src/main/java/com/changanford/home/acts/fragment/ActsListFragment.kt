package com.changanford.home.acts.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.R
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.acts.dialog.HomeActsScreenDialog
import com.changanford.home.acts.dialog.UnitActsPop
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentActsListBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
import com.google.android.material.appbar.AppBarLayout
import com.zhpan.bannerview.BannerViewPager
import razerdp.basepopup.BasePopupWindow

/**
 *  活动列表
 * */
class ActsListFragment : BaseFragment<FragmentActsListBinding, ActsListViewModel>() {

    val searchActsResultAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }

    //， 排序，活动状态  ，发布方,线上线下
    var shaixuanList = arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")

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
        binding.homeCrv.adapter = searchActsResultAdapter
        initViewPager()
        setIndicator()
        searchActsResultAdapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.NewsVideoDetailActivity)
        }
        searchActsResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                when (searchActsResultAdapter.getItem(position).jumpType) {
                    1 -> {
                        JumpUtils.instans?.jump(
                            10000,
                            searchActsResultAdapter.getItem(position).jumpVal
                        )
                    }
                    2 -> {
                        JumpUtils.instans?.jump(
                            1,
                            searchActsResultAdapter.getItem(position).jumpVal
                        )
                        viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
                    }
                    3 -> {
                        JumpUtils.instans?.jump(
                            1,
                            searchActsResultAdapter.getItem(position).jumpVal
                        )
                    }
                }
            }
        })
    }

    var homeActsDialog: HomeActsScreenDialog? = null
    var unitPop: UnitActsPop? = null // 综合排序
    var allActsPop: UnitActsPop? = null // 全部活动。
    override fun initData() {
        binding.layoutHomeScreen.tvSrceen.setOnClickListener {
            if (homeActsDialog == null) {
                homeActsDialog = HomeActsScreenDialog(requireActivity(), this, object : ICallback {
                    override fun onResult(result: ResultData) {
                        if(result.resultCode==ResultData.OK){
                            var screenData = result.data as ScreenData
                            cityId=screenData.cityId
                            cityName=screenData.cityName
                            if(!TextUtils.isEmpty(screenData.official)){
                                officialCode= screenData.official.toInt()
                            }
                            if(!TextUtils.isEmpty(screenData.wonderfulType)){
                                wonderfulType=screenData.wonderfulType.toInt()
                            }
                            getActList(false)
                        }
                    }
                })
            }
            xianshangEnum?.let { it1 -> homeActsDialog?.setActsTypeDatta(it1) }
            officialEnum?.let { it1 -> homeActsDialog?.setOfficalData(it1) }
            homeActsDialog?.show()
        }
        binding.layoutHomeScreen.tvAllActs.setOnClickListener {
            viewModel.getEnum(shaixuanList[1])
        }
        binding.layoutHomeScreen.tvDesc.setOnClickListener { // 综合排序
            viewModel.getEnum(shaixuanList[0])
        }
        appBarState()
        viewModel.getActList()
        viewModel.getBanner()
        viewModel.getEnum(shaixuanList[2])
        viewModel.getEnum(shaixuanList[3])
    }
    var officialEnum: List<EnumBean>? = null
    var xianshangEnum: List<EnumBean>? = null
    override fun observe() {
        super.observe()
        viewModel.actsLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isSuccess) {
                searchActsResultAdapter.setNewInstance(it.data.dataList)
            } else {
                toastShow(it.message)
            }
        })
        viewModel.zonghescreens.observe(this, androidx.lifecycle.Observer {
            setUnitPopu(binding.layoutHomeScreen.tvSrceen, it as MutableList<EnumBean>)
        })
        viewModel.screenstype.observe(this, androidx.lifecycle.Observer {
            setAllActsPopu(binding.layoutHomeScreen.tvAllActs, it as MutableList<EnumBean>)
        })
        viewModel.guanfang.observe(this, androidx.lifecycle.Observer {
            // 记录官方渠道
            officialEnum = it
        })
        viewModel.xianshang.observe(this, androidx.lifecycle.Observer {
            xianshangEnum = it
        })
        viewModel.bannerLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isSuccess) {
                binding.layoutViewpager.bViewpager.create(it.data)
            } else {
                toastShow(it.message)
            }
        })
    }
    fun getActList(isLoadMore:Boolean){
        viewModel.getActList(isLoadMore,
            cityId=cityId,
            cityName=cityName,
            wonderfulType=wonderfulType,
            official=officialCode,
            orderType = allUnitCode,
            activityTimeStatus = allActsCode)
    }
    fun appBarState() {
        binding.homeAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percent: Float =
                Math.abs(verticalOffset * 1.0f) / appBarLayout.totalScrollRange //滑动比例
            if (percent > 0.8) {
                (parentFragment as HomeV2Fragment).exBand(false)
            } else {
                (parentFragment as HomeV2Fragment).exBand(true)
            }
        })
    }
    var allActsCode: String = ""// 进行中
    var allUnitCode: String=""// 综合排序code
    var cityId:String=""
    var cityName:String=""
    var officialCode:Int=-1
    var wonderfulType:Int=-1
    fun setUnitPopu(view: View, list: MutableList<EnumBean>) {
        if (unitPop == null) {
            unitPop = UnitActsPop(this,
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        val allEnum = result.data as? EnumBean
                        binding.layoutHomeScreen.tvDesc.text = allEnum?.message
                        allUnitCode = allEnum?.code.toString()
                        getActList(false)
                    }
                })
        }
        unitPop?.updateData(list)
        unitPop?.showPopupWindow(view)
        unitPop?.setAlignBackground(true)
        unitPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }
    fun setAllActsPopu(view: View, list: MutableList<EnumBean>) {
        if (allActsPop == null) {
            allActsPop = UnitActsPop(this,
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        if (result.resultCode == ResultData.OK) {
                            val allEnum = result.data  as? EnumBean
                            binding.layoutHomeScreen.tvAllActs.text = allEnum?.message
                            allActsCode = allEnum?.code.toString()
                            getActList(false)
                        }
                    }
                })
        }
        allActsPop?.updateData(list)
        allActsPop?.showPopupWindow(view)
        allActsPop?.setAlignBackground(true)
        allActsPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
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
        }.create()
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
    fun changeScreen() { // 改变了筛选参数。。
//        viewModel.getActList()

    }

}