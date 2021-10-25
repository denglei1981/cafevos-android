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
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.location.LocationUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListFragment : BaseFragment<FragmentCircleListBinding, CircleListViewModel>() {

    companion object {
        fun newInstance(type: Int): CircleListFragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var type = 0
    private var page = 1

    private val adapter by lazy {
        CircleListAdapter()
    }

    override fun initView() {

        arguments?.getInt("type", 0)?.let {
            type = it
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

    override fun initData() {
        if (type == 1) {
            binding.refreshLayout.post {
                SoulPermission.getInstance()
                    .checkAndRequestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                        object : CheckRequestPermissionListener {
                            override fun onPermissionOk(permission: Permission) {
                                LocationUtils.circleLocation(object : BDAbstractLocationListener() {
                                    override fun onReceiveLocation(location: BDLocation) {
                                        val latitude = location.latitude //获取纬度信息
                                        val longitude = location.longitude //获取经度信息
                                        viewModel.getData(
                                            type,
                                            longitude.toString(),
                                            latitude.toString(),
                                            page
                                        )
                                    }
                                })
                            }

                            override fun onPermissionDenied(permission: Permission) {
                                viewModel.getData(
                                    type,
                                    "",
                                    "",
                                    page
                                )
                            }
                        })
            }

        } else {
            viewModel.getData(
                type,
                "",
                "",
                page
            )
        }

    }

    override fun observe() {
        super.observe()
        viewModel.circleListBean.observe(this, {
            if (page == 1) {
                if (it.dataList.size == 0) {
                    adapter.setEmptyView(R.layout.circle_empty_layout)
                }
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        })
    }
}