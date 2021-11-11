package com.changanford.common.web


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.changanford.common.MyApp
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.ShareBean
import com.changanford.common.databinding.ActivityWebveiwBinding
import com.changanford.common.manger.UserManger
import com.changanford.common.pay.PayViewModule
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.CaptureActivity.SCAN_RESULT
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.totalWebNum
import com.changanford.common.util.SoftHideKeyBoardUtil
import com.changanford.common.util.bus.*
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toastShow
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener

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
class AgentWebActivity : BaseActivity<ActivityWebveiwBinding, AgentWebViewModle>() {

    private lateinit var shareViewModule: ShareViewModule //分享
    private lateinit var payViewModule: PayViewModule //支付

    //    private lateinit var mineSignViewModel: MineSignViewModel //获取个人信息，获取U享卡列表
    lateinit var headerView: View
    lateinit var agentWeb: AgentWeb
    var url: String = ""
    private var subcallback = "subcallback"//右上角图标文字点击时给h5的回调
    private var uploadImgCallback = ""//上传图片回调
    private var payCallback = ""//上传图片回调
    private var getLocationCallback = ""//获取经纬度回调
    private var shareCallBack = ""//分享回调
    private var shareBean: ShareBean? = null
    private var cacTokenCallBack = ""//获取(刷新)cacToken回调
    private var loginAppCallBack = ""//登录App回调
    private var bindPhoneCallBack = ""//绑定手机号回调
    private var backEventCallBack = ""//自定义返回
    private var chooseAddressCallback = ""//选择地址回调
    private var h5OrderPayCallback = ""//h5订单支付
    private var h5BindDealerCallback = ""//绑定经销商
    private var getMyInfoCallback = ""//获取用户信息回调
    private var getUniCardsListCallback = ""//获取用户U享卡信息回调
    private var showScanCallback = ""//扫码识别回调
    private var getCurVinCallback = ""//获取当前默认车辆vin
    private var addPlateNumCallback = ""//H5添加车牌回调
    private var jrsdkCallBack = "" //h5调起金融sdk回调

    //    public var postEntity: List<PostEntity>? = null//草稿
    private var setNavTitleKey: String = System.currentTimeMillis().toString()
    private var localWebNum = -1

    companion object {
        private const val REQUEST_PIC = 0x5431//图片
        private const val REQUEST_CUT = 0x531//剪切
        private const val SCAN_REQUEST_CODE: Int = 100//扫码
        var isOnPause = true
    }


    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.titleBar.commTitleBar, this)
        SoftHideKeyBoardUtil.assistActivity(this)
        totalWebNum += 1
        localWebNum = totalWebNum
        shareViewModule = createViewModel(ShareViewModule::class.java)
        payViewModule =
            createViewModel(PayViewModule::class.java)
//        mineSignViewModel =
//            ViewModelProvider(this, getFactoryProducer()).get(MineSignViewModel::class.java)
        headerView = findViewById(R.id.title_bar)
//        AppUtils.setStatusBarHeight(headerView,this)
        headerView.findViewById<ImageView>(R.id.bar_img_back).setOnClickListener {
            if (handleH5Back()) {
                return@setOnClickListener
            }
            if (agentWeb.webCreator.webView.canGoBack()) {
                agentWeb.webCreator.webView.goBack()
            } else {
                finish()
            }
        }
        headerView.findViewById<ImageView>(R.id.bar_img_close).setOnClickListener {
//            if (handleH5Back()) {
//                return@setOnClickListener
//            }
            finish()
        }

        registerLiveBus()
        initObserver()


//        PostDatabase.getInstance(this).getPostDao().findAll().observe(this,
//            Observer {
//                postEntity = it
//            })
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
            var map: HashMap<String, String> = it as HashMap<String, String>
            var jsonStr = map["jsonStr"].toString()
            shareCallBack = map["shareCallBack"].toString()
            handleShare(jsonStr)
        })
        //设置头部，全部
//        LiveDataBus.get().with(LiveDataBusKey.WEB_SET_NAV_TITLE).observe(this, Observer { it ->
//
//        })
        //设置头部，单独
        LiveDataBus.get().with(setNavTitleKey).observe(this, Observer { it ->
            var map = it as HashMap<String, String>
            setHeadTitle(map["title"])
            setStyle(map["style"])
            subcallback = map["subcallback"].toString()
        })
        //支付
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_PAY).observe(this, Observer {
            if (totalWebNum == localWebNum) {
                var map = it as HashMap<String, Any>
                var payCode = map["payCode"]
                var param = map["param"].toString()
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
                            agentWeb.jsAccessEntrace.quickCallJs(payCallback, "true")
                        }
                        false -> {
                            toastShow("支付失败")
                            agentWeb.jsAccessEntrace.quickCallJs(payCallback, "false")
                        }
                    }
                }
            })
        LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).observe(this,
            Observer {
                if (totalWebNum == localWebNum) {
                    when (it) {
                        0 -> {
                            agentWeb.jsAccessEntrace.quickCallJs(payCallback, "true")
                        }//成功
                        1 -> {
                            agentWeb.jsAccessEntrace.quickCallJs(payCallback, "false")
                        }//失败
                        2 -> {
                            agentWeb.jsAccessEntrace.quickCallJs(payCallback, "false")
                        }//取消
                    }
                }
            })


        //显示隐藏导航栏
        LiveDataBus.get().with(LiveDataBusKey.WEB_NAV_HID).observe(this, Observer {
            if (totalWebNum == localWebNum) {
                setTitleHide(it as Boolean)
            }
        })
//        //登录成功
//        LiveDataBus.get().with(LiveDataBusKey.WEB_LOGIN_SUCCESS).observe(this, Observer {
//            agentWeb.jsAccessEntrace.quickCallJs(it as String?)
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
            SoulPermission.getInstance()
                .checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    object : CheckRequestPermissionListener {
                        override fun onPermissionOk(permission: Permission?) {
                            if (JumpUtils.instans?.isOPen(this@AgentWebActivity) == true) {
                                viewModel.initLocationOption()
                            } else {
                                toastShow("手机没有打开定位权限,请手动去设置页打开权限")
                            }
                        }

                        override fun onPermissionDenied(permission: Permission?) {
                            toastShow("用户拒绝了权限")
                            agentWeb.jsAccessEntrace.quickCallJs(getLocationCallback, "false")
                        }

                    })
        })
        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {

                agentWeb.jsAccessEntrace.quickCallJs(shareCallBack, it.toString())
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
                        agentWeb.jsAccessEntrace.quickCallJs(loginAppCallBack)
                    }

                    //取消绑定手机号
                    UserManger.UserLoginStatus.USE_CANCEL_BIND_MOBILE -> {
                        agentWeb.jsAccessEntrace.quickCallJs(loginAppCallBack)
                        agentWeb.jsAccessEntrace.quickCallJs(bindPhoneCallBack, "false")
                    }
                    //绑定手机号成功
                    UserManger.UserLoginStatus.USE_BIND_MOBILE_SUCCESS -> {
                        agentWeb.jsAccessEntrace.quickCallJs(loginAppCallBack)
                        agentWeb.jsAccessEntrace.quickCallJs(bindPhoneCallBack, "true")
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
                        agentWeb.jsAccessEntrace.quickCallJs(chooseAddressCallback, it)
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
                    agentWeb.jsAccessEntrace.quickCallJs(h5OrderPayCallback, it.toString())
                })

        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_MYINFO, String::class.java).observe(this,
            Observer {
                getMyInfoCallback = it
                UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().getUser()
                    .observe(this, {
                        it?.toString()?.logE()
                        var user =
                            if (MConstant.token.isNullOrEmpty() || it.userJson.isNullOrEmpty()) {
                                ""
                            } else {
                                it.userJson
                            }
                        agentWeb.jsAccessEntrace.quickCallJs(getMyInfoCallback, user)
                    })
//                mineSignViewModel.getUserInfo()
            })
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
//                                agentWeb.jsAccessEntrace.quickCallJs(
//                                    getCurVinCallback,
//                                    response.data.carAuth!!.vin
//                                )
//                            } else {
//                                agentWeb.jsAccessEntrace.quickCallJs(getCurVinCallback, "")
//                            }
//                        } else {
//                            agentWeb.jsAccessEntrace.quickCallJs(getCurVinCallback, "")
//                        }
//                    }
//
//                    override fun onFail(e: ApiException) {
//                        agentWeb.jsAccessEntrace.quickCallJs(getCurVinCallback, "")
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
                agentWeb.jsAccessEntrace.quickCallJs(addPlateNumCallback, it.toString())
            })

        /**
         * 金融返回处理回调
         */
        LiveDataBus.get().with(LiveDataBusKey.JUMP_JR_BACK, String::class.java).observe(this,
            Observer {
                agentWeb.jsAccessEntrace.quickCallJs(jrsdkCallBack, it.toString())
            })
        /**
         * 砍价下单回调
         */
//        LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK).observe(this,  {
//            if("2"==it)this.finish()
//        })
    }

    override fun initData() {
        var data = intent.extras
        data?.let {
            var tempUrl = data.getString("value")
            if (!tempUrl.isNullOrEmpty()) {
                url = tempUrl
                Log.e("WEBURL", url)
            }
        }
        initWeb()
//        viewModel.getWxPayInfo()
//        viewModel.getAliPayInfo()
//        cacViewModule.getCACToken()
        LiveDataBus.get().with(LiveDataBusKey.H5POST_SUCCESS).observe(this, Observer {
            it?.let {
                agentWeb.jsAccessEntrace.quickCallJs(h5callback, it.toString())
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
            agentWeb.jsAccessEntrace.quickCallJs(uploadImgCallback, it)
        })
        viewModel._location.observe(this, Observer {
            agentWeb.jsAccessEntrace.quickCallJs(getLocationCallback, it)
        })
//        mineSignViewModel.uniUserInfo.observe(this, Observer {
//            try {
//                agentWeb.jsAccessEntrace.quickCallJs(getMyInfoCallback, JSON.toJSONString(it))
//            } catch (e: Exception) {
//                e.printStackTrace()
//                agentWeb.jsAccessEntrace.quickCallJs(getMyInfoCallback, it.toString())
//            }
//        })
    }


    private fun initWeb() {
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.llWebparent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setWebChromeClient(mWebChromeClient)
            .setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    "---url----$url".logE()
                    if (agentWeb.webCreator.webView.canGoBack()) {
                        headerView.findViewById<ImageView>(R.id.bar_img_close).visibility =
                            View.VISIBLE
                    } else {
                        headerView.findViewById<ImageView>(R.id.bar_img_close).visibility =
                            View.GONE
                    }
                }

                //忽略浏览器报错
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }
            })
            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //                .setWebLayout(new WebLayout(this))
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DERECT) //打开其他应用时，弹窗咨询用户是否前往其他应用
            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
            .createAgentWeb()
            .ready()
            .go(url)
        agentWeb.jsInterfaceHolder.addJavaObject(
            "FORDApp",
            AgentWebInterface(agentWeb, this@AgentWebActivity)
        )
        agentWeb.agentWebSettings.webSettings.javaScriptEnabled = true
        agentWeb.agentWebSettings.webSettings.mediaPlaybackRequiresUserGesture = false
        AndroidBug5497Workaround.assistActivity(this)
    }

    var mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            var mSuper = this
            SoulPermission.getInstance()
                .checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    object : CheckRequestPermissionListener {
                        override fun onPermissionOk(permission: Permission?) {
//                            mSuper.onGeolocationPermissionsShowPrompt(origin, callback)
                            callback!!.invoke(origin, true, false)
//                            toastShow("用户开启了权限")
                        }

                        override fun onPermissionDenied(permission: Permission?) {
//                            AlertDialog(this@AgentWebActivity)
//                                .builder()
//                                .setTitle("提示")
//                                .setMsg("您已禁止了定位权限，请到设置中心去打开")
//                                .setNegativeButton(
//                                    "取消"
//                                ) { }.setPositiveButton(
//                                    "确定"
//                                ) { SoulPermission.getInstance().goPermissionSettings() }.show()
                            callback!!.invoke(origin, true, true)

//                            mSuper.onGeolocationPermissionsShowPrompt(origin, callback)
//                            toastShow("用户拒绝了权限")
                        }

                    })
        }
    }


    private fun handleH5Back(): Boolean {
        if (!backEventCallBack.isNullOrEmpty()) {
            agentWeb.jsAccessEntrace.quickCallJs(backEventCallBack)
            return true
        }
        return false
    }

    /**
     * 处理返回
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (handleH5Back()) {
            return true
        }
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        if (isOnPause) {
            agentWeb.webLifeCycle.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        agentWeb.jsAccessEntrace.quickCallJs("AppViewDidShow")
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
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
            shareBean?.let { shareViewModule.share(this, shareBean = it) }
        } catch (e: Exception) {

        }
    }

    /**
     * 设置标题
     */
    private fun setHeadTitle(title: String?) {
        headerView.findViewById<TextView>(R.id.bar_tv_title).text = title
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
        var style: JSONObject
        try {
            style = JSONObject.parseObject(jsonStr)
        } catch (e: Exception) {
            headerView.findViewById<TextView>(R.id.bar_tv_other).visibility = View.GONE
            headerView.findViewById<ImageView>(R.id.bar_img_more).visibility = View.GONE
            return
        }
        if (style.isNullOrEmpty())
            return
        var text: String = style["text"] as String
        var color = style["color"].toString()
        var image: String = style["image"] as String
        if (text.isNullOrEmpty()) {
            if (!image.isNullOrEmpty()) {
                //设置图片
                headerView.findViewById<TextView>(R.id.bar_tv_other).visibility = View.GONE
                headerView.findViewById<ImageView>(R.id.bar_img_more).visibility = View.VISIBLE
                Glide.with(headerView).load(image)
                    .into(headerView.findViewById<ImageView>(R.id.bar_img_more))
            } else {
                headerView.findViewById<TextView>(R.id.bar_tv_other).visibility = View.GONE
                headerView.findViewById<ImageView>(R.id.bar_img_more).visibility = View.GONE
            }
        } else {
            headerView.findViewById<TextView>(R.id.bar_tv_other).visibility = View.VISIBLE
            headerView.findViewById<ImageView>(R.id.bar_img_more).visibility = View.GONE
            headerView.findViewById<TextView>(R.id.bar_tv_other).text = text
            headerView.findViewById<TextView>(R.id.bar_tv_other)
                .setTextColor(Color.parseColor(color))
        }
        bindListener()
    }

    private fun bindListener() {
        headerView.findViewById<TextView>(R.id.bar_tv_other).setOnClickListener {
//            toastShow("点击文字")
            agentWeb.jsAccessEntrace.quickCallJs(subcallback)
        }
        headerView.findViewById<ImageView>(R.id.bar_img_more).setOnClickListener {
//            toastShow("点击图标")
            agentWeb.jsAccessEntrace.quickCallJs(subcallback)
        }
    }

    //    private var videoLocalMedia: LocalMedia? = null//视频媒体
//    private var selectList: ArrayList<LocalMedia>? = null//选择的媒体
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                    agentWeb.jsAccessEntrace.quickCallJs(data?.getStringExtra(SCAN_RESULT))
                }

            }
        }
    }


    private lateinit var mjsonpost: String
    private var postType: String = ""
    public lateinit var h5callback: String
    fun openGallery_onlyimg(type: String, jsonStr: String, back: String) {
        mjsonpost = jsonStr
        postType = type
        h5callback = back
//        PictureUtil.openGarlly5(
//            this,
//            object : OnResultCallbackListener<LocalMedia> {
//                override fun onResult(result: MutableList<LocalMedia>) {
//                    if (!result.isNullOrEmpty()) {
//                        val bundle = Bundle()
//                        if (result[0].mimeType.contains("video")) {//视频
//                            videoLocalMedia = result[0]
//                            bundle.putString(
//                                "path",
//                                AppUtils.getFinallyPath(videoLocalMedia)
//                            )
//                            startARouterForResult(
//                                this@AgentWebActivity,
//                                ARouterCirclePath.EsayVideoEditActivity,
//                                bundle,
//                                REQUEST_CUT
//                            )
//                        } else {
//                            bundle.putInt("position", 0)
//                            bundle.putInt("showEditType", 1)
//                            bundle.putParcelableArrayList(
//                                "picList",
//                                arrayListOf(*result.toTypedArray())
//                            )
//                            startARouterForResult(
//                                this@AgentWebActivity,
//                                ARouterCirclePath.PictureeditlActivity,
//                                bundle,
//                                REQUEST_PIC
//                            )
//                        }
//                    }
//
//                }
//
//                override fun onCancel() {
//
//                }
//            })
    }

    fun openGallery_onlyvideo(type: String, jsonStr: String, back: String) {
        mjsonpost = jsonStr
        postType = type
        h5callback = back
//        PictureUtil.openGarlly3(
//            this,
//            object : OnResultCallbackListener<LocalMedia> {
//                override fun onResult(result: MutableList<LocalMedia>) {
//                    if (!result.isNullOrEmpty()) {
//                        val bundle = Bundle()
//                        if (result[0].mimeType.contains("video")) {//视频
//                            videoLocalMedia = result[0]
//                            bundle.putString(
//                                "path",
//                                AppUtils.getFinallyPath(videoLocalMedia)
//                            )
//                            startARouterForResult(
//                                this@AgentWebActivity,
//                                ARouterCirclePath.EsayVideoEditActivity,
//                                bundle,
//                                REQUEST_CUT
//                            )
//                        } else {
//                            bundle.putInt("position", 0)
//                            bundle.putInt("showEditType", 1)
//                            bundle.putParcelableArrayList(
//                                "picList",
//                                arrayListOf(*result.toTypedArray())
//                            )
//                            startARouterForResult(
//                                this@AgentWebActivity,
//                                ARouterCirclePath.PictureeditlActivity,
//                                bundle,
//                                REQUEST_PIC
//                            )
//                        }
//                    }
//
//                }
//
//                override fun onCancel() {
//
//                }
//            })
    }

    fun scan() {
        SoulPermission.getInstance().checkAndRequestPermission(
            Manifest.permission.CAMERA, object : CheckRequestPermissionListener {
                override fun onPermissionOk(permission: Permission?) {
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

                override fun onPermissionDenied(permission: Permission?) {
                    toastShow("没有获取到相机权限,请手动去设置页打开权限,或者重试授权权限")
                }
            }
        )
    }

    fun clearPost() {
        viewModel.clearAllPost()
    }

//    fun getBindMobileJumpDataType(): Boolean {
//        return viewModel.getBindMobileJumpDataType()
//    }
}