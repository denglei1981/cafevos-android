package com.changanford.circle

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.changanford.circle.adapter.CircleMainAdapter
import com.changanford.circle.databinding.FragmentCircleBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.getPermissionLauncher
import com.changanford.common.utilext.toast
import razerdp.basepopup.BasePopupWindow

/**
 * 社区
 */
class CircleFragment : BaseFragment<FragmentCircleBinding, EmptyViewModel>() {

    private val circleAdapter by lazy {
        CircleMainAdapter(requireContext(), childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.rlTitle, requireActivity())
        MUtils.scrollStopLoadImage(binding.ryCircle)
        binding.ivMenu.setOnClickListener {
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
        }
    }

    override fun initData() {
        val list = arrayListOf("", "")
        circleAdapter.setItems(list)
        binding.ryCircle.adapter = circleAdapter
    }
}