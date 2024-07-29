package com.changanford.my.ui

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.view.SurfaceHolder
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouterFinish
import com.changanford.common.util.AppUtils
import com.changanford.common.util.ConfigUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.PUSH_ID
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_SIGN_WX_CODE
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.util.request.GetRequestResult
import com.changanford.common.util.request.getBizCode
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toastShow
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiLoginBinding
import com.changanford.my.utils.signAgreement
import com.changanford.my.viewmodel.SignViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.checkedChanges
import com.jakewharton.rxbinding4.widget.textChanges
import com.netease.nis.captcha.Captcha
import com.netease.nis.captcha.CaptchaConfiguration
import com.netease.nis.captcha.CaptchaListener
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
    var mPlayer = MediaPlayer()
    var fromSplash = false

    var qqCallback = object : IUiListener {
        override fun onComplete(p0: Any?) {
            try {
                var json = JSONObject(p0.toString())
                var openId = json.getString("openid")
                var accessToken = json.getString("access_token")
                viewModel.otherLogin(
                    "qq", "$accessToken,$openId",
                    SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                )
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

//    private val defaultLifecycleObserver = object : DefaultLifecycleObserver {
//        override fun onStop(owner: LifecycleOwner) {
//            super.onStop(owner)
//            //后台
//            ToastUtils.reToast("${resources.getString(R.string.app_name)}App已经进入后台")
//        }
//
//
//        override fun onStart(owner: LifecycleOwner) {
//            super.onStart(owner)
//            //前台
//        }
//
//    }

    override fun initView() {
//        ProcessLifecycleOwner.get().lifecycle.addObserver(defaultLifecycleObserver)
        fromSplash = intent.extras?.getBoolean("fromSplash", false) ?: false
        updateMainGio("登陆页", "登陆页")
        GioPageConstant.topicEntrance = "登陆页"
        AppUtils.setStatusBarMarginTop(binding.back, this)
        AppUtils.setStatusBarMarginTop(binding.title, this)
        UserManger.deleteUserInfo()
        playInit()
        MConstant.token = ""
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, null)
        tencent = Tencent.createInstance(ConfigUtils.QQAPPID, this)

        wxApi = WXAPIFactory.createWXAPI(this, ConfigUtils.WXAPPID)
        wxApi.registerApp(ConfigUtils.WXAPPID)

        binding.signAgreement.signAgreement()

        var mobileText = binding.etLoginMobile.textChanges()
        var smsText = binding.etLoginSmsCode.textChanges()
        var checkBox = binding.cbEx.checkedChanges()

        Observable.combineLatest(
            mobileText,
            smsText,
            checkBox,
            Function3<CharSequence, CharSequence, Boolean, Boolean> { t1, t2, t3 ->
                if (t3 == true) {
                    getBizCode(
                        this,
                        MConstant.agreementPrivacy + "," + MConstant.agreementRegister,
                        object :
                            GetRequestResult {
                            override fun success(data: Any) {
                                viewModel.ruleId = data.toString()
                            }

                        })
                }
                if (t1.toString().length == 11) {
                    binding.btnGetSms.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.color_1700F4
                        )
                    )
                    binding.btnGetSms.isEnabled = true
                } else {
                    binding.btnGetSms.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.color_c7c8ca
                        )
                    )
                    binding.btnGetSms.isEnabled = false
                }
                t1.isNotEmpty() && t2.isNotEmpty() && t3
            })
            .subscribe {
                binding.btnLogin.setTextColor(Color.parseColor(if (it) "#1700f4" else "#6f6e6e"))
                binding.btnLogin.isEnabled = it
            }

        binding.btnGetSms.clicks().throttleFirst(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.smartCode()
            }, {

            })

        binding.btnLogin.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.smsLogin(
                    binding.etLoginMobile.text.toString(),
                    binding.etLoginSmsCode.text.toString(),
                    SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                )
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
                viewModel.otherLogin(
                    "weixin", it, SPUtils.getParam(this@LoginUI, PUSH_ID, "11111") as String
                )
            })

        viewModel.smsSuccess.observe(this, androidx.lifecycle.Observer {
            smsCountDownTimer()
            showToast("验证码获取成功")
        })
        viewModel.smartCodeBean.observe(this) {
            if (!it.isOpen ) {
                viewModel.smsCacSmsCode(binding.etLoginMobile.text.toString())
            } else {
                it.captchaId?.let { it1 -> outCheckVerify(it1) }
            }
        }
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS,
                    UserManger.UserLoginStatus.USE_CANCEL_BIND_MOBILE -> {
                        back()
                    }

                    UserManger.UserLoginStatus.USE_UNBIND_MOBILE -> {
                        RouterManger.startARouter(ARouterMyPath.MineBindMobileUI)
                        back()
                    }

                    else -> {}
                }
            })

        viewModel.loginBgPath.observe(this, androidx.lifecycle.Observer {
            it?.let {
                try {
                    play(it)
                } catch (e: Exception) {
                    binding.loginVideo.visibility = View.GONE
                    binding.imBg.visibility = View.VISIBLE
                }
            }
        })
        binding.back.setOnClickListener {
            back()
        }
//        viewModel.downLoginBgUrl()
    }

    private var captcha: Captcha? = null

    //行为验证吗
    private fun outCheckVerify(captchaId:String) {
        if (binding.etLoginMobile.text.toString().isNullOrEmpty()) {
            toastShow("请输入手机号")
            return
        }
        val captchaConfiguration =
            CaptchaConfiguration.Builder().captchaId(captchaId)
                .languageType(CaptchaConfiguration.LangType.LANG_ZH_CN)
                .loadingText("安全检测中")
                .isCloseButtonBottom(true)
                .listener(object : CaptchaListener {
                    override fun onCaptchaShow() {}
                    override fun onValidate(result: String?, validate: String?, msg: String?) {
                        validate?.let {
                            if (it.isNotEmpty()) {
                                viewModel.getSmsCodeV2(binding.etLoginMobile.text.toString(),it,captchaId)
                            }
                        }
                    }

                    override fun onError(code: Int, msg: String?) {}
                    override fun onClose(closeType: Captcha.CloseType?) {}
                })
                .build(this)
        captcha = Captcha.getInstance().init(captchaConfiguration)
        captcha?.validate()
    }

    /**
     * qq登录
     */
    private fun qqLogin() {
        tencent.login(this, "all", qqCallback)
    }


    override fun initData() {

    }

    private fun playInit() {
        mPlayer.let { player ->
            binding.loginVideo.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    player.setDisplay(holder)
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                }

            })
            player.setOnPreparedListener {
                try {
                    if (!player.isPlaying) {
                        player.setDisplay(binding.loginVideo.holder)
                        player.start()
                    }
                } catch (e: Exception) {
                    binding.loginVideo.visibility = View.GONE
                    binding.imBg.visibility = View.VISIBLE
                }
            }
            player.setOnErrorListener { mp, what, extra ->
                binding.loginVideo.visibility = View.GONE
                binding.imBg.visibility = View.VISIBLE
                false
            }
        }
    }

    private fun play(videoUrl: String) {
        "----play 1".logE()
        videoUrl.logE()
        binding.loginVideo.visibility = View.VISIBLE
        if (isloaded) {
            mPlayer.start()
        } else {
            mPlayer.reset()
            mPlayer.setDataSource(videoUrl)
            mPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            mPlayer.prepareAsync()
            mPlayer.isLooping = true
            isloaded = true
        }
        binding.imBg.visibility = View.GONE
        "----play 2".logE()
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

    override fun isUserLightMode(): Boolean {
        return false
    }

    override fun onBackPressed() {
        if (fromSplash) {
            startARouterFinish(this, ARouterHomePath.MainActivity)
        } else {
            super.onBackPressed()
        }
    }

    override fun back() {
        if (fromSplash) {
            startARouterFinish(this, ARouterHomePath.MainActivity)
        } else {
            super.back()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        ProcessLifecycleOwner.get().lifecycle.removeObserver(defaultLifecycleObserver)
        subscribe?.let {
            it.dispose()
        }
        mPlayer.let {
            try {
                if (it.isPlaying)
                    it.stop()
                it.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        captcha?.destroy()
    }


    var isloaded = false

    override fun onResume() {
        super.onResume()
        try {
            if (isloaded) {
                viewModel.loginBgPath.value?.let { play(it) }
            } else {
                viewModel.downLoginBgUrl()
            }
//            mPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            binding.loginVideo.visibility = View.VISIBLE
            binding.imBg.visibility = View.GONE
        }
    }

}