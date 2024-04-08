package com.changanford.my.ui

import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.UserManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiConfirmCancelAccountBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：ConfirmCancelAccountUI
 *  创建者: zcy
 *  创建日期：2021/1/6 17:46
 *  描述: TODO
 *  修改描述：TODO
 */

@Route(path = ARouterMyPath.ConfirmCancelAccountUI)
class ConfirmCancelAccountUI :
    BaseMineUI<UiConfirmCancelAccountBinding, SignViewModel>(), View.OnClickListener {
    var reason: String = ""

    lateinit var countDownTimer: CountDownTimer

    override fun initView() {

        intent.extras?.getString("value")?.let {
            reason = it
        }

        binding.mineToolbar.toolbarTitle.text = "确认注销"
        binding.mineToolbar.toolbar.setNavigationOnClickListener { finish() }

        UserManger.getSysUserInfo()?.mobile?.let {
            MineUtils.cancelAccountHintContent(binding.phoneHint, it)
        }

        binding.cancelBtn.setOnClickListener {
            back()
        }

        //确认注销成功
        binding.confirmCancelBtn.setOnClickListener(this)

        binding.getVerifySms.setOnClickListener(this)

        countDownTimer = MineUtils.date(binding.getVerifySms)

        viewModel.smsSuccess.observe(this, Observer {
            if (it) {
                showToast("验证码已发送")
                countDownTimer.start()
            }
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                //退出登录
                it?.let {
                    if (it == UserManger.UserLoginStatus.USER_LOGIN_OUT) {
                        back()
                    }
                }
            })
    }


    override fun back() {
        LiveDataBus.get().with(LiveDataBusKey.MINE_CANCEL_ACCOUNT, Boolean::class.java)
            .postValue(true)
        super.back()
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.get_verify_sms -> {
                submit(true)
            }
            R.id.confirm_cancel_btn -> {
                submit(false)
            }
        }
    }

    /**
     * 验证
     */
    private fun submit(isSendSms: Boolean) {

        var mobile = binding.phone.text.trim().toString()
        var smsCode = binding.verifySms.text.trim().toString()


        if (!MineUtils.isMobileNO(mobile)) {
            showToast("请输入正确手机号")
            return
        }

        if (isSendSms) {//获取验证码
            viewModel.getSmsCode(mobile)
            return
        }

        if (smsCode.isNullOrEmpty()) {
            showToast("请输入验证码")
            return
        }

        ConfirmTwoBtnPop(this).apply {
            title.apply {
                text = "是否确认注销账号？"
                visibility = View.VISIBLE
            }
            contentText.text = "帖子、福币、圈子、经验等级、活动、商城、勋章相关数据将被清除。"
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                viewModel.cancelAccount(mobile, smsCode, reason) {
                    it.onSuccess {
                        showToast("申请注销成功")
                        viewModel.loginOut()
                    }
                    it.onWithMsgFailure {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                dismiss()
            }
        }.showPopupWindow()
    }
}