package com.changanford.common.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ActivitySlaBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.viewmodel.SLAAViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author: niubobo
 * @date: 2024/12/9
 * @description：服务协议确认
 */
@Route(path = ARouterCommonPath.SLAActivity)
class SLAActivity : BaseActivity<ActivitySlaBinding, SLAAViewModel>() {

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@SLAActivity,
                Builder().apply { title = "服务协议确认" })
            webView.loadUrl("https://fanyi.baidu.com/mtpe-individual/multimodal")
            binding.tvSure.isEnabled = false
            binding.tvSure.setOnFastClickListener {
                startARouter(ARouterCommonPath.SignatureActivity)
            }
        }
        startCountdown()
    }

    private fun startCountdown() {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 6 downTo 1) {
                binding.tvSure.text = "我已阅读并确认，去签字(${i})"
                delay(1000) // 延迟 1 秒
            }
            binding.tvSure.text = "我已阅读并确认，去签字"
            binding.tvSure.isEnabled = true
        }
    }

    override fun initData() {

    }
}