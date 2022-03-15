package com.changanford.common.web

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.changanford.common.BuildConfig
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.H5PostTypeBean
import com.changanford.common.bean.MediaListBean
import com.changanford.common.listener.OnDownBitmapListener
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterCarControlPath
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.SelectPostDialog
import com.changanford.common.util.*
import com.changanford.common.util.JumpUtils.Companion.instans
import com.changanford.common.util.bus.*
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.changanford.common.widget.BindingPhoneDialog
import com.changanford.common.wutil.WCommonUtil
import com.just.agentweb.AgentWeb
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*
import kotlin.collections.set


/**
 * H5调用原生方法
 */
class AgentWebInterface(var agentWeb: AgentWeb, var activity: AgentWebActivity?) {

    /**
     * 是否在APP中
     * @return
     */
    @get:JavascriptInterface
    val isInApp: Boolean
        get() = true

    /**
     * AES加密
     * @param content = 需要加密的字符串参数
     * @param key = 16位随机字符串key
     * @return 加密后的字符串
     */
    @JavascriptInterface
    fun encryption(content: String, key: String): String {
        return aesEncrypt(content, key)
    }

    /**
     * @param key = 16位随机字符串key
     */
    @JavascriptInterface
    fun encryKey(key: String) = rsaEncrypt(key)

    /**
     * AES解密
     * @param content = 需要解密的字符串
     * @param key = 16位随机字符串key
     * @return 解密后的字符串
     */
    @JavascriptInterface
    fun decrypt(content: String, key: String): String = decryResult(content, key)

    /**
     * @param content = 需要签名的字符串，不带特殊字符
     * @param type =  String: 0埋点签名  1为其他接口签名
     */
    @JavascriptInterface
    fun sign(
        content: String,
        type: String
    ): String {
        if (content.isNullOrEmpty()) {
            return "加密内容或类型字符串为空"
        }
        if (type == "0") {
            return signMD(content)
        }
        return sign(content)
    }

    /**
     * 获取用户token
     * @return
     */
    @get:JavascriptInterface
    val token: String
        get() = MConstant.token


    /**
     * 关闭webview
     * type: 默认不传关闭所有h5的webview， 传1关闭当前webview
     */
    @JavascriptInterface
    fun closePage() {
        activity?.finish()
    }

    /**
     * @param
     */
    @JavascriptInterface
    fun closePage(i: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_CLOSEPAGE).postValue(i)
    }

    /**
     * 跳转APP方法
     * @param jsonStr = {"type":1,"value":"xxx"})
     *
     */
    @JavascriptInterface
    fun openPage(jsonStr: String?) {
//        toastShow("打开页面: ".plus(jsonStr))
        try {
            val jsonObject = JSON.parseObject(jsonStr)
            val type = jsonObject.getIntValue("jumpDataType")
            val value = jsonObject.getString("jumpDataValue")
            instans!!.jump(type, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取设备信息
     * {"deviceId": kUUID(),"brand": "苹果","system":"Android","model": UIDevice.current.currentDeviceModel()}
     *
     *
     */
    @JavascriptInterface
    fun getDeviceInfo(): String {
        var js: JSONObject = JSONObject()
        js["deviceId"] = DeviceUtils.getUUID()
        js["brand"] = DeviceUtils.getDeviceBRAND()
        js["system"] = "Android"
        js["model"] = DeviceUtils.getDeviceModel()
        return js.toJSONString()
    }


    /**
     * 跳转到app登陆界面
     * @param logincallback 登陆成功回调
     */
    @JavascriptInterface
    fun loginApp(logincallback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_LOGIN_APP).postValue(logincallback)
        instans!!.jump(100, "")
    }

    /**
     * 上传图片
     * @param img base64的图片
     * @return 上传后的http连接
     *
     */
    @JavascriptInterface
    fun uploadImgData(img: String, callback: String) {
//        toastShow("上传图片")
        var map = HashMap<String, String>()
        map["img"] = img
        if (!img.isNullOrEmpty()) {
            var newImg = img.replace("data:image/png;base64,", "")
            map["img"] = newImg
        }
        map["callback"] = callback
        LiveDataBus.get().with(LiveDataBusKey.WEB_UPLOAD_IMG).postValue(map)
    }

    /**
     * 选择一张图片
     * @param callback，传回一张图片的base64串
     */
    @JavascriptInterface
    fun choseImg(callback: String) {
//        toastShow("选择图片")
        PictureUtils.openGarlly(500,
            activity,
            object : OnResultCallbackListener<LocalMedia?> {
                override fun onCancel() {}
                override fun onResult(result: MutableList<LocalMedia?>?) {
                    if (result != null) {
                        for (media in result) {
                            var path = ""
                            media?.let {
                                path = if (media.isCut && !media.isCompressed) {
                                    // 裁剪过
                                    media.cutPath
                                } else if (media.isCompressed || media.isCut && media.isCompressed) {
                                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                                    media.compressPath
                                } else {
                                    // 原图
                                    media.path
                                }
                            }
                            path?.let {
                                var base64Str = FileHelper.getImageStr(path)
                                agentWeb.jsAccessEntrace.quickCallJs(callback, base64Str)
                            }
                        }
                    }
                }
            })
    }

    /**
     * 复制
     * 将文本内容放到系统剪贴板里
     * @param content = 复制的内容
     */
    @JavascriptInterface
    fun copyText(content: String) {
//        toastShow("复制内容成功: ".plus(content))
        var manager: ClipboardManager =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.text = content
    }

    /**
     * 分享
     * @param jsonStr = 分享的json内容
     * @sample { shareTitle:"", // 分享标题 shareUrl:"", // 分享链接 shareImg:"", // 分享icon shareDesc:"", // 分享描述 shareType: 1, // 1:好友, 2:朋友圈 3:好友和朋友圈 isImg: "0", // 是否为分享纯图片（1 是 0 否）
     * isMiniProgram: "1", // 是否分享微信小程序（可选）}
     * TODO
     */
    @JavascriptInterface
    fun shareTo(jsonStr: String, shareCallBack: String) {
//        toastShow("分享的内容".plus(jsonStr))
        var map = HashMap<String, String>()
        map["jsonStr"] = jsonStr
        map["shareCallBack"] = shareCallBack
        LiveDataBus.get().with(LiveDataBusKey.WEB_SHARE).postValue(map)
    }

    /**
     * 头部设置
     * @param title = 标题
     * @param @sample style = { text = "标题" color = "文字颜色" image = "图片地址 / 有标题再设置图片地址不会生效" }
     */
    @JavascriptInterface
    fun setNavTitle(title: String, style: String, subcallback: String) {
//        toastShow("头部设置".plus(title))
        var map = HashMap<String, String>()
        map["title"] = title
        map["style"] = style
        map["subcallback"] = subcallback
        activity?.setNavTitle(map)
//        LiveDataBus.get().with(LiveDataBusKey.WEB_SET_NAV_TITLE).postValue(map)
    }

    /**
     * 支付
     * @sample = payCode: 1-支付宝， 2-微信 * param: 支付字符串 * callback：支付成功回调
     */
    @JavascriptInterface
    fun openPay(payCode: Int, param: String, callback: String) {
//        toastShow("支付".plus(param))
        var map = HashMap<String, Any>()
        map["payCode"] = payCode
        map["param"] = param
        map["callback"] = callback
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_PAY).postValue(map)
    }


    /**
     * 显示需要隐藏导航栏
     *
     */
    @JavascriptInterface
    fun isNavigationHidden(bool: Boolean) {
//        toastShow("显示需要隐藏导航栏".plus(bool))
        LiveDataBus.get().with(LiveDataBusKey.WEB_NAV_HID).postValue(bool)
    }

    /**
     * 缓存到本地一个json
     */
    @JavascriptInterface
    fun setSessionStorage(key: String, value: String) {
        SPUtils.setParam(BaseApplication.INSTANT, key, value)
    }

    /**
     * 从本地读取缓存
     */
    @JavascriptInterface
    fun getSessionStorage(key: String): String {
        return SPUtils.getParam(BaseApplication.INSTANT, key, "").toString()
    }

    /**
     * 删除本地某条缓存
     */
    @JavascriptInterface
    fun delegateSessionStorage(key: String) {
        SPUtils.clearByKey(key)
    }

    /**
     * 获取经纬度
     * <!-- 返回json字符串 -->
    {"address":"","latitude": "29.558115","longitude":"106.579987"}
     */
    @JavascriptInterface
    fun getLocation(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_LOCATION).postValue(callback)
    }

    /**
     * 获取刘海高度
     */
    @JavascriptInterface
    fun getStatusBarHeight(): Float {
        return DisplayUtil.getStatusBarHeight(activity)
    }

    /**
     * 绑定手机号,返回"true"成功，"false"失败
     */
    @JavascriptInterface
    fun bindPhone(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_BIND_PHONE).postValue(callback)
        instans!!.jump(18, "")
    }

    /**
     * 绑定手机号,返回"true"成功，"false"失败
     * @param toast = 没有弹框toast =“0”,有弹框toast =“1”
     */
    @JavascriptInterface
    fun bindPhone(toast: String, callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_BIND_PHONE).postValue(callback)
        if (toast == "1") {
            BindingPhoneDialog(activity!!).show()
        } else {
            instans!!.jump(18, "")
        }
    }

    /**
     * 自定义返回按钮事件
     * @param jumpData 需json化 :  {"jumpDataType": x, "jumpDataValue": x}
     * @param callback：回调方法名字 如果回调方法需要参数 请自己拼接好 e·g： xxxx(a)~~~~
     */
    @JavascriptInterface
    fun setBackEvent(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_BACKEVENT).postValue(callback)
    }


    /**
     * H5选择地址，返回地址Josn数据
     */
    @JavascriptInterface
    fun selectAddress(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_CHOOSE_ADDRESS, String::class.java)
            .postValue(callback)
        instans?.jump(20, "1")
    }

    /**
     * H5订单支付
     */
    @JavascriptInterface
    fun openPayController(orderNo: String, callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_ORDER_PAY, String::class.java)
            .postValue(callback)
        var b = Bundle()
        b.putInt("type", 2)//H5支付
        b.putString("value", orderNo)//订单号
        startARouter(ARouterMyPath.OrderPayUI, b)
    }

    @JavascriptInterface
    fun showError404() {
        startARouter(ARouterCarControlPath.NothingActivity)
        activity?.finish()
    }

    // postType: 帖子类型 0-正常贴子  1-视频帖子
    // {"circleId": 1, "circleName": "圈子名字", "topicId": 1, "topicName": "话题名字", “ext”: "activityId"}
    @JavascriptInterface
    fun gotoPost(postType: String, param: String, callback: String) {

        when {
            MConstant.token.isEmpty() -> {
                startARouter(ARouterMyPath.SignUI)
            }
//            activity!!.getBindMobileJumpDataType() -> {
//                BindingPhoneDialog(context!!).show()
//            }
            else -> {

                postType.plus("参数$param").logE()
                val b = Bundle()
                b.putBoolean("isH5Post", true)
                b.putInt("postType", postType.toInt())
                b.putString("jsonStr", param)
                var  h5PostTypeBean = JSON.parseObject(param, H5PostTypeBean::class.java)

                if(h5PostTypeBean.circleId=="0"){
                    b.putBoolean("isCirclePost",false)
                }else{
                    b.putBoolean("isCirclePost",true)
                    b.putString("circleId",h5PostTypeBean.circleId)
                    b.putString("circleName",h5PostTypeBean.circleName)
                }
                if(h5PostTypeBean.topicId=="0"){
                    b.putBoolean("isTopPost",false)
                }else{
                    b.putBoolean("isTopPost",true)
                    b.putString("topicId",h5PostTypeBean.topicId)
                    b.putString("topName",h5PostTypeBean.topicName)
                }

                when (postType) {
                    "0" -> {
                        startARouter(ARouterCirclePath.LongPostAvtivity, b, true)
                    }
                    "1" -> {
                        startARouter(ARouterCirclePath.PostActivity, b, true)

                    }
                    "2" -> {
                        startARouter(ARouterCirclePath.VideoPostActivity, b, true)
                    }
                    else -> {
                        showPostDialog(b)
                    }
                }


//                startARouter(ARouterHomePath.HomePostActivity, json.toJSONString())
//                if (activity?.postEntity.isNullOrEmpty()) {
//                    if (postType == "0") activity?.openGallery_onlyimg(
//                        postType,
//                        param,
//                        callback
//                    ) else activity?.openGallery_onlyvideo(
//                        postType,
//                        param,
//                        callback
//                    )
//                } else {
//                    AlertDialog(activity).builder().setGone().setMsg("发现您有草稿还未发布")
//                        .setNegativeButton("继续编辑") {
//                            val b = Bundle()
//                            b.putBoolean("isH5Post", true)
//                            b.putInt("postType", postType.toInt())
//                            var json = JSONObject();
//                            json["circleId"] = activity?.postEntity!![0].circleId ?: "0"
//                            json["circleName"] = activity?.postEntity!![0].circleName ?: ""
//                            json["topicId"] = activity?.postEntity!![0].topicId ?: "0"
//                            json["topicName"] = activity?.postEntity!![0].topicName ?: ""
//                            var p = JSON.parseObject(param)
//                            json["ext"] = p["ext"]
//                            b.putString("jsonStr", json.toJSONString())
//                            b.putSerializable("postEntity", activity?.postEntity!![0])
//                            activity?.h5callback = callback
//                            startARouter(ARouterHomePath.HomePostActivity, b)
//                        }.setPositiveButton("不使用草稿") {
//                            activity?.clearPost()
//                            if (postType == "0") activity?.openGallery_onlyimg(
//                                postType,
//                                param,
//                                callback
//                            ) else activity?.openGallery_onlyvideo(postType, param, callback)
//                        }.show()
//                }
            }
        }
//        BuriedUtil.instant!!.click_fatie()
    }
    /**
     *
     */
    fun showPostDialog(b: Bundle) {

        try {
            SelectPostDialog(BaseApplication.curActivity, object : SelectPostDialog.CheckedView {
                override fun postLong() {
                    startARouter(ARouterCirclePath.LongPostAvtivity, b, true)
                }

                override fun postPics() {
                    startARouter(ARouterCirclePath.PostActivity, b, true)
                }

                override fun postVideo() {
                    startARouter(ARouterCirclePath.VideoPostActivity, b, true)
                }
            }).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取用户信息
     */
    @JavascriptInterface
    fun getMyInfo(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_MYINFO, String::class.java)
            .postValue(callback)
    }

    /**
     * 获取用户U享卡信息
     */
    @JavascriptInterface
    fun getUniCardsList(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_GET_UNICARDS_LIST, String::class.java)
            .postValue(callback)
    }

    /**
     * 扫码识别
     */
    @JavascriptInterface
    fun showScan(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.WEB_SHOW_SCAN, String::class.java)
            .postValue(callback)
    }

    /**
     * 保存一张网络图片
     * callback,返回1成功，0失败
     */
    @JavascriptInterface
    fun saveImg(url: String, callback: String) {

        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        try {
                            if (url.contains(".gif")) {
                                GifUtils.saveGif(
                                    url,
                                    activity,
                                    MConstant.ftFilesDir + "/" + System.currentTimeMillis() + ".gif"
                                )
                            } else {
                                Glide.with(activity!!).asBitmap().load(url)
                                    .into(object : SimpleTarget<Bitmap?>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap?>?
                                        ) {
                                            PictureUtil.saveBitmapPhoto(resource)
                                        }
                                    })
                            }
                            "保存成功".toast()
                            agentWeb.jsAccessEntrace.quickCallJs(callback, "1")
                        } catch (e: java.lang.Exception) {
                            "保存失败".toast()
                            agentWeb.jsAccessEntrace.quickCallJs(callback, "0")
                        }
                    }

                    override fun onPermissionDenied(permission: Permission) {}
                })
    }

    /**
     * 获取当前默认车辆的vin
     */
    @JavascriptInterface
    fun getCurVin(callback: String) {
        LiveDataBus.get().with(LiveDataBusKey.GET_CUR_VIN, String::class.java)
            .postValue(callback)
    }

    @JavascriptInterface// _index = "0"
    fun showPhotoBrowser(_imgs: String, _index: String) {
        val bundle = Bundle()
        var imageUrls: JSONArray = JSON.parseArray(_imgs)
        bundle.putStringArray("imageUrls", imageUrls.toArray(arrayOfNulls<String>(imageUrls.size)))
        val mediaListBeans: MutableList<MediaListBean> = ArrayList()
        var count = 0
        if (imageUrls.isNotEmpty()) {
            for (i in imageUrls.indices) {
                val mediaListBean = MediaListBean()
                mediaListBean.img_url = imageUrls[i] as String
                mediaListBeans.add(mediaListBean)
            }
        }
        var img = imageUrls[(_index ?: "0").toString().toInt()] as String
        bundle.putSerializable("imgList", mediaListBeans as Serializable?)
        bundle.putString("curImageUrl", img)
        bundle.putInt("count", count)
        ARouter.getInstance().build(ARouterCirclePath.PhotoViewActivity).with(bundle).navigation()
    }

    /**
     * H5调用车牌修改
     */
    @JavascriptInterface
    fun showPlateAlertView(plateNum: String, plateNumCallback: String) {
//        LiveDataBus.get().with(MINE_ADD_PLATE_NUM, String::class.java).postValue(plateNumCallback)
//        var bundle = Bundle()
//        bundle.putString("plateNum", plateNum)
//        startARouter(ARouterMyPath.AddCardNumTransparentUI, bundle)
    }


    /**
     * H5通过传入入参和url相对路径请求原生接口
     * @param body: 入参
     * @param url : 相对路径如：/base/config/getConfigValueByKey
     * @param callBack : 回调函数名
     */
    @JavascriptInterface
    fun getUrlResult(body: String, url: String, callBack: String) {
        var map: Map<String, Any> = try {
            JSON.parseObject(body) as Map<String, Any>
        } catch (e: Exception) {
            HashMap()
        }
        activity!!.lifecycleScope.launch(Dispatchers.IO) {
//            val str = H5Request.getInstance()
//                .getData(BaseApplication.Url + url, map)
//            launch(Dispatchers.Main) {
////                Log.e("H5Request", str)
//                agentWeb.jsAccessEntrace.quickCallJs(callBack, str)
//            }
        }
    }

    /**
     * 获取系统版本号
     */
    @JavascriptInterface
    fun getSystemVersion() = DeviceUtils.getDeviceVersion()

    /**
     * 获取App版本 1.1.1
     */
    @JavascriptInterface
    fun getAppVersion() = DeviceUtils.getversionName()

    /**
     * 设置状态栏字体的颜色
     * color 0是白，1是黑
     */
    @JavascriptInterface
    fun setStatusBarTextColor(color: Int) {
        activity!!.lifecycleScope.launch {
            StatusBarUtil.setLightStatusBar(activity, color != 0)
        }
    }

    /**
     * 设置状态栏是否透明 不透明默认白色
     * [isTransparent]是否透明
     */
    @JavascriptInterface
    fun setStatusBarIsTransparent(isTransparent: Boolean) {
        activity?.lifecycleScope?.launch {
            StatusBarUtil.setStatusBarColor(
                activity,
                if (isTransparent) com.changanford.common.R.color.transparent else R.color.white
            )
        }
    }
    /**
     * 打开PDF文件
     */
    @JavascriptInterface
    fun opePdf(pdfUrl:String) {
        JumpUtils.instans?.jump(1,"http://mozilla.github.io/pdf.js/web/viewer.html?file=$pdfUrl")
    }
    /**
     * 小程序分享
     * [webpageUrl]兼容低版本的网页链接 限制长度不超过 10KB
     * [miniprogramType]正式版:0，测试版:1，体验版:2
     * [userName]小程序原始id（gh_d43f693ca31f）
     * [path]小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
     * [title] 小程序消息title
     * [description]小程序消息desc
     * * thumbData 小程序消息封面图片，小于128k ByteArray(byte[])
     * [imgPath]小程序消息封面图片，imgPath 图片路径 没有传 null
     * [shareCallBack]回调
     */
    @JavascriptInterface
    fun shareSmallProgram(webpageUrl:String,miniprogramType:Int,userName:String,path:String,title:String,description:String,imgPath:String?,shareCallBack:String) {
        if(BuildConfig.DEBUG){
            Log.e("wenke","小程序分享：webpageUrl：$webpageUrl>>>miniprogramType:$miniprogramType>>>userName:$userName>>>path:$path")
            Log.e("wenke","小程序分享：title：$title>>>description:$description>>>imgPath:$imgPath》》》shareCallBack:$shareCallBack")
        }
        activity?.apply {
            if(!TextUtils.isEmpty(imgPath)){
                WCommonUtil.pathUrlToBitmap(this,imgPath!!,object :OnDownBitmapListener{
                    override fun onFinish(bitmap: Bitmap) {
                        val thumbData=WCommonUtil.bitmap2Bytes(bitmap)
                        LiveDataBus.get().with(LiveDataBusKey.WEB_SMALL_PROGRAM_WX_SHARE).postValue(shareCallBack)
                        WCommonUtil.shareSmallProgram(this@apply, webpageUrl,miniprogramType, userName,path,title,description,thumbData)
                    }
                })
            }else{
                val bmp = BitmapFactory.decodeResource(resources, R.mipmap.fordicon)
                val thumbData=WCommonUtil.bitmap2Bytes(bmp)
                LiveDataBus.get().with(LiveDataBusKey.WEB_SMALL_PROGRAM_WX_SHARE).postValue(shareCallBack)
                WCommonUtil.shareSmallProgram(this, webpageUrl,miniprogramType, userName,path,title,description,thumbData)
            }

        }
    }
    /**
     * 银联支付
     * [payType]支付类型 1支付宝、2 微信、3云闪付
     * [appPayRequest]拉起支付的参数（具体参考对应文档）
     * [callback]支付回调
     * [serverMode] 云闪付使用 为后台环境标识，不传或者null默认使用“00”生产环境
     */
    @JavascriptInterface
    fun openUnionPay(payType: Int, appPayRequest: String, callback: String) {
        openUnionPay(payType,appPayRequest,callback,null)
    }
    @JavascriptInterface
    fun openUnionPay(payType: Int, appPayRequest: String, callback: String,serverMode:String?) {
        if(BuildConfig.DEBUG)Log.d("wenke","H5调用银联支付：payType:$payType>>>appPayRequest:$appPayRequest>>>callback:$callback")
        val map = HashMap<String, Any>()
        map["payType"] = payType
        map["appPayRequest"] = appPayRequest
        map["callback"] = callback
        map["serverMode"] = serverMode?:"00"
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_UNION_PAY).postValue(map)
    }
    /**
     * 打开相机
    * */
    @JavascriptInterface
    fun openCamera(callback:String){
        PictureUtils.opencarcme(activity, object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                if (result.isNotEmpty()) {
                    for (media in result) {
                        val path: String = PictureUtil.getFinallyPath(media)
                        path.let {
                            val base64Str = FileHelper.getImageStr(path)
                            agentWeb.jsAccessEntrace.quickCallJs(callback, base64Str)
                        }
                    }
                }
            }
            override fun onCancel() {}
        })
    }
}