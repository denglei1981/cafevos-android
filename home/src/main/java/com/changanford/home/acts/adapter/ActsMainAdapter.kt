package com.changanford.home.acts.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.common.net.*
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.acts.dialog.UnitActsPop
import com.changanford.home.acts.fragment.ActsChildListFragment
import com.changanford.home.api.HomeNetWork
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.HomeActsBottomBinding
import com.changanford.home.databinding.HomeActsHeaderBinding
import com.changanford.home.util.launchWithCatch
import com.changanford.home.util.newTabLayout
import com.google.android.material.tabs.TabLayout
import com.zhpan.bannerview.constants.PageStyle
import razerdp.basepopup.BasePopupWindow

class ActsMainAdapter(
    var context: Context,
    var lifecycleOwner: LifecycleOwner,
    var fragment: FragmentActivity
) : BaseAdapter<String>(
    context,
    Pair(R.layout.home_acts_header, 0),
    Pair(R.layout.home_acts_bottom, 1),
) {
    private val params = arrayOf("综合排序", "全部活动")

    //， 排序，活动状态  ，发布方,线上线下
    var shaixuanList =
        arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")
    var adBean = ArrayList<CircleHeadBean>()
    var zonghescreens = MutableLiveData<List<EnumBean>>() //综合排序等
    var screenstype = MutableLiveData<MutableList<EnumBean>>()  //进行中等
    var guanfang = MutableLiveData<List<EnumBean>>()  //官方
    var xianshang = MutableLiveData<List<EnumBean>>()  //线上线下

    var unitPop: UnitActsPop? = null // 综合排序
    var allActsPop: UnitActsPop? = null // 全部活动。

    var allActsCode: String = ""// 进行中
    var allUnitCode: String = ""// 综合排序code
    var headerBinding: HomeActsHeaderBinding? = null

    val actsChildFragment: ActsChildListFragment by lazy {
        ActsChildListFragment.newInstance()
    }

    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                headerBinding = vdBinding as HomeActsHeaderBinding
                initViewPager(headerBinding!!)
                setIndicator(headerBinding!!)
            }
            1 -> {
                val binding = vdBinding as HomeActsBottomBinding
                binding.viewPager.run {
                    adapter = object : FragmentStateAdapter(fragment) {
                        override fun getItemCount(): Int {
                            return 1
                        }

                        override fun createFragment(position: Int): Fragment {
                            return actsChildFragment
                        }
                    }
                    offscreenPageLimit = 1
                }
                params.forEach {
                    val view = binding.tabs.newTabLayout(R.layout.tab_acts_title, false)
                    TabViewHolder(view).textView.text = it
                }
                binding.tvSrceen.setOnClickListener {
                    actsChildFragment.show()
                }
                binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> {
                                if (unitPop != null && unitPop!!.isShowing) {
                                    unitPop!!.dismiss()
                                } else {
                                    getEnum(binding, shaixuanList[0])
                                }
                            }
                            1 -> {
                                if (allActsPop != null && allActsPop!!.isShowing) {
                                    allActsPop!!.dismiss()
                                } else {
                                    getEnum(binding, shaixuanList[1])
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> {
                                getEnum(binding, shaixuanList[0])
                            }
                            1 -> {
                                getEnum(binding, shaixuanList[1])
                            }
                        }
                    }
                })
            }
        }
    }

    private class TabViewHolder(view: View) {
        init {
            view.tag = this
        }

        val textView: TextView = view.findViewById(R.id.text_view)
        val img: ImageView = view.findViewById(R.id.img)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setViewPagerData(list: ArrayList<CircleHeadBean>) {
        this.adBean = list
        headerBinding?.bViewpager?.refreshData(list)
//        notifyItemChanged(0, 0)
    }

    private fun initViewPager(binding: HomeActsHeaderBinding) {
        binding.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            setIndicatorView(binding.drIndicator)
            setRoundCorner(20)
                .setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            setOnPageClickListener { }
            setIndicatorSliderColor(
                ContextCompat.getColor(context, R.color.blue_tab),
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
        }.create()


    }

    /**
     * 设置指示器
     * */
    private fun setIndicator(binding: HomeActsHeaderBinding) {
        val dp6 = context.resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                context.resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
            .setIndicatorGap(context.resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    /**
     *  查询活动枚举。
     * */
    fun getEnum(binding: HomeActsBottomBinding, className: String) {
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, Any>()
            body["className"] = className
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getEnum(body.header(rkey), body.body(rkey))
                .onSuccess {
                    when (className) {
                        shaixuanList[0] -> {
                            zonghescreens.value = it
                            setUnitPopu(binding, it as MutableList<EnumBean>)
                        }
                        shaixuanList[1] -> {
                            screenstype.value = it as? MutableList<EnumBean>
                            screenstype.value?.add(0, EnumBean("", "全部活动"))
                            setAllActsPopu(binding, it as MutableList<EnumBean>)
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

    fun setUnitPopu(binding: HomeActsBottomBinding, list: MutableList<EnumBean>) {
        if (unitPop == null) {
            unitPop = UnitActsPop(context,
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        val allEnum = result.data as? EnumBean
                        binding.tabs.getTabAt(0)
                            ?.customView?.findViewById<TextView>(R.id.text_view)
                            ?.text = allEnum?.message
                        allUnitCode = allEnum?.code.toString()
                        actsChildFragment.getActList(
                            false,
                            orderType = allUnitCode,
                            activityTimeStatus = allActsCode
                        )
                    }
                })
        }
        unitPop?.updateData(list)
        unitPop?.showPopupWindow(binding.tabs)
        unitPop?.setAlignBackground(true)
        unitPop?.setOnDismissListener(object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
                binding.tabs.getTabAt(0)?.customView?.findViewById<ImageView>(R.id.img)?.rotation =
                    0f
            }
        })
        unitPop?.setOnPopupWindowShowListener {
            binding.tabs.getTabAt(0)?.customView?.findViewById<ImageView>(R.id.img)?.rotation = 180f

        }
        unitPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }

    fun setAllActsPopu(binding: HomeActsBottomBinding, list: MutableList<EnumBean>) {
        if (allActsPop == null) {
            allActsPop = UnitActsPop(context,
                object : ICallback {
                    override fun onResult(result: ResultData) {
                        if (result.resultCode == ResultData.OK) {
                            val allEnum = result.data as? EnumBean
                            binding.tabs.getTabAt(1)
                                ?.customView?.findViewById<TextView>(R.id.text_view)
                                ?.text = allEnum?.message
                            allActsCode = allEnum?.code.toString()
                            actsChildFragment.getActList(
                                false,
                                orderType = allUnitCode,
                                activityTimeStatus = allActsCode
                            )
                        }
                    }
                })
        }
        allActsPop?.updateData(list)
        allActsPop?.showPopupWindow(binding.tabs)
        allActsPop?.setAlignBackground(true)
        allActsPop?.onDismissListener = object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
//                binding.layoutHomeScreen.ivDown.rotation = 0f
                binding.tabs.getTabAt(1)?.customView?.findViewById<ImageView>(R.id.img)?.rotation =
                    0f
            }
        }
        allActsPop?.setOnPopupWindowShowListener {
//            binding.layoutHomeScreen.ivDown.rotation = 180f
            binding.tabs.getTabAt(1)?.customView?.findViewById<ImageView>(R.id.img)?.rotation = 180f
        }
        allActsPop?.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.BOTTOM)
    }


}

