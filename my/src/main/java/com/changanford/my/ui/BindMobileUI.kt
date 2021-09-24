package com.changanford.my.ui

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.StatusBarUtil
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
import kotlinx.coroutines.launch
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

    override fun initView() {
        StatusBarUtil.setTranslucentForImageView(this,null)

        var mobileText = binding.etLoginMobile.textChanges()
        var smsText = binding.etLoginSmsCode.textChanges()

        Observable.combineLatest(
            mobileText,
            smsText,
            BiFunction<CharSequence, CharSequence, Boolean> { t1, t2 ->
                binding.btnGetSms.isEnabled = t1.isNotEmpty()
                t1.isNotEmpty() && t2.isNotEmpty()
            })
            .subscribe {
                binding.btnLogin.isEnabled = it
            }

        binding.btnGetSms.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lifecycleScope.launch {
                    viewModel.getSmsCode(binding.etLoginMobile.text.toString())
                }
            }, {

            })

        binding.btnLogin.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lifecycleScope.launch {
                    viewModel.bindMobile(
                        binding.etLoginMobile.text.toString(),
                        binding.etLoginSmsCode.text.toString(),
                    )
                }
            }, {

            })

        viewModel.smsSuccess.observe(this, androidx.lifecycle.Observer {
            smsCountDownTimer()
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        showToast("绑定成功")
                        finish()
                    }
                }
            })
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

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.let {
            it.dispose()
        }
    }
}