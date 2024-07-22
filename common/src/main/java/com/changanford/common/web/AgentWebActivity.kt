package com.changanford.common.web


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.ShareBean
import com.changanford.common.databinding.ActivityWebveiwBinding
import com.changanford.common.manger.UserManger
import com.changanford.common.pay.PayViewModule
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.CaptureActivity.SCAN_RESULT
import com.changanford.common.ui.LoadingDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FileHelper
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.totalWebNum
import com.changanford.common.util.SoftHideKeyBoardUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toastShow
import com.changanford.common.wutil.UnionPayUtils
import com.just.agentweb.AgentWebConfig
import com.qw.soul.permission.bean.Permissions
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.yalantis.ucrop.UCrop
import org.json.JSONArray
import org.json.JSONException


/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.evos.ui.fragment.AgentWebActivity
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/13 15:45
 * @Description: 　WebView
 * *********************************************************************************
 */
@Route(path = ARouterHomePath.AgentWebActivity)
class AgentWebActivity : BaseActivity<ActivityWebveiwBinding, AgentWebViewModle>(), JsCallback {

    private lateinit var shareViewModule: ShareViewModule //分享
    private lateinit var payViewModule: PayViewModule //支付

    //    private lateinit var mineSignViewModel: MineSignViewModel //获取个人信息，获取U享卡列表
    lateinit var headerView: View
    var url: String = ""
    private var subcallback = "subcallback"//右上角图标文字点击时给h5的回调
    private var uploadImgCallback = ""//上传图片回调
    private var payCallback = ""//支付回调
    private var getLocationCallback = ""//获取经纬度回调
    private var shareCallBack = ""//分享回调
    private var shareBean: ShareBean? = null
    private var cacTokenCallBack = ""//获取(刷新)cacToken回调
    private var loginAppCallBack = ""//登录App回调
    private var bindPhoneCallBack = ""//绑定手机号回调
    private var backEventCallBack = ""//自定义返回
    private var closeEventCallBack = ""//自定义返回
    private var chooseAddressCallback = ""//选择地址回调
    private var h5OrderPayCallback = ""//h5订单支付
    private var h5BindDealerCallback = ""//绑定经销商
    private var getMyInfoCallback = ""//获取用户信息回调
    private var getUniCardsListCallback = ""//获取用户U享卡信息回调
    private var showScanCallback = ""//扫码识别回调
    private var getCurVinCallback = ""//获取当前默认车辆vin
    private var addPlateNumCallback = ""//H5添加车牌回调
    private var jrsdkCallBack = "" //h5调起金融sdk回调
    private var getAccessCodeCallBack = ""//h5授权回调
    private var clientId = ""
    private var redirectUrl = ""
    private val REQUEST_CODE_FILE_CHOOSER = 1

    private var mUploadCallbackForLowApi: ValueCallback<Uri>? = null
    private var mUploadCallbackForHighApi: ValueCallback<Array<Uri>>? = null

    private var setNavTitleKey: String = System.currentTimeMillis().toString()
    private var localWebNum = -1
    private var loadingDialog: LoadingDialog? = null

    companion object {
        private const val REQUEST_PIC = 0x5431//图片
        private const val REQUEST_CUT = 0x531//剪切
        private const val SCAN_REQUEST_CODE: Int = 100//扫码
        var isOnPause = true
    }

    override fun initView() {
        updateMainGio("无", "无")
        AppUtils.setStatusBarPaddingTop(binding.titleBar.commTitleBar, this)
        SoftHideKeyBoardUtil.assistActivity(this)
        loadingDialog = LoadingDialog(this)
        totalWebNum += 1
        localWebNum = totalWebNum
        shareViewModule = createViewModel(ShareViewModule::class.java)
        payViewModule =
            createViewModel(PayViewModule::class.java)

        val isGoneTitle = intent.getBooleanExtra("isGoneTitle", false)
        headerView = findViewById(com.changanford.common.R.id.title_bar)
        if (isGoneTitle) {
            headerView.isVisible = false
        }
        headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_back).setOnClickListener {
            if (handleH5Back()) {
                return@setOnClickListener
            }
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
        headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_close).setOnClickListener {
            if (handleH5X()){
                return@setOnClickListener
            }
            finish()
        }
        registerLiveBus()
        initObserver()
    }

    fun quickCallJs(
        method: String,
        vararg params: String?,
        callback: ValueCallback<String?>? = ValueCallback { }
    ) {
        val sb = StringBuilder()
        sb.append("javascript:$method")
        if (params.isEmpty()) {
            sb.append("()")
        } else {
            sb.append("(").append(concat(*params)).append(")")
        }
        binding.webView.post {
            binding.webView.evaluateJavascript(sb.toString(), callback)
        }
    }

    private fun concat(vararg params: String?): String {
        val mStringBuilder = java.lang.StringBuilder()
        for (i in params.indices) {
            val param = params[i]
            if (!isJson(param)) {
                mStringBuilder.append("\"").append(param).append("\"")
            } else {
                mStringBuilder.append(param)
            }
            if (i != params.size - 1) {
                mStringBuilder.append(" , ")
            }
        }
        return mStringBuilder.toString()
    }

    private fun isJson(target: String?): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        }
        var tag = false
        tag = try {
            if (target?.startsWith("[") == true) {
                JSONArray(target)
            } else {
                org.json.JSONObject(target)
            }
            true
        } catch (ignore: JSONException) {
            //            ignore.printStackTrace();
            false
        }
        return tag
    }

    /**
     * LiveDataBus获取消息的处理
     */
    private fun registerLiveBus() {
        LiveDataBus.get().with(LiveDataBusKey.JUMP_JRSDK).observe(this, Observer {
            jrsdkCallBack = it as String
        })

        //分享
        LiveDataBus.get().with(LiveDataBusKey.WEB_SHARE).observe(this, Observer {
            val map: HashMap<String, String> = it as HashMap<String, String>
            val jsonStr = map["jsonStr"].toString()
            shareCallBack = map["shareCallBack"].toString()
            handleShare(jsonStr)
        })
        //分享-小程序
        LiveDataBus.get().with(LiveDataBusKey.WEB_SMALL_PROGRAM_WX_SHARE).observe(this, Observer {
            shareCallBack = it as String
        })
        //设置头部，全部
//        LiveDataBus.get().with(LiveDataBusKey.WEB_SET_NAV_TITLE).observe(this, Observer { it ->
//
//        })
        //设置头部，单独
        LiveDataBus.get().with(setNavTitleKey).observe(this, Observer { it ->
            val map = it as HashMap<String, String>
            setHeadTitle(map["title"])
            setStyle(map["style"])
            subcallback = map["subcallback"].toString()
        })
        //银联支付
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_UNION_PAY).observe(this, Observer {
            if (totalWebNum == localWebNum) {
                val map = it as HashMap<*, *>
                val payType: Int = map["payType"] as Int
                val appPayRequest = map["appPayRequest"].toString()
                val serverMode = map["serverMode"].toString()
                payCallback = map["callback"].toString()
                UnionPayUtils.goUnionPay(this, payType, appPayRequest, serverMode)
            }
        })
        //支付
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_PAY).observe(this, Observer {
            if (totalWebNum == localWebNum) {
                val map = it as HashMap<String, Any>
                val payCode = map["payCode"]
                val param = map["param"].toString()
                payCallback = map["callback"].toString()
                payViewModule.goPay(this, payCode.toString(), param)
            }
        })
        //支付宝支付结果
        LiveDataBus.get().with(LiveDataBusKey.ALIPAY_RESULT).observe(this,
            Observer {
                if (totalWebNum == localWebNum) {
                    when (it) {
                        true -> {
                            toastShow("支付成功")
                            quickCallJs(payCallback, "true") {}
//                            quickCallJs(payCallback, "true")
                        }

                        false -> {
                            toastShow("支付失败")
                            quickCallJs(payCallback, "false") {}
//                            quickCallJs(payCallback, "false")
                        }
                    }
                }
            })
        //微信支付结果
        LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).observe(this,
            Observer {
                if (totalWebNum == localWebNum) {
                    when (it) {
                        0 -> {
                            quickCallJs(payCallback, "true") {}
                        }//成功
                        1 -> {
                            quickCallJs(payCallback, "false") {}
                        }//失败
                        2 -> {
                            quickCallJs(payCallback, "false") {}
                        }//取消
                    }
                }
            })
        //银联-支付回调
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_UNION_PAY_BACK).observe(this) {
            if (totalWebNum == localWebNum) {
                when (it) {
                    0 -> {//成功
                        quickCallJs(payCallback, "true") {}
                    }

                    else -> {//1 失败 2 取消
                        quickCallJs(payCallback, "false") {}
                    }
                }
            }
        }

        //显示隐藏导航栏
        LiveDataBus.get().with(LiveDataBusKey.WEB_NAV_HID).observe(this, Observer {
            if (totalWebNum == localWebNum) {
                setTitleHide(it as Boolean)
            }
        })
//        //登录成功
//        LiveDataBus.get().with(LiveDataBusKey.WEB_LOGIN_SUCCESS).observe(this, Observer {
//            quickCallJs(it as String?)
//        })
        //上传图片
        LiveDataBus.get().with(LiveDataBusKey.WEB_UPLOAD_IMG).observe(this, Observer {
            var map: HashMap<String, String> = it as HashMap<String, String>
            var img = map["img"]
            img?.let { it1 -> viewModel.uploadImg(it1) }
            uploadImgCallback = map["callback"].toString()
        })
        //地图定位
        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_LOCATION).observe(this, Observer {
            getLocationCallback = it as String
            val permissions = Permissions.build(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            val success = {
                if (JumpUtils.instans?.isOPen(this@AgentWebActivity) == true) {
                    viewModel.initLocationOption()
                } else {
                    quickCallJs(getLocationCallback, "false") {}
                    toastShow("手机没有打开定位权限,请手动去设置页打开权限")
                }
            }
            val fail = {
                toastShow("用户拒绝了权限")
                quickCallJs(getLocationCallback, "false") {}
            }
            PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
        })
        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                quickCallJs(shareCallBack, it.toString()) {}
                shareViewModule.shareBack(shareBean)
            }
        })
        //登录
        LiveDataBus.get().with(LiveDataBusKey.WEB_LOGIN_APP).observe(this, Observer {
            loginAppCallBack = it as String
        })
        //登录成功
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                when (it) {
                    //登录成功
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        quickCallJs(loginAppCallBack, "true") {}
                        doGetAccessCode()
                    }

                    //取消绑定手机号
                    UserManger.UserLoginStatus.USE_CANCEL_BIND_MOBILE -> {
                        quickCallJs(loginAppCallBack, "false") {}
                        quickCallJs(bindPhoneCallBack, "false") {}
                    }
                    //绑定手机号成功
                    UserManger.UserLoginStatus.USE_BIND_MOBILE_SUCCESS -> {
                        quickCallJs(loginAppCallBack, "true") {}
                        quickCallJs(bindPhoneCallBack, "true")
                        doGetAccessCode()
                    }

                    else -> {

                    }
                }
            })
        //绑定手机号
        LiveDataBus.get().with(LiveDataBusKey.WEB_BIND_PHONE).observe(this, Observer {
            bindPhoneCallBack = it as String
        })

        //自定义返回
        LiveDataBus.get().with(LiveDataBusKey.WEB_BACKEVENT).observe(this, Observer {
            backEventCallBack = it as String
        })
        //自定义x事件
        LiveDataBus.get().with(LiveDataBusKey.WEB_X_CLICK).observe(this, Observer {
            closeEventCallBack = it as String
        })
        //关闭页面
        LiveDataBus.get().with(LiveDataBusKey.WEB_CLOSEPAGE).observe(this, Observer {
            if ((totalWebNum - localWebNum).toString() == it) {
                finish()
            }
        })

        //h5选择地址
        LiveDataBus.get().with(LiveDataBusKey.WEB_CHOOSE_ADDRESS, String::class.java)
            .observe(this, Observer {
                chooseAddressCallback = it
            })

        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java)
            .observe(this,
                Observer {
                    it?.let {
                        quickCallJs(chooseAddressCallback, it)
                    }
                })

        //h5订单支付
        LiveDataBus.get().with(LiveDataBusKey.WEB_ORDER_PAY, String::class.java)
            .observe(this,
                Observer {
                    h5OrderPayCallback = it
                })

        //H5支付结果回调
        LiveDataBus.get().with(LiveDataBusKey.WEB_ORDER_PAY_STATUS, Boolean::class.java)
            .observe(this,
                Observer {
                    quickCallJs(h5OrderPayCallback, it.toString())
                })

        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_MYINFO, String::class.java).observe(this) {
            getMyInfoCallback = it
            viewModel.getUserInfo { infoBean ->
                try {
                    var userJson = JSON.toJSONString(infoBean)
                    if (!userJson.isNullOrEmpty()) {
                        quickCallJs(getMyInfoCallback, userJson)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
//            UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().getUser()
//                .observe(this) { infoBean ->
//                    val userJson = infoBean?.userJson
//                    if (!userJson.isNullOrEmpty()) {
//                        quickCallJs(getMyInfoCallback, userJson)
//                    }
//                }
//                mineSignViewModel.getUserInfo()
        }
        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_UNICARDS_LIST, String::class.java)
            .observe(this,
                Observer {
                    getUniCardsListCallback = it
//                    mineSignViewModel.getUniCards()
                })
        LiveDataBus.get().with(LiveDataBusKey.WEB_SHOW_SCAN, String::class.java).observe(this,
            Observer {
                showScanCallback = it
                scan()
            })
        LiveDataBus.get().with(LiveDataBusKey.GET_CUR_VIN, String::class.java)
            .observe(this, Observer {
                getCurVinCallback = it
//                mineSignViewModel.queryAuthCar(object : ResponseObserver<BaseBean<UniCarAuth>>() {
//                    override fun onSuccess(response: BaseBean<UniCarAuth>) {
//                        if (response.data != null && response.data.isAuth != null && response.data.isAuth) {
//                            if (response.data.carAuth != null) {
//                                quickCallJs(
//                                    getCurVinCallback,
//                                    response.data.carAuth!!.vin
//                                )
//                            } else {
//                                quickCallJs(getCurVinCallback, "")
//                            }
//                        } else {
//                            quickCallJs(getCurVinCallback, "")
//                        }
//                    }
//
//                    override fun onFail(e: ApiException) {
//                        quickCallJs(getCurVinCallback, "")
//                    }
//                })
            })

        /**
         * H5调取添加车牌，需要回调车牌，不请求接口
         */
        LiveDataBus.get().with(LiveDataBusKey.MINE_ADD_PLATE_NUM, String::class.java).observe(this,
            Observer {
                addPlateNumCallback = it
            })

        /**
         * 添加车牌成功
         */
        LiveDataBus.get().with(LiveDataBusKey.MINE_CAR_CARD_NUM, String::class.java).observe(this,
            Observer {
                quickCallJs(addPlateNumCallback, it.toString())
            })

        /**
         * 金融返回处理回调
         */
        LiveDataBus.get().with(LiveDataBusKey.JUMP_JR_BACK, String::class.java).observe(this,
            Observer {
                quickCallJs(jrsdkCallBack, it.toString())
            })
        /**
         * 砍价下单回调
         */
//        LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK).observe(this,  {
//            if("2"==it)this.finish()
//        })

        //获取用户认证车辆列表
        LiveDataBus.get().with(LiveDataBusKey.GET_USER_APPROVE_CAR, String::class.java)
            .observe(
                this
            ) {
                viewModel.getMyBindCarList(object : MyBindCarList {
                    override fun myBindCarList(string: String) {
                        quickCallJs(it, string)
                    }

                })
            }
    }

    fun getAccessCode(clientId: String, redirectUrl: String, callback: String) {
        if (UserManger.isLogin()) {
            viewModel?.getH5AccessCode(clientId, redirectUrl) {
                quickCallJs(callback, it)
            }
        } else {
            getAccessCodeCallBack = callback
            this.clientId = clientId
            this.redirectUrl = redirectUrl
            JumpUtils.instans?.jump(100, "")
        }
    }

    private fun doGetAccessCode() {
        if (getAccessCodeCallBack.isNotEmpty()) {
            viewModel?.getH5AccessCode(clientId, redirectUrl) {
                quickCallJs(getAccessCodeCallBack, it)
                getAccessCodeCallBack = ""
            }
        }
    }

    override fun initData() {
        val data = intent.extras
        data?.let {
            val tempUrl = data.getString("value")
            if (!tempUrl.isNullOrEmpty()) {
                url = tempUrl
                Log.e("WEBURL", url)
            }
        }
        initWeb()
        LiveDataBus.get().with(LiveDataBusKey.H5POST_SUCCESS).observe(this, Observer {
            it?.let {
                quickCallJs(h5callback, it.toString())
            }
        })
    }

    fun setNavTitle(map: HashMap<String, String>) {
        LiveDataBus.get().with(setNavTitleKey).postValue(map)
    }

    private fun initObserver() {
        /**
         * 图片上传完成回调h5
         * @see AgentWebInterface.uploadImgData
         */
        viewModel._pic.observe(this, Observer {//图片上传的地址
            Log.e("UPIMG", it)
            quickCallJs(uploadImgCallback, it)
        })
        viewModel._location.observe(this, Observer {
            quickCallJs(getLocationCallback, it)
        })

    }

    private fun initWebViewSettings() {
        val mWebSettings = binding.webView.settings
        mWebSettings.setJavaScriptEnabled(true)
        mWebSettings.javaScriptCanOpenWindowsAutomatically = true
        mWebSettings.allowFileAccess = false
        mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        mWebSettings.setSupportZoom(true)
        mWebSettings.builtInZoomControls = true
        mWebSettings.useWideViewPort = true
        mWebSettings.setSupportMultipleWindows(true)
        mWebSettings.setAppCacheEnabled(true)
        mWebSettings.domStorageEnabled = true
        mWebSettings.setGeolocationEnabled(true)
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE)
        mWebSettings.pluginState = WebSettings.PluginState.ON_DEMAND
        mWebSettings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    private fun initWeb() {
        binding.webView.webChromeClient = mWebChromeClient
        binding.webView.webViewClient = object : com.tencent.smtt.sdk.WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (binding.webView.canGoBack()) {
                    headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_close).visibility =
                        View.VISIBLE
                } else {
                    headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_close).visibility =
                        View.GONE
                }
                loadingDialog?.dismiss()
            }


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: com.tencent.smtt.export.external.interfaces.WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false
                }
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    " Exception is ==== >>> $e".logE()
                }
                return true
            }

            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                loadingDialog?.show()
            }

            override fun onReceivedSslError(
                p0: WebView?,
                p1: com.tencent.smtt.export.external.interfaces.SslErrorHandler?,
                p2: com.tencent.smtt.export.external.interfaces.SslError?
            ) {
                p1?.proceed()
                super.onReceivedSslError(p0, p1, p2)
            }

            override fun onReceivedError(p0: WebView?, p1: Int, p2: String?, p3: String?) {
                super.onReceivedError(p0, p1, p2, p3)
                "webError${p2}".logE()
            }

        }
        binding.webView.apply {
            addJavascriptInterface(
                AgentWebInterface(
                    this,
                    this@AgentWebActivity,
                    this@AgentWebActivity
                ), "FORDApp"
            )
            initWebViewSettings()
            settings.userAgentString =
                "${settings.userAgentString} ford-evos"
        }
        AndroidBug5497Workaround.assistActivity(this)
        binding.webView.loadUrl(url)
    }

    var mWebChromeClient = object : com.tencent.smtt.sdk.WebChromeClient() {
        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissionsCallback?
        ) {
            var mSuper = this
            val permissions = Permissions.build(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            val success = {
                callback!!.invoke(origin, true, false)
            }
            val fail = {
                callback!!.invoke(origin, true, true)
            }
            PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            mUploadCallbackForHighApi = filePathCallback
            val intent = fileChooserParams!!.createIntent()
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
            } catch (e: ActivityNotFoundException) {
                mUploadCallbackForHighApi = null
                return false
            }
            return true

        }

        override fun openFileChooser(p0: ValueCallback<Uri>?, p1: String?, p2: String?) {
            p0?.let { openFilerChooser(it) }
        }

    }

    private fun afterFileChooseGoing(resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadCallbackForHighApi == null) {
                return
            }
            mUploadCallbackForHighApi?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            mUploadCallbackForHighApi = null
        } else {
            if (mUploadCallbackForLowApi == null) {
                return
            }
            val result = data?.data
            mUploadCallbackForLowApi?.onReceiveValue(result)
            mUploadCallbackForLowApi = null
        }
    }


    private fun openFilerChooser(uploadMsg: ValueCallback<Uri>) {
        mUploadCallbackForLowApi = uploadMsg
        startActivityForResult(
            Intent.createChooser(getFilerChooserIntent(), "File Chooser"),
            REQUEST_CODE_FILE_CHOOSER
        )
    }

    private fun getFilerChooserIntent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }


    private fun handleH5Back(): Boolean {
        if (!backEventCallBack.isNullOrEmpty()) {
            quickCallJs(backEventCallBack)
            return true
        }
        return false
    }

    private fun handleH5X(): Boolean {
        if (!closeEventCallBack.isNullOrEmpty()) {
            quickCallJs(closeEventCallBack)
            return true
        }
        return false
    }

    /**
     * 处理返回
     */
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (handleH5Back()) {
//            return true
//        }
//        if (binding.webView.canGoBack()) {
//            binding.webView.goBack()
//            return true
//        } else {
//            finish()
//        }
////        if (agentWeb.handleKeyEvent(keyCode, event)) {
////            return true
////        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onBackPressed() {
        if (handleH5Back()) {
            return
        }
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        } else {
            finish()
        }
        super.onBackPressed()
    }

    override fun onPause() {
        if (isOnPause) {
            binding.webView.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        binding.webView.onResume()
        quickCallJs("AppViewDidShow")
        super.onResume()
    }

    override fun onDestroy() {
        AgentWebConfig.clearDiskCache(this)
        binding.webView.destroy()
        totalWebNum -= 1
        super.onDestroy()
    }

    /**
     * 处理分享数据解析、
     */
    private fun handleShare(jsonStr: String) {
        val sharejson =
            JSON.parseObject(jsonStr)
        try {
            val isimg = sharejson.getString("isImg")
            val shareImg = sharejson["shareImg"] as String?
            val path: String? = null
            val bitmap: Bitmap? = null
            val shareTitle = sharejson["shareTitle"] as String?
            val shareDesc = sharejson["shareDesc"] as String?
            val shareUrl = sharejson["shareUrl"] as String?
            val shareType = sharejson["shareType"]
            val liknId = sharejson["liknId"]
            val shareActType = sharejson["shareActType"]
            val results = sharejson["results"]
            val isShareCallback = sharejson["isShareCallback"]
            val isMiniProgram = sharejson["isMiniProgram"]
            val type = sharejson["type"].toString()
            val bizId = sharejson["bizId"].toString()
            val shareWithType = sharejson["shareWithType"]?.toString()
            try {
                val prompt = sharejson.getBoolean("prompt")
            } catch (e: Exception) {
                e.printStackTrace()
                val prompt = true
            }
            shareBean = shareUrl?.let {
                shareImg?.let { it1 ->
                    shareTitle?.let { it2 ->
                        shareDesc?.let { it3 ->
                            ShareBean(
                                it,
                                it1,
                                it2,
                                it3,
                                type,
                                bizId,
                                isimg
                            )
                        }
                    }
                }
            }
            shareBean?.shareWithType = shareWithType
            shareBean?.let { shareViewModule.share(this, shareBean = it) }
        } catch (e: Exception) {

        }
    }

    /**
     * 设置标题
     */
    private fun setHeadTitle(title: String?) {
        headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_title).text = title
    }

    /**
     * 设置标题
     */
    private fun setTitleHide(hide: Boolean) {
        headerView.visibility = if (hide) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /**
     * var style = { text = "标题" color = "文字颜色" image = "图片地址 / 有标题再设置图片地址不会生效" }
     * @sample = {"text":"标题","color":"文字颜色","image":"图片地址 / 有标题再设置图片地址不会生效" }
     */
    private fun setStyle(jsonStr: String?) {
        val style: JSONObject
        try {
            style = JSONObject.parseObject(jsonStr)
        } catch (e: Exception) {
            headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).visibility = View.GONE
            headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more).visibility = View.GONE
            return
        }
        if (style.isNullOrEmpty())
            return
        val text: String = style["text"] as String
        val color = style["color"].toString()
        val image: String = style["image"] as String
        if (text.isNullOrEmpty()) {
            if (!image.isNullOrEmpty()) {
                //设置图片
                headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).visibility = View.GONE
                headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more).visibility = View.VISIBLE
                Glide.with(headerView).load(image)
                    .into(headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more))
            } else {
                headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).visibility = View.GONE
                headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more).visibility = View.GONE
            }
        } else {
            headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).visibility = View.VISIBLE
            headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more).visibility = View.GONE
            headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).text = text
            headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other)
                .setTextColor(Color.parseColor(color))
        }
        bindListener()
    }

    private fun bindListener() {
        headerView.findViewById<TextView>(com.changanford.common.R.id.bar_tv_other).setOnClickListener {
//            toastShow("点击文字")
            quickCallJs(subcallback)
        }
        headerView.findViewById<ImageView>(com.changanford.common.R.id.bar_img_more).setOnClickListener {
//            toastShow("点击图标")
            quickCallJs(subcallback)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {
            afterFileChooseGoing(resultCode, data)
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            val bundle = Bundle()
            bundle.putInt("index", 0)//权限
            when (requestCode) {
                REQUEST_CUT -> {//选择视频
//                    videoLocalMedia!!.path = data?.getStringExtra("cutPath");
//                    videoLocalMedia!!.androidQToPath = data?.getStringExtra("cutPath");
//                    videoLocalMedia!!.duration = (data?.getIntExtra("time", 1)!! * 1000).toLong()
//                    bundle.putParcelableArray("data", arrayOf(videoLocalMedia))
//                    bundle.putBoolean("isH5Post", true)
//                    bundle.putString("jsonStr", mjsonpost)
//                    startARouter(ARouterHomePath.HomePostActivity, bundle)
                }

                REQUEST_PIC -> {//选择图片
//                    selectList =
//                        data!!.getSerializableExtra("picList") as ArrayList<LocalMedia>
//                    bundle.putParcelableArray("data", selectList!!.toTypedArray())
//                    bundle.putBoolean("isH5Post", true)
//                    bundle.putString("jsonStr", mjsonpost)
//                    startARouter(ARouterHomePath.HomePostActivity, bundle)
                }

                SCAN_REQUEST_CODE -> {
                    data?.getStringExtra(SCAN_RESULT)?.let { quickCallJs(it) }
                }

                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    val base64Str = FileHelper.getImageStr(resultUri?.path)
                    quickCallJs(MConstant.carpWebCallBack, base64Str)
                }
            }
        }
        data?.extras?.apply { UnionPayUtils.payOnActivityResult(this) }
    }


    private lateinit var mjsonpost: String
    private var postType: String = ""
    public lateinit var h5callback: String

    fun scan() {
        val permissions = Permissions.build(
            Manifest.permission.CAMERA,
        )
        val success = {
            var bundle = Bundle()
            bundle.putBoolean("shouldCallback", true)
            startARouterForResult(
                this@AgentWebActivity,
                ARouterHomePath.CaptureActivity,
                bundle,
                SCAN_REQUEST_CODE,
                false
            )
        }
        val fail = {
            toastShow("没有获取到相机权限,请手动去设置页打开权限,或者重试授权权限")
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun clearPost() {
        viewModel.clearAllPost()
    }

    override fun jsCallback(key: Int, value: Any) {
        Thread {
            val msg = Message.obtain()
            msg.what = key
            msg.obj = value
            handle.sendMessage(msg)
        }.start()
    }

    private val handle = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            msg.let {
                when (it.what) {
                    //显示隐藏导航栏
                    0 -> setTitleHide(it.obj as Boolean)
                }
            }
        }
    }

}