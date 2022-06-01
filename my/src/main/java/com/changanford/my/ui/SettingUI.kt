package com.changanford.my.ui

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.utilext.CleanDataUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiSeetingBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.SignViewModel
import kotlin.concurrent.thread

/**
 *  文件名：SettingUI
 *  创建者: zcy
 *  创建日期：2021/9/9 13:40
 */
@Route(path = ARouterMyPath.MineSettingUI)
class SettingUI : BaseMineUI<UiSeetingBinding, SignViewModel>() {

    override fun initView() {
        binding.setToolbar.toolbarTitle.text = "设置"
        binding.setToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.btnLoginOut.isEnabled = UserManger.isLogin()
        binding.btnLoginOut.visibility = if (UserManger.isLogin()) View.VISIBLE else View.GONE

        binding.btnLoginOut.setOnClickListener {
            var confirmPop = ConfirmTwoBtnPop(this)
            confirmPop.contentText.text = "确认退出登录？"
            confirmPop.btnConfirm.setOnClickListener {
                confirmPop.dismiss()
                viewModel.loginOut()
            }
            confirmPop.btnCancel.setOnClickListener {
                confirmPop.dismiss()
            }
            confirmPop.showPopupWindow()
        }

        binding.setSafe.setOnClickListener {
            RouterManger.needLogin(true).startARouter(ARouterMyPath.AccountSafeUI)
        }
        binding.setbg.setOnClickListener {
            if (MConstant.isCanQeck&&FastClickUtils.fastRepeatClick()) {
                startARouter(ARouterMyPath.BateActivity)
            }
        }
        binding.setFord.setOnClickListener {
            startARouter(ARouterMyPath.AboutUI)
        }
        //推送通知
        binding.setNotice.setOnClickListener {
            WCommonUtil.openNotificationSetting(this)
        }
        binding.setVersion.text = "版本${DeviceUtils.getversionName()}"

        var cache = CleanDataUtils.getTotalCacheSize(this)
        if (!cache.contains("0.00")) {
            binding.setCacheSize.text = CleanDataUtils.getTotalCacheSize(this)
            binding.setCacheSize.setOnClickListener {
                var d = LoadDialog(this)
                d.setLoadingText("正在清除缓存...")
                d.show()
                thread {
                    CleanDataUtils.clearAllCache(this);
                    runOnUiThread {
                        d.dismiss()
                        var cache = CleanDataUtils.getTotalCacheSize(this)
                        if (cache.contains("0.00")) {
                            binding.setCacheSize.text = ""
                        }
                    }
                }
                MConstant.isDownLoginBgSuccess = false
            }
        }
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                finish()
            })

        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                //退出登录
                it?.let {
                    if (it == UserManger.UserLoginStatus.USER_LOGIN_OUT) {
                        finish()
                    }
                }
            })

    }

    override fun initData() {
        //
    }
}