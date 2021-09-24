package com.changanford.my.ui

import android.content.Intent
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.ConfigUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiAccountSafeBinding
import com.changanford.my.viewmodel.SignViewModel
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.launch
import org.json.JSONObject
import razerdp.basepopup.BasePopupWindow

/**
 *  文件名：AccountSafeUI
 *  创建者: zcy
 *  创建日期：2021/9/23 15:29
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.AccountSafeUI)
class AccountSafeUI : BaseMineUI<UiAccountSafeBinding, SignViewModel>() {

    var qqCallback = object : IUiListener {
        override fun onComplete(p0: Any?) {
            try {
                var json = JSONObject(p0.toString())
                var openId = json.getString("openid")
                var accessToken = json.getString("access_token")
                bindMobile("qq", "${accessToken},${openId}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancel() {
            showToast("取消QQ绑定")
            initData()
        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(p0: UiError?) {
            showToast("QQ绑定失败")
            initData()
        }
    }


    private lateinit var tencent: Tencent
    private lateinit var wxApi: IWXAPI
    var type: Int = 0
    lateinit var bindType: String

    override fun initView() {

        tencent = Tencent.createInstance(ConfigUtils.QQAPPID, this)

        wxApi = WXAPIFactory.createWXAPI(this, ConfigUtils.WXAPPID)
        wxApi.registerApp(ConfigUtils.WXAPPID)

        binding.accountToolbar.toolbarTitle.text = "账号与安全"
        binding.accountToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        viewModel.bindAccount.observe(this, Observer {
            it?.forEach { bean ->
                when (bean.type) {
                    "weixin" -> {
                        binding.safeWxNum.text = if (bean.bind) "已绑定" else "未绑定"
                        binding.safeWxNum.isSelected = bean.bind
                    }
                    "qq" -> {
                        binding.safeQqNum.text = if (bean.bind) "已绑定" else "未绑定"
                        binding.safeQqNum.isSelected = bean.bind
                    }
                    "apple" -> {
                        binding.safeAppleNum.text = if (bean.bind) "已绑定" else "未绑定"
                        binding.safeAppleNum.isSelected = bean.bind
                    }
                }
            }
        })

        UserManger.getSysUserInfo()?.let {
            binding.safeIdNum.text = it.uid
        }

        binding.clearAccount.paint.flags = Paint.UNDERLINE_TEXT_FLAG

        viewModel.bindOtherAccount.observe(this, Observer {
            when (it) {
                "bindSuccess" -> {
                    showToast("绑定成功")
                    initData()
                }
                "unBindSuccess" -> {
                    showToast("解绑成功")
                    initData()
                }
                else -> {
                    showToast(it)
                }
            }
        })

        var pop = ConfirmPop()
        pop.contentText.text = "您确认取消绑定吗"
        pop.btnConfirm.setOnClickListener {
            pop.dismiss()
            lifecycleScope.launch {
                viewModel.unBindOtherAuth(bindType)
            }
        }
        pop.btnCancel.setOnClickListener {
            pop.dismiss()
            initData()
        }

        //qq
        binding.safeQq.setOnClickListener {
            type = 1
            if (binding.safeQqNum.isSelected) {
                bindType = "qq"
                pop.showPopupWindow()
            } else {
                tencent.login(this, "all", qqCallback)
            }
        }

        //微信
        binding.safeWx.setOnClickListener {
            type = 2
            if (binding.safeWxNum.isSelected) {
                bindType = "weixin"
                pop.showPopupWindow()
            } else {
                //产生6位数随机数为例
                ConfigUtils.NUM = ((Math.random() * 9 + 1) * 100000).toString()
                val req = SendAuth.Req()
                req.state = "diandi_wx_login"
                req.scope = "snsapi_userinfo"
                wxApi.sendReq(req)
            }
        }

        //apple
        binding.safeApple.setOnClickListener {
            if (binding.safeAppleNum.isSelected) {
                bindType = "apple"
                pop.showPopupWindow()
            } else {
                binding.safeAppleNum.isSelected = false
                showToast("当前系统版本暂不支持绑定苹果账号")
            }
        }

        binding.clearAccount.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineCancelAccountUI)
        }
    }

    fun bindMobile(type: String, code: String) {
        lifecycleScope.launch {
            viewModel.bindOtherAuth(type, code)
        }
    }

    override fun initData() {
        lifecycleScope.launch { viewModel.bindAccount() }
    }

    inner class ConfirmPop : BasePopupWindow(this) {
        lateinit var contentText: AppCompatTextView
        lateinit var btnCancel: AppCompatButton
        lateinit var btnConfirm: AppCompatButton


        init {
            setContentView(R.layout.pop_two_btn)
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)
            contentText = contentView.findViewById(R.id.text_content)
            btnCancel = contentView.findViewById(R.id.btn_cancel)
            btnConfirm = contentView.findViewById(R.id.btn_comfir)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.onActivityResultData(requestCode, resultCode, data, qqCallback)
    }
}