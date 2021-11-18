package com.changanford.my.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.view.SurfaceHolder
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiBindMobileBinding
import com.changanford.my.viewmodel.SignViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *  文件名：BindMobileUI
 *  创建者: zcy
 *  创建日期：2021/9/17 10:21
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineBindMobileUI)
class BindMobileUI : BaseMineUI<UiBindMobileBinding, SignViewModel>() {

    var player = MediaPlayer()

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.rlTitle, this)
        var mobileText = binding.etLoginMobile.textChanges()
        var smsText = binding.etLoginSmsCode.textChanges()

        Observable.combineLatest(
            mobileText,
            smsText,
            BiFunction<CharSequence, CharSequence, Boolean> { t1, t2 ->
                t1.isNotEmpty() && t2.isNotEmpty()
            })
            .subscribe {
                binding.btnLogin.setTextColor(Color.parseColor(if (it) "#01025C" else "#757575"))
                binding.btnLogin.isEnabled = it
            }

        binding.btnGetSms.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.smsCacSmsCode(binding.etLoginMobile.text.toString())
            }, {

            })

        binding.btnLogin.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.bindMobile(
                    binding.etLoginMobile.text.toString(),
                    binding.etLoginSmsCode.text.toString(),
                )
            }, {
                it?.message?.logE()
            })

        viewModel.smsSuccess.observe(this, androidx.lifecycle.Observer {
            smsCountDownTimer()
            showToast("验证码获取成功")
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        LiveDataBus.get()
                            .with(
                                LiveDataBusKey.USER_LOGIN_STATUS,
                                UserManger.UserLoginStatus::class.java
                            )
                            .postValue(UserManger.UserLoginStatus.USE_BIND_MOBILE_SUCCESS)
                        showToast("绑定成功")
                        finish()
                    }
                }
            })
        binding.btnNoBind.setOnClickListener {
            back()
        }
        binding.back.setOnClickListener {
            back()
        }
        playInit()
        viewModel.loginBgPath.observe(this, androidx.lifecycle.Observer {
            it?.let {
                try {
                    play(it)
                } catch (e: Exception) {
                    binding.loginVideo.visibility = View.GONE
                    binding.imBg.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun initData() {

    }

    fun playInit() {
        binding.loginVideo.holder.addCallback(object : SurfaceHolder.Callback {
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
            player.setDisplay(binding.loginVideo.holder)
            player.start()
        }
        player.setOnErrorListener { mp, what, extra ->
            binding.loginVideo.visibility = View.GONE
            binding.imBg.visibility = View.VISIBLE
            false
        }

    }

    fun play(videoUrl: String) {
        "${videoUrl}".logE()
        binding.loginVideo.visibility = View.VISIBLE
        player.reset()
        player.setDataSource(videoUrl)
        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        player.prepareAsync()
        player.isLooping = true
        binding.imBg.visibility = View.GONE
    }


    override fun back() {
        super.back()
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .postValue(UserManger.UserLoginStatus.USE_CANCEL_BIND_MOBILE)
    }


    /**
     * 获取验证码倒计时
     */
    private var subscribe: Disposable? = null

    private fun smsCountDownTimer() {
        var time: Long = 60
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.rxjava3.core.Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    subscribe = d
                    binding.btnGetSms.isEnabled = false
                }

                override fun onNext(t: Long) {
                    if (t < 59) {
                        time -= 1
                        binding.btnGetSms.text = "${time}s"
                    } else {
                        onComplete()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                    binding.btnGetSms.text = "获取验证码"
                    binding.btnGetSms.isEnabled = true
                    subscribe?.dispose()
                }
            })
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

    override fun isUserLightMode(): Boolean {
        return false
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.downLoginBgUrl()
    }


    override fun onDestroy() {
        super.onDestroy()
        subscribe?.let {
            it.dispose()
        }
        if (player.isPlaying) {
            player.stop()
            player.release()
        }
    }
}