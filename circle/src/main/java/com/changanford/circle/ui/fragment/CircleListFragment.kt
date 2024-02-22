package com.changanford.circle.ui.fragment

import android.Manifest
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleListAdapter
import com.changanford.circle.databinding.FragmentCircleListBinding
import com.changanford.circle.viewmodel.CircleListViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.location.LocationUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.qw.soul.permission.bean.Permissions

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListFragment : BaseFragment<FragmentCircleListBinding, CircleListViewModel>() {

    companion object {
        fun newInstance(type: Int, isRegion: String): CircleListFragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("isRegion", isRegion)
            val fragment = CircleListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var type = 0
    private var isRegion = "NO"
    private var page = 1

    private val adapter by lazy {
        CircleListAdapter(false)
    }

    override fun initView() {
        addLiveDataBus()
        arguments?.getInt("type", 0)?.let {
            type = it
        }

        arguments?.getString("isRegion")?.let {
            isRegion = it
        }

        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener { _, view, position ->
            val bundle = Bundle()
            bundle.putString("circleId", adapter.data[position].circleId)
            startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
        }

        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            initData()
        }
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
            it.finishRefresh()
        }
    }
    private fun addLiveDataBus(){
        //登录回调
        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when(it){
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS->{
                        page = 1
                        initData()
                    }
                    UserManger.UserLoginStatus.USER_LOGIN_OUT->{
                        page = 1
                        initData()
                    }
                    else -> {}
                }
            }
    }
    override fun initData() {
        if (isRegion == "YES") {//是地域圈子
            binding.refreshLayout.post {
                val permissions = Permissions.build(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
                val success = {
                    LocationUtils.circleLocation(object : BDAbstractLocationListener() {
                        override fun onReceiveLocation(location: BDLocation) {
                            val latitude = location.latitude //获取纬度信息
                            val longitude = location.longitude //获取经度信息
                            viewModel.getData(
                                type,
                                longitude.toString(),
                                latitude.toString(),
                                page,
                                isRegion
                            )
                        }
                    })
                }
                val fail = {
                    viewModel.getData(
                        type,
                        "",
                        "",
                        page,
                        isRegion
                    )
                }
                PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//                SoulPermission.getInstance()
//                    .checkAndRequestPermission(
//                        Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
//                        object : CheckRequestPermissionListener {
//                            override fun onPermissionOk(permission: Permission) {
//                                LocationUtils.circleLocation(object : BDAbstractLocationListener() {
//                                    override fun onReceiveLocation(location: BDLocation) {
//                                        val latitude = location.latitude //获取纬度信息
//                                        val longitude = location.longitude //获取经度信息
//                                        viewModel.getData(
//                                            type,
//                                            longitude.toString(),
//                                            latitude.toString(),
//                                            page,
//                                            isRegion
//                                        )
//                                    }
//                                })
//                            }
//
//                            override fun onPermissionDenied(permission: Permission) {
//                                viewModel.getData(
//                                    type,
//                                    "",
//                                    "",
//                                    page,
//                                    isRegion
//                                )
//                            }
//                        })
            }

        } else {
            viewModel.getData(
                type,
                "",
                "",
                page,
                isRegion
            )
        }

    }

    override fun observe() {
        super.observe()
        viewModel.circleListBean.observe(this) {
            if (page == 1) {
                if (it.dataList.size == 0) {
                    adapter.setEmptyView(R.layout.base_layout_empty_search)
                }
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}