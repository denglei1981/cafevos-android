package com.changanford.common.ui.activity

import android.annotation.SuppressLint
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.databinding.ActivitySlaBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.viewmodel.SLAAViewModel
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
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

    private var picPath = ""
    private var content = ""
    private var orderNo = ""

    override fun initView() {
        intent.getStringExtra("orderNo")?.let {
            orderNo = it
        }
        intent.getParcelableExtra<OrderInfoBean>("orderInfoBean")?.let {
            orderNo = it.orderNo.toString()
        }
        binding.run {
            title.toolbar.initTitleBar(
                this@SLAActivity,
                Builder().apply { title = "服务协议确认" })
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    getDataFromWebPage()
                }
            }
            webView.loadUrl("${MConstant.SLA_RULE_URL}?orderNo=${orderNo}")
            binding.tvSure.isEnabled = false
            binding.tvSure.setOnFastClickListener {
                startARouter(ARouterCommonPath.SignatureActivity)
            }
        }
        startCountdown()
        LiveDataBus.get().withs<String>(LiveDataBusKey.SIGNATURE_PIC_PATH).observe(this) {
            picPath = it
            binding.tvSure.text = "去支付"
            binding.tvSure.setOnFastClickListener {
                viewModel.addWbOrder(content, orderNo, picPath) {
                    intent.getStringExtra("orderNo")?.let {
                        JumpUtils.instans?.jump(110, orderNo)
                    }
                    intent.getParcelableExtra<OrderInfoBean>("orderInfoBean")?.let {
                        intent.extras?.let { it1 ->
                            startARouter(
                                ARouterShopPath.PayConfirmActivity,
                                it1, true
                            )
                        }
                    }
                    finish()
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun startCountdown() {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 6 downTo 1) {
                binding.tvSure.text = "我已阅读并确认，去签字(${i})"
                delay(1000)
            }
            binding.tvSure.text = "我已阅读并确认，去签字"
            binding.tvSure.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        getDataFromWebPage()
    }

    private fun getDataFromWebPage() {
        binding.webView.evaluateJavascript("javascript:\$getPageData();") { result ->
            content = result
        }
    }

    override fun initData() {

    }
}