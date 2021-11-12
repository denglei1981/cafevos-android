package com.changanford.evos

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterFinish
import com.changanford.common.util.*
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.evos.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule
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
        MConstant.isPopAgreement = SPUtils.getParam(requireContext(), "isPopAgreement", true) as Boolean
    }

    override fun initData() {
        GifLoadOneTimeGif.loadOneTimeGif(requireContext(),R.drawable.splashgif,binding.splashimg,1,object :GifLoadOneTimeGif.GifListener{
            override fun gifPlayComplete() {
            }
        })
        lifecycleScope.launch {
            delay(4000)
            viewModel.getKey()
            viewModel.key.observe(this@SplashFragment) {
                MConstant.pubKey = it
                if (MConstant.isPopAgreement) {
                    showAppPrivacy(BaseApplication.curActivity as AppCompatActivity) {
                        viewModel.getDbAds()
                        viewModel.adService("app_launch")
                    }
                } else {
                    viewModel.getDbAds()
                    viewModel.adService("app_launch")
                }
            }
            showAds()
        }

    }

    /**
     * 处理首次登录
     */
    private fun firstIn(){
        var oldVersionCode = SPUtils.getParam(requireContext(),"versionCode",0) as Int
        var curVersionCode = DeviceUtils.getVersionCode(requireContext())
        if (SPUtils.getParam(requireContext(), "isfirstin", true) as Boolean || oldVersionCode < curVersionCode) {
            startARouterFinish(requireActivity(), ARouterHomePath.LandingActivity)
            SPUtils.setParam(requireContext(), "isfirstin", false)
            SPUtils.setParam(requireContext(),"versionCode",curVersionCode)
            return
        }
    }


    private fun showAds() {
        var bundle = requireActivity().intent?.extras?: Bundle()

        viewModel.imgBean.observe(this, {
            firstIn()
            val imgBean = it
            if (imgBean == null || imgBean.adImg.isNullOrEmpty() || !GlideUtils.handleImgUrl(imgBean.adImg)!!
                    .startsWith("http")
            ) {
                navFinishActivityTo(R.id.action_splashFragment_to_mainActivity,bundle)
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
                viewModel.setTime("5")
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
                    navFinishActivityTo(R.id.action_splashFragment_to_mainActivity,bundle)
                }
            }
            binding.counter.setOnClickListener {
                navFinishActivityTo(R.id.action_splashFragment_to_mainActivity,bundle)
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