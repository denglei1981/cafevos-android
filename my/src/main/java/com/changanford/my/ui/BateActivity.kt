package com.changanford.my.ui

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.ISDEBUG
import com.changanford.common.util.SPUtils
import com.changanford.my.databinding.BateactivityBinding
import com.luck.picture.lib.tools.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = ARouterMyPath.BateActivity)
class BateActivity :
    BaseActivity<BateactivityBinding, EmptyViewModel>() {

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barImgBack.setOnClickListener { finish() }
        binding.title.barTvTitle.text = "环境切换"

        binding.tvCeshi.setOnClickListener {
            if (MConstant.isDebug) {
                ToastUtils.s(BaseApplication.INSTANT, "当前为测试环境,不需要切换")

            } else {
                AlertDialog(this).builder()
                    .setTitle("即将切换到测试环境")
                    .setNegativeButton("取消") { }.setPositiveButton("确定") {
                        lifecycleScope.launch {
                            SPUtils.setParam(BaseApplication.INSTANT, ISDEBUG, true)
                            delay(1200)
                            FastClickUtils.relaunchApp()
                        }
                    }.show()
            }

        }

        binding.tvZhengshi.setOnClickListener {
            if (MConstant.isDebug) {
                AlertDialog(this).builder().setTitle("即将切换到正式环境")
                    .setNegativeButton("取消") { }.setPositiveButton("确定") {
                        lifecycleScope.launch {
                            SPUtils.setParam(BaseApplication.INSTANT, ISDEBUG, false)
                            delay(1200)
                            FastClickUtils.relaunchApp()
                        }
                    }.show()
            } else {
                ToastUtils.s(BaseApplication.INSTANT, "当前为正式环境,不需要切换")
            }
        }
    }

    override fun initData() {}
}