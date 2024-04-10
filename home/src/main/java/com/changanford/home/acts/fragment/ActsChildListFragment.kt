package com.changanford.home.acts.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.adapter.SearchActsResultAdapter
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toastShow
import com.changanford.home.PageConstant
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.acts.dialog.HomeActsScreenDialog
import com.changanford.home.acts.dialog.UnitActsPop
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentActsChildBinding
import com.changanford.home.databinding.LayoutActsHomeHeaderBinding
import com.changanford.home.util.newTabLayout
import com.google.android.material.tabs.TabLayout
import com.zhpan.bannerview.constants.PageStyle
import razerdp.basepopup.BasePopupWindow

/**
 *  子活动列表。
 * */
class ActsChildListFragment : BaseLoadSirFragment<FragmentActsChildBinding, ActsListViewModel>() {

    private val searchActsResultAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }
    private lateinit var headBinding: LayoutActsHomeHeaderBinding
    private val headView by lazy {
        layoutInflater.inflate(com.changanford.home.R.layout.layout_acts_home_header, null)
    }

    private val params = arrayOf("综合排序", "全部活动")
    private var adBean = ArrayList<CircleHeadBean>()
    private var zonghescreens = MutableLiveData<List<EnumBean>>() //综合排序等
    private var screenstype = MutableLiveData<MutableList<EnumBean>>()  //进行中等
    private var guanfang = MutableLiveData<List<EnumBean>>()  //官方
    private var xianshang = MutableLiveData<List<EnumBean>>()  //线上线下

    var unitPop: UnitActsPop? = null // 综合排序
    var allActsPop: UnitActsPop? = null // 全部活动。

    //， 排序，活动状态  ，发布方,线上线下
    var shaixuanList =
        arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")
    private var homeActsDialog: HomeActsScreenDialog? = null

    var cityId: String = ""
    var cityName: String = ""
    var officialCode: Int = -1
    var wonderfulType: Int = -1


    var allActsCode: String = ""// 进行中
    var allUnitCode: String = ""// 综合排序code

    companion object {
        fun newInstance(): ActsChildListFragment {
            val fg = ActsChildListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        headBinding = DataBindingUtil.bind(headView)!!
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        searchActsResultAdapter.addHeaderView(headView)
        binding.recyclerView.adapter = searchActsResultAdapter
        searchActsResultAdapter.loadMoreModule.setOnLoadMoreListener {
            getActList(
                true,
                allActsCode,
                allUnitCode
            )
        }
        searchActsResultAdapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.NewsVideoDetailActivity)
        }
        searchActsResultAdapter.setOnItemClickListener { adapter, view, position ->
            val item = searchActsResultAdapter.getItem(position)
            GIOUtils.homePageClick("活动信息流", (position + 1).toString(), item.title)
            //                CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
            JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
            //                if (item.jumpType == 2||item.jumpType==1) {
            if (item.outChain == "YES") {
                viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
            }
        }
        searchActsResultAdapter.sSetLogHistory {
            viewModel.AddACTbrid(it)
        }
        initViewPager()
        setIndicator()

        params.forEach {
            val view =
                headBinding.tabs.newTabLayout(com.changanford.home.R.layout.tab_acts_title, false)
            TabViewHolder(view).textView.text = it
        }
        headBinding.tvSrceen.setOnClickListener {
            show()
            GIOUtils.homePageClick("筛选区", 3.toString(), "筛选")
        }

        headBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                val title =
                    tab?.customView?.findViewById<TextView>(com.changanford.home.R.id.text_view)?.text
                when (tab?.position) {
                    0 -> {
                        if (unitPop != null && unitPop!!.isShowing) {
                            unitPop!!.dismiss()
                        } else {
                            getEnum(shaixuanList[0])
                            GIOUtils.homePageClick("筛选区", 1.toString(), title.toString())
                        }
                    }

                    1 -> {
                        if (allActsPop != null && allActsPop!!.isShowing) {
                            allActsPop!!.dismiss()
                        } else {
                            getEnum(shaixuanList[1])
                            GIOUtils.homePageClick("筛选区", 2.toString(), title.toString())
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val title =
                    tab?.customView?.findViewById<TextView>(com.changanford.home.R.id.text_view)?.text
                when (tab?.position) {
                    0 -> {
                        getEnum(shaixuanList[0])
                        GIOUtils.homePageClick("筛选区", 1.toString(), title.toString())
                    }

                    1 -> {
                        getEnum(shaixuanList[1])
                        GIOUtils.homePageClick("筛选区", 2.toString(), title.toString())
                    }
                }
            }
        })
    }

    override fun initData() {
        viewModel.getActList()
        viewModel.getEnum(shaixuanList[2])
        viewModel.getEnum(shaixuanList[3])
    }

    private var officialEnum: List<EnumBean>? = null
    private var xianshangEnum: List<EnumBean>? = null

    private fun showEmptys() {
        binding.llEmpty.visibility = View.VISIBLE
    }

    private fun hideEmptys() {
        binding.llEmpty.visibility = View.GONE
    }

    override fun observe() {
        super.observe()
        viewModel.bannerLiveData.observe(this) {
            if (it.isSuccess) {
//                (parentFragment as HomeV2Fragment).stopRefresh()
                setViewPagerData(it.data as ArrayList<CircleHeadBean>)
            } else {
                toastShow(it.message)
            }
        }
        viewModel.actsLiveData.safeObserve(this) {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    searchActsResultAdapter.addData(it.data.dataList)
                } else {
                    if (it.data.dataList.size == 0) {
                        showEmptys()
                    } else {
                        hideEmptys()
                        searchActsResultAdapter.setNewInstance(it.data.dataList)
                    }
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    //如果不够一页,显示没有更多数据布局
                    searchActsResultAdapter.loadMoreModule.loadMoreEnd()
                } else {
                    searchActsResultAdapter.loadMoreModule.loadMoreComplete();
                }
            } else {
                toastShow(it.message)
            }
        }

        viewModel.guanfang.safeObserve(this, androidx.lifecycle.Observer {
            // 记录官方渠道
            officialEnum = it
        })
        viewModel.xianshang.safeObserve(this, androidx.lifecycle.Observer {
            xianshangEnum = it
        })

    }

    fun getActList(
        isLoadMore: Boolean,
        orderType: String = "",//排序 综合排序  COMPREHENSIVE HOT,New
        activityTimeStatus: String = ""
    ) // 过期，还是进行中。ON_GOING CLOSED
    {
        this.allActsCode = orderType
        this.allUnitCode = activityTimeStatus
        viewModel.getActList(
            isLoadMore,
            cityId = cityId,
            cityName = cityName,
            wonderfulType = wonderfulType,
            official = officialCode,
            orderType = allActsCode,
            activityTimeStatus = allUnitCode
        )
    }

    fun initMyView() {
        viewModel.getBanner()
        getActList(false)
    }

    fun show() {
        if (homeActsDialog == null) {
            homeActsDialog = HomeActsScreenDialog(requireActivity(), this, object : ICallback {
                override fun onResult(result: ResultData) {
                    if (result.resultCode == ResultData.OK) {
                        val screenData = result.data as ScreenData
                        cityId = screenData.cityId
                        cityName = screenData.cityName
                        officialCode = if (!TextUtils.isEmpty(screenData.official)) {
                            screenData.official.toInt()
                        } else {
                            -1
                        }
                        wonderfulType = if (!TextUtils.isEmpty(screenData.wonderfulType)) {
                            screenData.wonderfulType.toInt()
                        } else {
                            -1
                        }
                        getActList(false)
                    }
                }
            })
        }
        xianshangEnum?.let { it1 -> homeActsDialog?.setActsTypeDatta(it1) }
        officialEnum?.let { it1 -> homeActsDialog?.setOfficalData(it1) }
        homeActsDialog?.showPopupWindow()
    }

    private fun initViewPager() {
        headBinding.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            registerLifecycleObserver(lifecycle)
            setIndicatorView(headBinding.drIndicator)
            setRoundCorner(20).setPageStyle(PageStyle.MULTI_PAGE_SCALE)
//            setOnPageClickListener { }
            setIndicatorSliderColor(
                ContextCompat.getColor(context, com.changanford.home.R.color.blue_tab),
                ContextCompat.getColor(context, com.changanford.home.R.color.colorPrimary)
            )

            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (GioPageConstant.mainSecondPageName() == "发现页-活动") {
                        val bean = data as List<CircleHeadBean>
                        val item = bean[position]
                        bean[position].adName?.let { it1 ->
                            GIOUtils.homePageExposure(
                                "广告位banner", (position + 1).toString(),
                                it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                            )
                        }
                    }
                }
            })
        }.create()


    }

    private fun setIndicator() {
        val dp6 =
            requireContext().resources.getDimensionPixelOffset(com.changanford.home.R.dimen.dp_6)
        headBinding.drIndicator.setIndicatorDrawable(
            com.changanford.home.R.drawable.shape_home_banner_normal,
            com.changanford.home.R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                requireContext().resources.getDimensionPixelOffset(com.changanford.home.R.dimen.dp_20),
                dp6
            )
            .setIndicatorGap(requireContext().resources.getDimensionPixelOffset(com.changanford.home.R.dimen.dp_5))
    }

    private fun getEnum(className: String) {
        launchWithCatch {
            val body = HashMap<String, Any>()
            body["className"] = className
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getEnum(body.header(rkey), body.body(rkey))
                .onSuccess {
                    when (className) {
                        shaixuanList[0] -> {
                            zonghescreens.value = it
                            setUnitPopu(it as MutableList<EnumBean>)
                        }

                        shaixuanList[1] -> {
                            screenstype.value = it as? MutableList<EnumBean>
                            screenstype.value?.add(0, EnumBean("", "全部活动"))
                            setAllActsPopu(it as MutableList<EnumBean>)
                        }

                        shaixuanList[2] -> {
                            guanfang.value = it
                        }

                        shaixuanList[3] -> {
                            xianshang.value = it
                        }
                    }
                }.onWithMsgFailure {
                    toastShow(it!!)
                }.onFailure {

                }
        }
    }

    private class TabViewHolder(view: View) {
        init {
            view.tag = this
        }

        val textView: TextView = view.findViewById(com.changanford.home.R.id.text_view)
        val img: ImageView = view.findViewById(com.changanford.home.R.id.img)
    }

    private fun startViewPagerLoop() {
        headBinding.bViewpager.startLoop()
    }

    private fun stopViewPagerLoop() {
        headBinding.bViewpager.stopLoop()
    }

    private fun setUnitPopu(list: MutableList<EnumBean>) {
        if (unitPop == null) {
            unitPop = UnitActsPop(requireContext(),
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        val allEnum = result.data as? EnumBean
                        headBinding.tabs.getTabAt(0)
                            ?.customView?.findViewById<TextView>(com.changanford.home.R.id.text_view)
                            ?.text = allEnum?.message
                        allUnitCode = allEnum?.code.toString()
                        getActList(
                            false,
                            orderType = allUnitCode,
                            activityTimeStatus = allActsCode
                        )
                        unitPop?.setSelectedPosition(unitPop?.getitemPosition(allEnum!!) ?: 0)
                    }
                })
        }
        unitPop?.updateData(list)
        unitPop?.showPopupWindow(headBinding.tabs)
        unitPop?.setAlignBackground(true)
        unitPop?.setOnDismissListener(object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
                headBinding.tabs.getTabAt(0)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)
                    ?.setImageResource(com.changanford.home.R.mipmap.icon_act_shearch)
                headBinding.tabs.getTabAt(0)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)?.rotation =
                    0f

            }
        })
        unitPop?.setOnPopupWindowShowListener {
            headBinding.tabs.getTabAt(0)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)
                ?.setImageResource(com.changanford.home.R.mipmap.icon_act_shearch_blue)
        }
        unitPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }

    private fun setAllActsPopu(list: MutableList<EnumBean>) {
        if (allActsPop == null) {
            allActsPop = UnitActsPop(requireContext(),
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        if (result.resultCode == ResultData.OK) {
                            val allEnum = result.data as? EnumBean
                            headBinding.tabs.getTabAt(1)
                                ?.customView?.findViewById<TextView>(com.changanford.home.R.id.text_view)
                                ?.text = allEnum?.message
                            allActsCode = allEnum?.code.toString()
                            getActList(
                                false,
                                orderType = allUnitCode,
                                activityTimeStatus = allActsCode
                            )
                            allActsPop?.setSelectedPosition(
                                allActsPop?.getitemPosition(allEnum!!) ?: 0
                            )
                        }
                    }
                })
        }
        allActsPop?.updateData(list)
        allActsPop?.showPopupWindow(headBinding.tabs)
        allActsPop?.setAlignBackground(true)
        allActsPop?.onDismissListener = object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
                headBinding.tabs.getTabAt(1)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)
                    ?.setImageResource(com.changanford.home.R.mipmap.icon_act_shearch)
                headBinding.tabs.getTabAt(1)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)?.rotation =
                    0f
            }
        }
        allActsPop?.setOnPopupWindowShowListener {
            headBinding.tabs.getTabAt(1)?.customView?.findViewById<ImageView>(com.changanford.home.R.id.img)
                ?.setImageResource(com.changanford.home.R.mipmap.icon_act_shearch_blue)
        }
        allActsPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }

    fun setViewPagerData(list: ArrayList<CircleHeadBean>) {
        this.adBean = list
        headBinding.bViewpager.refreshData(list)
        if (list.isNotEmpty()) {
            val item = list[0]
            list[0].adName?.let { it1 ->
                GIOUtils.homePageExposure(
                    "广告位banner", 1.toString(),
                    it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                )
            }
        }

//        notifyItemChanged(0, 0)
    }

    override fun onResume() {
        super.onResume()
        try {
            startViewPagerLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stopViewPagerLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            stopViewPagerLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRetryBtnClick() {

    }
}










