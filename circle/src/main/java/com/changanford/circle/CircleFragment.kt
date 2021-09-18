package com.changanford.circle

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.changanford.circle.databinding.FragmentSecondBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.ShareBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.viewpager.Banner
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.getPermissionLauncher
import com.changanford.common.util.hasPermission
import com.changanford.common.util.work.BuriedWorker
import com.changanford.common.util.work.doOneWork
import com.changanford.common.utilext.toast
import com.changanford.common.web.ShareViewModule
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CircleFragment : BaseFragment<FragmentSecondBinding, EmptyViewModel>() {

    private lateinit var cameraPermission: ActivityResultLauncher<String>

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.permission.setOnClickListener {
            if (!hasPermission(requireActivity(), Manifest.permission.CAMERA) {
                    when (it < 0) {
                        true -> "应该跳系统".toast()
                        else -> "用户虽然禁止了，但是还可以弹出".toast()
                    }
                }) {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
        cameraPermission = getPermissionLauncher(this) {
            when (it) {
                true -> {
                    "用户授权了".toast()
                }
                false -> {
                    "用户禁止了".toast()
                }
            }
        }
        binding.viewpager.setOnClickListener {
            //埋点
            lifecycleScope.launch {
                doOneWork<BuriedWorker>()
            }
            val intent = Intent(activity, Banner::class.java)
            startActivity(intent)
        }
        binding.permission.setOnClickListener {
            startARouter(ARouterCirclePath.PostGraphicActivity)
        }
        binding.btnVideo.setOnClickListener {
            startARouter(ARouterCirclePath.PostVideoDetailsActivity)
        }
        binding.btnCircleList.setOnClickListener {
            startARouter(ARouterCirclePath.CircleListActivity)
        }
    }


    override fun onDestroyView() {
        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).postValue(false)
        super.onDestroyView()
    }

    override fun initView() {
        binding.scan.setOnClickListener {
            JumpUtils.instans?.jump(61, "")
        }
        binding.share.setOnClickListener {
            val shareViewModel = createViewModel(ShareViewModule::class.java)
            var shareBean = ShareBean(
                "https://www.baidu.com",
                "https://pics6.baidu.com/feed/728da9773912b31b488e11528b942d73dbb4e105.jpeg?token=d006ff7e58f523c108ce2c3af650d9aa",
                "分享",
                "分享的内容",
                "1",
                "2",
                "0"
            )
            shareViewModel.share(requireActivity(), shareBean)
        }
        binding.web.setOnClickListener {
            JumpUtils.instans?.jump(1, "https://www.baidu.com")
        }
    }

    override fun initData() {
    }
}