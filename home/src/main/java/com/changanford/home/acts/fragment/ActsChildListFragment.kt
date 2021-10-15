package com.changanford.home.acts.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.acts.dialog.HomeActsScreenDialog
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentActsChildBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter

/**
 *  子活动列表。
 * */
class ActsChildListFragment : BaseFragment<FragmentActsChildBinding, ActsListViewModel>() {

    val searchActsResultAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }

    //， 排序，活动状态  ，发布方,线上线下
    var shaixuanList =
        arrayListOf("OrderTypeEnum", "ActivityTimeStatus", "OfficialEnum", "WonderfulTypeEnum")
    var homeActsDialog: HomeActsScreenDialog? = null

    var cityId: String = ""
    var cityName: String = ""
    var officialCode: Int = -1
    var wonderfulType: Int = -1
    companion object {
        fun newInstance(): ActsChildListFragment {
            val fg = ActsChildListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchActsResultAdapter

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

    override fun initData() {
        viewModel.getActList()
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

        viewModel.guanfang.observe(this, androidx.lifecycle.Observer {
            // 记录官方渠道
            officialEnum = it
        })
        viewModel.xianshang.observe(this, androidx.lifecycle.Observer {
            xianshangEnum = it
        })

    }

    fun getActList(isLoadMore: Boolean,
                   orderType: String = "",//排序 综合排序  COMPREHENSIVE HOT,New
                   activityTimeStatus: String = "") // 过期，还是进行中。ON_GOING CLOSED
    {
        viewModel.getActList(
            isLoadMore,
            cityId = cityId,
            cityName = cityName,
            wonderfulType = wonderfulType,
            official = officialCode,
            orderType = orderType,
            activityTimeStatus = activityTimeStatus
        )
    }


     fun show(){
        if (homeActsDialog == null) {
            homeActsDialog = HomeActsScreenDialog(requireActivity(), this, object : ICallback {
                override fun onResult(result: ResultData) {
                    if (result.resultCode == ResultData.OK) {
                        val screenData = result.data as ScreenData
                        cityId = screenData.cityId
                        cityName = screenData.cityName
                        if (!TextUtils.isEmpty(screenData.official)) {
                            officialCode = screenData.official.toInt()
                        }
                        if (!TextUtils.isEmpty(screenData.wonderfulType)) {
                            wonderfulType = screenData.wonderfulType.toInt()
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
}










