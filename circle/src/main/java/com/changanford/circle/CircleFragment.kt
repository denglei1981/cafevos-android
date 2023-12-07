package com.changanford.circle

import android.Manifest
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.changanford.circle.adapter.CircleMainAdapter
import com.changanford.circle.databinding.FragmentCircleBinding
import com.changanford.circle.viewmodel.CircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.BindDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.location.LocationUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterHomePath.SplashActivity
import com.changanford.common.ui.dialog.PostDialog
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.wutil.WCommonUtil
import com.qw.soul.permission.bean.Permissions


/**
 * 社区
 */
@Deprecated("被v2代替了")
class CircleFragment : BaseFragment<FragmentCircleBinding, CircleViewModel>() {

    private var postEntity: ArrayList<PostEntity>? = null//草稿
    private val circleAdapter by lazy {
        CircleMainAdapter(requireContext(), childFragmentManager)
    }

    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (null != savedInstanceState) {
            ARouter.getInstance().build(SplashActivity)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation()
        }
    }

    override fun initView() {
        bus()
        AppUtils.setStatusBarMarginTop(binding.rlTitle, requireActivity())
        PostDatabase.getInstance(requireActivity()).getPostDao().findAll().observe(this,
            {
                postEntity = it as ArrayList<PostEntity>
            })
        binding.ivMenu.setOnClickListener {

            if (MConstant.token.isNotEmpty()) {
                if (!MineUtils.getBindMobileJumpDataType()) {
                    if (postEntity?.size == 0) {
                        showMenuPop()
                    } else {
                        activity?.let { it1 ->
                            PostDialog(
                                it1,
                                "发现您还有草稿未发布",
                                postButtonListener = object : PostDialog.PostButtonListener {
                                    override fun save() { //继续编辑 2 图片 3 视频 4 图文长帖
                                        val postEntity = postEntity?.last()
                                        when (postEntity?.type) {
                                            "2" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.PostActivity)
                                            }
                                            "3" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.VideoPostActivity)
                                            }
                                            "4" -> {
                                                RouterManger.param("postEntity", postEntity)
                                                    .startARouter(ARouterCirclePath.LongPostAvtivity)
                                            }
                                        }
                                    }

                                    override fun cancle() {  //不使用草稿
                                        showMenuPop()
                                    }


                                }).show()
                        }

                    }
                } else {
                    BindDialog(binding.ivMenu.context).show()
                }
            } else {
                startARouter(ARouterMyPath.SignUI)
            }

        }
        binding.ivSearch.setOnClickListener {
            JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_POST.toString())
        }
        binding.refreshLayout.setOnRefreshListener {
            initData()
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_BOTTOM_FRAGMENT).postValue(false)
        }
        initRecyclerData()
    }

    private fun showMenuPop() {
        CircleMainMenuPop(
            requireContext(),
            object : CircleMainMenuPop.CheckPostType {
                override fun checkLongBar() {
                    startARouter(ARouterCirclePath.LongPostAvtivity, true)
                }

                override fun checkPic() {
                    startARouter(ARouterCirclePath.PostActivity, true)
                }

                override fun checkVideo() {
                    startARouter(ARouterCirclePath.VideoPostActivity, true)
                }

                override fun checkQuestion() {
                    JumpUtils.instans?.jump(116)
                }

            }).run {
            setBlurBackgroundEnable(false)
            showPopupWindow(binding.ivMenu)
            initData()
        }
    }

    private fun initRecyclerData() {
        val list = arrayListOf("", "")
        circleAdapter.setItems(list)
        binding.ryCircle.adapter = circleAdapter
    }

    override fun initData() {
        binding.refreshLayout.post {
            val permissions = Permissions.build(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            val success = {
                if (JumpUtils.instans?.isOPen(requireContext()) == true) {
                    getLocationData()
                } else {
                    viewModel.communityIndex()
                }
            }
            val fail = {
                viewModel.communityIndex()
            }
            PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//            SoulPermission.getInstance()
//                .checkAndRequestPermission(
//                    Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
//                    object : CheckRequestPermissionListener {
//                        override fun onPermissionOk(permission: Permission) {
//                            if (JumpUtils.instans?.isOPen(requireContext()) == true) {
//                                getLocationData()
//                            } else {
//                                viewModel.communityIndex()
//                            }
//                        }
//
//                        override fun onPermissionDenied(permission: Permission) {
//                            viewModel.communityIndex()
//                        }
//                    })
        }
    }

    private fun getLocationData() {
        LocationUtils.circleLocation(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation) {
                val latitude = location.latitude //获取纬度信息
                val longitude = location.longitude //获取经度信息
                viewModel.communityIndex(longitude, latitude)
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

//                topFragments.forEachIndexed { index, circleMainFragment ->
//                    when (index) {
//                        0 -> {
//                            circleMainFragment.setData(it.regionCircles?.circleInfos)
//                        }
//                        1 -> {
//                            circleMainFragment.setData(it.interestCircles?.circleInfos)
//                        }
//
//                    }
//                }
            }
            binding.refreshLayout.finishRefresh()
        })
    }

    private fun bus() {
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).observe(this, {
            binding.refreshLayout.finishRefresh()
        })
    }
}