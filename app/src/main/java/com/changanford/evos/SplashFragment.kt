package com.changanford.evos

import android.media.MediaPlayer
import android.os.Build
import android.os.SystemClock
import android.view.SurfaceHolder
import android.view.View
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterFinish
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.evos.databinding.FragmentSplashBinding
import kotlin.math.ceil

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    var player = MediaPlayer()


    override fun onDestroy() {
        super.onDestroy()
        if (player.isPlaying)
            player.stop()
        player.release()
    }
    override fun initView() {
    }

    override fun initData() {
        viewModel.getKey()
        viewModel.key.observe(this) {
            showCounter()
            MConstant.pubKey = it
            viewModel.getDbAds()
            viewModel.adService("app_launch")
        }
        showAds()
    }

    /**
     * 处理首次登录
     */
    private fun firstIn(){
        if (SPUtils.getParam(requireContext(), "isfirstin", true) as Boolean) {
            startARouterFinish(requireActivity(), ARouterHomePath.LandingActivity)
            SPUtils.setParam(requireContext(), "isfirstin", false)
            return
        }
    }

    private fun showCounter() {
        binding.chronometer.apply {
            base = SystemClock.elapsedRealtime() + 5 * 1000
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                isCountDown = true
            }
        }.start()
    }

    private fun showAds() {
        viewModel.imgBean.observe(this, {
            firstIn()
            var imgBean = it
            if (imgBean == null || imgBean.adImg.isNullOrEmpty() || !GlideUtils.handleImgUrl(imgBean.adImg)!!
                    .startsWith("http")
            ) {
                navFinishActivityTo(R.id.action_splashFragment_to_mainActivity)
                return@observe
            }
            if (imgBean.video == 1) {
                binding.splashVideo.visibility = View.VISIBLE
                binding.splashimg.visibility = View.GONE
                binding.splashVideo.holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                        player.setDisplay(holder)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                    }

                })
                player.setOnPreparedListener {
                    player.setDisplay(binding.splashVideo.holder)
                    player.start()
                }
                player.setDataSource(GlideUtils.handleImgUrl(imgBean.adImg))
                player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                player.prepareAsync()
                viewModel.setTime(imgBean.videoTime)
            } else {
                binding.splashimg.load(imgBean.adImg)
            }
            binding.chronometer.base = viewModel.getTime()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.chronometer.isCountDown = true
            }
            binding.chronometer.start()
            var tv = binding.counter
            binding.chronometer.setOnChronometerTickListener { it ->
                binding.counter.visibility = View.VISIBLE
                tv.text =
                    "跳过 ${ceil(((ceil((it.base - SystemClock.elapsedRealtime()).toDouble())) / 1000)).toInt()}"
                if (it.base <= SystemClock.elapsedRealtime()) {
                    tv.text = "跳过 ${0}"
                    viewModel.jump = true
                    it.stop()
                    navFinishActivityTo(R.id.action_splashFragment_to_mainActivity)
                }
            }
            binding.counter.setOnClickListener {
                navFinishActivityTo(R.id.action_splashFragment_to_mainActivity)
            }
            binding.splashimg.setOnClickListener { _ ->
                JumpUtils.instans!!.jump(imgBean.jumpDataType, imgBean.jumpDataValue)
            }
            binding.splashVideo.setOnTouchListener() { _, _ ->
                JumpUtils.instans!!.jump(imgBean.jumpDataType, imgBean.jumpDataValue)
                true
            }
        })
    }
}