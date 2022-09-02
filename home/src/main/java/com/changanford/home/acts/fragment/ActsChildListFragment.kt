package com.changanford.home.acts.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.changanford.common.adapter.SearchActsResultAdapter
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.PageConstant
import com.changanford.home.acts.dialog.HomeActsScreenDialog
import com.changanford.home.acts.request.ActsListViewModel
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentActsChildBinding

/**
 *  子活动列表。
 * */
class ActsChildListFragment : BaseLoadSirFragment<FragmentActsChildBinding, ActsListViewModel>() {

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
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchActsResultAdapter
        searchActsResultAdapter.loadMoreModule.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                getActList(true, allActsCode, allUnitCode)
            }
        })
        searchActsResultAdapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.NewsVideoDetailActivity)
        }
        searchActsResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = searchActsResultAdapter.getItem(position)
//                CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
                JumpUtils.instans?.jump(item.jumpDto.jumpCode,item.jumpDto.jumpVal)
//                if (item.jumpType == 2||item.jumpType==1) {
                if (item.outChain == "YES") {
                    viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
                }
            }
        })
        searchActsResultAdapter.sSetLogHistory{
            viewModel.AddACTbrid(it)
        }
    }

    override fun initData() {
        viewModel.getActList()
        viewModel.getEnum(shaixuanList[2])
        viewModel.getEnum(shaixuanList[3])
    }

    var officialEnum: List<EnumBean>? = null
    var xianshangEnum: List<EnumBean>? = null

    fun showEmptys() {
        binding.llEmpty.visibility = View.VISIBLE
    }

    fun hideEmptys() {
        binding.llEmpty.visibility = View.GONE
    }

    override fun observe() {
        super.observe()
        viewModel.actsLiveData.safeObserve(this, androidx.lifecycle.Observer {
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
        })

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
            orderType = orderType,
            activityTimeStatus = activityTimeStatus
        )
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
        homeActsDialog?.show()
    }

    override fun onRetryBtnClick() {

    }
}










