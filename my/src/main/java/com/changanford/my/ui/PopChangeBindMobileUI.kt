package com.changanford.my.ui

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.PopChangeBindMobileCodeBinding
import com.changanford.my.viewmodel.CarAuthViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *  文件名：PopChangeBindModileUI
 *  创建者: zcy
 *  创建日期：2021/11/24 15:54
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.PopChangeBindMobileUI)
class PopChangeBindMobileUI : BaseMineUI<PopChangeBindMobileCodeBinding, CarAuthViewModel>() {

    private var carItemBean: CarItemBean? = null
    override fun initView() {
        intent.getSerializableExtra(RouterManger.KEY_TO_OBJ)?.let {
            carItemBean = it as CarItemBean
        }
        var mobile: String = MConstant.mine_phone
        if (mobile.isNotEmpty() && mobile.length >= 11) {
            mobile = "${mobile.substring(0, 3)}****${mobile.substring(7, 11)}"
        }

        carItemBean?.let {
            var oldMobile = it.oldBindPhone
            if (oldMobile.isNotEmpty() && oldMobile.length >= 11) {
                oldMobile =
                    "${oldMobile.substring(0, 3)}****${oldMobile.substring(7, 11)}"
            }
            binding.content.text = "您即将用当前手机号码替换${oldMobile}与该车辆进行绑定。请确认是否继续往下操作？"
            binding.mobile.text = oldMobile
        }

        binding.checkTrue.setOnClickListener {
            binding.group.visibility = View.VISIBLE
            binding.checkTrue.isChecked = true
            binding.checkFalse.isChecked = false
        }

        binding.checkFalse.setOnClickListener {
            binding.group.visibility = View.GONE
            binding.checkFalse.isChecked = true
            binding.checkTrue.isChecked = false
        }

        binding.cancel.setOnClickListener {
            back()
        }

        binding.submit.setOnClickListener {
            back()
        }

        binding.btnGetSms.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.getSmsCode(carItemBean?.oldBindPhone) {
                    it.onSuccess {
                        smsCountDownTimer()
                    }
                    it.onWithMsgFailure {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }, {

            })

        binding.submit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                carItemBean?.let { carItemBean ->
                    var sms: String = binding.etLoginSmsCode.text.toString()
                    if (binding.group.visibility == View.GONE) {
                        sms = ""
                    }
                    viewModel.changePhoneBind(carItemBean.vin, carItemBean.oldBindPhone, sms) {
                        it.onSuccess { _ ->
                            if (sms.isNullOrEmpty()) {
                                finish()
                            } else {
                                RouterManger.param(
                                    RouterManger.KEY_TO_ITEM,
                                    CarItemBean(
                                        vin = carItemBean.vin,
                                        authStatus = 2
                                    )
                                )
                                    .startARouter(ARouterMyPath.CarAuthSuccessUI)
                                finish()
                            }
                        }
                        it.onWithMsgFailure {
                            it?.let {
                                showToast(it)
                            }
                        }
                    }
                }
            }, {

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


    override fun onDestroy() {
        super.onDestroy()
        subscribe?.let {
            it.dispose()
        }
    }

}