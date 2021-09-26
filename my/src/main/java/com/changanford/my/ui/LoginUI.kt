package com.changanford.my.ui

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.PUSH_ID
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_SIGN_WX_CODE
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiLoginBinding
import com.changanford.my.utils.signAgreement
import com.changanford.my.viewmodel.SignViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.checkedChanges
import com.jakewharton.rxbinding4.widget.textChanges
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 *  文件名：LoginUI
 *  创建者: zcy
 *  创建日期：2021/9/9 10:01
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.SignUI)
class LoginUI : BaseMineUI<UiLoginBinding, SignViewModel>() {

    private lateinit var tencent: Tencent
    private lateinit var wxApi: IWXAPI

    var qqCallback = object : IUiListener {
        override fun onComplete(p0: Any?) {
            try {
                var json = JSONObject(p0.toString())
                var openId = json.getString("openid")
                var accessToken = json.getString("access_token")
                lifecycleScope.launch {
                    viewModel.otherLogin(
                        "qq", "$accessToken,$openId",
                        SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancel() {
            showToast("取消QQ登录")
        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(p0: UiError?) {
            showToast("QQ登录失败")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun initView() {
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, null)
        StatusBarUtil.setAndroidNativeLightStatusBar(this,false)
        tencent = Tencent.createInstance(MConstant.QQAPPID, this)

        wxApi = WXAPIFactory.createWXAPI(this, MConstant.WXAPPID)
        wxApi.registerApp(MConstant.WXAPPID)

        signAgreement(binding.signAgreement)

        var mobileText = binding.etLoginMobile.textChanges()
        var smsText = binding.etLoginSmsCode.textChanges()
        var checkBox = binding.cbEx.checkedChanges()

        Observable.combineLatest(
            mobileText,
            smsText,
            checkBox,
            Function3<CharSequence, CharSequence, Boolean, Boolean> { t1, t2, t3 ->
                binding.btnGetSms.isEnabled = t1.isNotEmpty()
                t1.isNotEmpty() && t2.isNotEmpty() && t3
            })
            .subscribe {
                binding.btnLogin.isEnabled = it
            }

        binding.btnGetSms.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lifecycleScope.launch {
                    viewModel.smsCacLogin(binding.etLoginMobile.text.toString())
                }
            }, {

            })

        binding.btnLogin.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lifecycleScope.launch {
                    viewModel.smsLogin(
                        binding.etLoginMobile.text.toString(),
                        binding.etLoginSmsCode.text.toString(),
                        SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                    )
                }
            }, {

            })

        //QQ登录
        binding.imQqLogin.clicks().debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(Function<Any, ObservableSource<Boolean>> {
                Observable.just(binding.cbEx.isChecked)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
//                longLog("勾选结果", "$it")
                if (!it) {
                    showToast("请同意相关协议")
                } else {
                    qqLogin()
                }
            })
        //微信登录
        binding.imWeixinLogin.clicks().debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(Function<Any, ObservableSource<Boolean>> {
                Observable.just(binding.cbEx.isChecked)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                if (!it) {
                    showToast("请同意相关协议")
                } else {
                    //产生6位数随机数为例
                    MConstant.NUM = ((Math.random() * 9 + 1) * 100000).toString()
                    val req = SendAuth.Req()
                    req.state = "diandi_wx_login"
                    req.scope = "snsapi_userinfo"
                    wxApi.sendReq(req)
                }
            })

        //三方微信返回
        LiveDataBus.get().with(MINE_SIGN_WX_CODE, String::class.java)
            .observe(this, androidx.lifecycle.Observer {
                lifecycleScope.launch {
                    viewModel.otherLogin(
                        "weixin", it, SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                    )
                }
            })

        viewModel.smsSuccess.observe(this, androidx.lifecycle.Observer {
            smsCountDownTimer()
        })

        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        finish()
                    }
                    UserManger.UserLoginStatus.USE_UNBIND_MOBILE -> {
                        RouterManger.startARouter(ARouterMyPath.MineBindMobileUI)
                    }
                }
            })
    }

    /**
     * qq登录
     */
    private fun qqLogin() {
        tencent.login(this, "all", qqCallback)
    }


    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.onActivityResultData(requestCode, resultCode, data, qqCallback)
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