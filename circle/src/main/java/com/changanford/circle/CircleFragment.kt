package com.changanford.circle

import android.Manifest
import cn.hchstudio.kpermissions.KPermission
import com.alibaba.fastjson.JSON
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.changanford.circle.adapter.CircleMainAdapter
import com.changanford.circle.databinding.FragmentCircleBinding
import com.changanford.circle.viewmodel.CircleViewModel
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.common.basic.BaseFragment
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.location.LocationUtils
import com.changanford.common.utilext.logD
import com.xiaomi.push.it

/**
 * 社区
 */
class CircleFragment : BaseFragment<FragmentCircleBinding, CircleViewModel>() {

    private var postEntity: ArrayList<PostEntity>? = null//草稿

    private val permissionsGroup =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private val circleAdapter by lazy {
        CircleMainAdapter(requireContext(), childFragmentManager)
    }

    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.rlTitle, requireActivity())
        PostDatabase.getInstance(requireActivity()).getPostDao().findAll().observe(this,
            {
                postEntity = it as ArrayList<PostEntity>
            })
        binding.ivMenu.setOnClickListener {
            if (postEntity?.size==0){

                CircleMainMenuPop(requireContext(), object : CircleMainMenuPop.CheckPostType {
                    override fun checkLongBar() {
                        startARouter(ARouterCirclePath.LongPostAvtivity)
                    }

                    override fun checkPic() {
                        startARouter(ARouterCirclePath.PostActivity)
                    }

                    override fun checkVideo() {
                        startARouter(ARouterCirclePath.VideoPostActivity)
                    }

                }).run {
                    setBlurBackgroundEnable(false)
                    showPopupWindow(it)
                    initData()
                }
            } else {
                JSON.toJSONString(postEntity).logD()
                AlertDialog(activity).builder().setGone().setMsg("发现您有草稿还未发布")
                    .setNegativeButton("继续编辑") {
                        startARouter(ARouterMyPath.MyPostDraftUI)
                    }.setPositiveButton("不使用草稿") {
                        CircleMainMenuPop(
                            requireContext(),
                            object : CircleMainMenuPop.CheckPostType {
                                override fun checkLongBar() {
                                    startARouter(ARouterCirclePath.LongPostAvtivity)
                                }

                                override fun checkPic() {
                                    startARouter(ARouterCirclePath.PostActivity)
                                }

                                override fun checkVideo() {
                                    startARouter(ARouterCirclePath.VideoPostActivity)
                                }

                            }).run {
                            setBlurBackgroundEnable(false)
                            showPopupWindow(binding.ivMenu)
                            initData()
                        }
                    }.show()
            }
        }
        binding.ivSearch.setOnClickListener {

        }
        binding.refreshLayout.setOnRefreshListener {
            initData()
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_BOTTOM_FRAGMENT).postValue(false)
        }
        initRecyclerData()
    }

    private fun initRecyclerData() {
        val list = arrayListOf("", "")
        circleAdapter.setItems(list)
        binding.ryCircle.adapter = circleAdapter
    }

    override fun initData() {
        KPermission(requireActivity()).requestPermission(permissionsGroup, {
            if (it) {
                LocationUtils.circleLocation(object : BDAbstractLocationListener() {
                    override fun onReceiveLocation(location: BDLocation) {
                        val latitude = location.latitude //获取纬度信息
                        val longitude = location.longitude //获取经度信息
                        viewModel.communityIndex(longitude, latitude)
                    }
                })
            } else {
                viewModel.communityIndex()
            }
        })

    }

    override fun observe() {
        super.observe()
        viewModel.circleBean.observe(this, {
            circleAdapter.run {
                allCircleAdapter.setItems(it.allCircles)
                allCircleAdapter.notifyDataSetChanged()
                circleAdapter.topicAdapter.setItems(it.topics)
                topicAdapter.notifyDataSetChanged()

                topFragments.forEachIndexed { index, circleMainFragment ->
                    when (index) {
                        0 -> {
                            circleMainFragment.setData(it.regionCircles.circleInfos)
                        }
                        1 -> {
                            circleMainFragment.setData(it.interestCircles.circleInfos)
                        }

                    }
                }
            }
            binding.refreshLayout.finishRefresh()
        })
    }
}