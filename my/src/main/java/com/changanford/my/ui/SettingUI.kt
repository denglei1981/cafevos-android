package com.changanford.my.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.utilext.CleanDataUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiSeetingBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.SignViewModel
import kotlin.concurrent.thread

/**
 *  文件名：SettingUI
 *  创建者: zcy
 *  创建日期：2021/9/9 13:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineSettingUI)
class SettingUI : BaseMineUI<UiSeetingBinding, SignViewModel>() {

    override fun initView() {
        binding.setToolbar.toolbarTitle.text = "设置"
        binding.setToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.btnLoginOut.isEnabled = UserManger.isLogin()

        binding.btnLoginOut.setOnClickListener {
            var confirmPop = ConfirmTwoBtnPop(this)
            confirmPop.contentText.text = "确认退出登录？"
            confirmPop.btnConfirm.setOnClickListener {
                confirmPop.dismiss()
                viewModel.loginOut()
                finish()
            }
            confirmPop.btnCancel.setOnClickListener {
                confirmPop.dismiss()
            }
            confirmPop.showPopupWindow()
        }

        binding.setSafe.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.AccountSafeUI)
        }
        binding.setFord.setOnClickListener {
            startARouter(ARouterMyPath.AboutUI)
        }
        binding.setVersion.text = "V${DeviceUtils.getversionName()}"

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
            }
        }
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                finish()
            })

    }

    override fun initData() {

    }
}