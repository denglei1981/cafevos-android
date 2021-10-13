package com.changanford.circle

import com.alibaba.fastjson.JSON
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
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.utilext.logD

/**
 * 社区
 */
class CircleFragment : BaseFragment<FragmentCircleBinding, CircleViewModel>() {
    private var postEntity: ArrayList<PostEntity>? = null//草稿

    private val circleAdapter by lazy {
        CircleMainAdapter(requireContext(), childFragmentManager)
    }

    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.rlTitle, requireActivity())
//        MUtils.scrollStopLoadImage(binding.ryCircle)
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
        binding.refreshLayout.setOnRefreshListener {
//            circleAdapter.topBinding.tvCircleMore.text="asd"
//            circleAdapter.circleAdapter.setItems(arrayListOf("",""))
//            circleAdapter.circleAdapter.notifyDataSetChanged()
            viewModel.communityIndex()
            it.finishRefresh()
        }
    }

    override fun initData() {
        val list = arrayListOf("", "")
        circleAdapter.setItems(list)
        binding.ryCircle.adapter = circleAdapter
    }
}