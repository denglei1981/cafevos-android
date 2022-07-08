package com.changanford.common.util

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseApplication.Companion.currentViewModelScope
import com.changanford.common.bean.*
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.constant.JumpConstant
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.GetCoupopBindingPop
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.ui.dialog.SelectMapDialog
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.web.ShareViewModule
import com.google.gson.Gson
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.launch


/**
 * Created by ${zy} on 2018/8/15.
 * type | value | 说明
-----|-----|-----
'99不跳转',
'100' |=> |'登录页面',
'101' |=> |'发现',
'102' |=> |'社区',
'103' |=> |'爱车',
'104' |=> |'商城',
'105' |=> |'我的',
'106'  |h5链接带参数 |'H5页面'（需要登录）,
'107'  |h5链接带参数 |'H5页面'（不仅登录需要绑定手机号）,
'10000H5外部跳转(打开浏览器)',
'1'   |h5链接或参数 |'H5页面',
'2'   |=> |'资讯详情',
'3'   |=> |'商品详情',
'4'   |=> |'帖子详情',
'5'   |=> |商城订单详情,
'6    | => | '圈子详情',
'7'    | => | '专题列表',
'8专题详情',
'9话题详情',
'10成长值详情',
'11'   |{"tagId": "28","content": "订单号"}|'填写意见反馈'
'12去发布调查',
'13去发布活动',
'14去发帖',
'16任务中心,
'17车主认证',
'18绑定手机号',
'19小程序',
'20地址管理',
'21设置',
'22' |=>|会员身份,
'23我的发布',
'24我的消息',
'25我的关注',
'26我的活动',
'27我的收藏',
'28我的圈子',
'29我的勋章,
'30积分纪录,
'32‘成长值',
'34用户个人信息页面,
'35他人主页',
'36聚合订单列表页,
'37签到（当前页面弹出签到成功弹框，若已签到提示语后台返回）
'38我的足迹
'39常见问题
'40我的粉丝
'42意见反馈记录
'43公民认证详情页
'44邀请新用户
'45'  |wonderid |编辑已发布的活动
'46'  |queryId |问卷编辑
'47'  |messageType(1->系统消息 2->互动消息 3->交易消息) |消息列表
'48 ' | =>| 秒杀列表
'49 ' | uniCardId |跳转到购买某类U享卡页面
'50 ' | =>｜跳转车主认证详情
'51 ' |=>｜跳转到U享卡片切换页面,（为其他车辆购买U享卡）
'52'|=>|商城订单列表
'55'|=>|月签到详情
'59'|toast消息|弹出一个toast内容
'60'|{"content":"弹框的内容","buttons":[{"btName":"按钮内容","jumpData":{"jumpDataType":"xx","jumpDataValue":"xx"}}]}|弹出一个弹框，内容，按钮及跳转在value中返回
'61'|=>|扫一扫（1.1.1版本）
'69'|经纬度和名称{"latY":39.916527,"lngX":116.397128,"name":"北京市天安门"}|跳转到选择高德百度地图弹框，逻辑和专属经销商一致(1.1.7版本)
'71'|=>|跳转新版意见反馈页面
'73'|=>|需要位置权限的H5
 */
class JumpUtils {

    fun jumpLogin() {

    }

    fun jump(jumpData: JumpDataBean?) {
        if (jumpData == null) {
            return
        }
        jump(jumpData.jumpDataType, jumpData.jumpDataValue)

    }

    fun jump(type: Int?, value: String? = "") {
        if (type == null || type == 0) {
            return
        }
        val bundle = Bundle()
        bundle.putString("value", value)
        when (type) {
            1 -> {//H5页面
                Log.i("h5", value ?: "")
                startARouter(ARouterHomePath.AgentWebActivity, bundle)
            }

            2 -> {//资讯详情
//                val gson = Gson()
//                val newsValueData = gson.fromJson(value, NewsValueData::class.java)
//                gotoNewsDetails(newsValueData)
                bundle.putString("artId", value)
                startARouter(ARouterHomePath.InfoDetailActivity, bundle)
            }
            3 -> {//商品详情
                if (value != null) {
                    bundle.putString("spuId", value)
//                    val json = JSON.parseObject(value)
//                    bundle.putString("spuId", json.getString("spuId"))
//                    bundle.putString("spuPageType", json.getString("spuPageType"))
                    startARouter(ARouterShopPath.ShopGoodsActivity, bundle)
                }
            }
            4 -> {//帖子详情
                bundle.putString("postsId", value)
                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            }
            5 -> {//商城订单详情
                if (value != null) {
                    bundle.putString("orderNo", value)
                }
                startARouter(ARouterShopPath.OrderDetailActivity, bundle, true)
            }
            6 -> {//圈子详情
                bundle.putString("circleId", value)
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }
            7 -> {//专题列表
                startARouter(ARouterHomePath.SpecialListActivity, bundle)
            }
            8 -> {//专题详情
                bundle.putString(JumpConstant.SPECIAL_TOPIC_ID, value)
                startARouter(ARouterHomePath.SpecialDetailActivity, bundle)
            }
            9 -> {//话题详情
                bundle.putString("topicId", value)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            10 -> {//成长值详情
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    RouterManger.needLogin(true).startARouter(ARouterMyPath.MineTaskListUI)
                }
            }
            11 -> {//填写意见反馈
                startARouter(ARouterMyPath.MineEditFeedbackUI, bundle)
            }
            12 -> {//去发布调查
                startARouter(ARouterCirclePath.ReleaseUpActivity, true)
            }
            13 -> {//去发布活动',
                startARouter(ARouterCirclePath.releasactivity, true)
            }
            14 -> {//去发帖',
                jump(102)
            }
            15 -> {//2级评论''

            }
            16 -> {//任务中心,
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    RouterManger.needLogin(true).startARouter(ARouterMyPath.MineTaskListUI)
                }
            }
            17 -> {//车主认证',
                when {
                    MConstant.token.isNullOrEmpty() -> {
                        startARouter(ARouterMyPath.SignUI)
                    }
                    MineUtils.getBindMobileJumpDataType() -> {
                        startARouter(ARouterMyPath.MineBindMobileUI)
                    }
                    else -> {
//                        startARouter(ARouterMyPath.UniCarAuthUI)
                        toCarAuth()
                    }
                }
            }
            18 -> {//绑定手机号',
                startARouter(ARouterMyPath.MineBindMobileUI, true)

            }
            19 -> {//小程序',
                if (MConstant.token.isNullOrEmpty()) {
                    startARouter(ARouterMyPath.SignUI)
                    return
                }
                if (!value.isNullOrEmpty()) {
                    try {
                        var json = JSON.parseObject(value)
                        var userName = json.getString("userName") ?: ""
                        var path = json.getString("path") ?: ""
                        if (!path.isNullOrEmpty()) {
                            path = if (path.contains("?")) {
                                path.plus("&app_token=${MConstant.token}")
                            } else {
                                path.plus("?app_token=${MConstant.token}")
                            }
                        }
                        var version = json.getInteger("version") ?: 0
                        val api: IWXAPI =
                            WXAPIFactory.createWXAPI(BaseApplication.INSTANT, ConfigUtils.WXAPPID)
                        val req: WXLaunchMiniProgram.Req = WXLaunchMiniProgram.Req()
                        req.userName = userName
                        req.path = path
                        req.miniprogramType = version
                        api.sendReq(req)
                    } catch (e: java.lang.Exception) {
                        "参数错误".toast()
                    }
                } else {
                    "传入参数为空".toast()
                }
            }
            20 -> {//地址管理',
                if (value.isNullOrEmpty()) {
                    startARouter(ARouterMyPath.MineAddressListUI, true)
                } else {//选择地址，点击地址Item直接返回地址Json
                    value.toIntOrNull()?.let {
                        RouterManger.param(RouterManger.KEY_TO_ITEM, it)
                            .needLogin(true)
                            .startARouter(ARouterMyPath.MineAddressListUI)
                    }
                }

            }
            21 -> {//设置',
                startARouter(ARouterMyPath.MineSettingUI)

            }
            22 -> {//' |=>|会员身份,
                startARouter(ARouterMyPath.UniUserIdcardUI, true)

            }
            23 -> {//我的发布',
                startARouter(ARouterMyPath.MineFollowUI, true)

            }
            24 -> {//我的消息',
                startARouter(ARouterMyPath.MineMessageUI, true)

            }
            25 -> {//我的关注',
                bundle.putInt(RouterManger.KEY_TO_ID, 2)
                startARouter(ARouterMyPath.MineFansUI, bundle, true)
            }
            26 -> {//我的活动',
                startARouter(ARouterMyPath.MineJoinAcUI, bundle, true)

            }
            27 -> {//我的收藏',
                startARouter(ARouterMyPath.MineCollectUI, bundle, true)

            }
            28 -> {//我的圈子',
                startARouter(ARouterMyPath.MineCircleUI, bundle, true)

            }
            29 -> {//所有勋章
//                startARouter(ARouterMyPath.MineMedalUI,true)
                startARouter(ARouterMyPath.AllMedalUI, bundle, true)
            }
            30 -> {//积分纪录,
                startARouter(ARouterMyPath.MineIntegralUI, bundle, true)

            }
            31 -> {//‘云豆详情',
            }
            32 -> {//‘成长值',
                startARouter(ARouterMyPath.MineGrowUpUI, true)

            }
            34 -> {//用户个人信息页面,
                startARouter(ARouterMyPath.MineEditInfoUI, true)

            }
            35 -> {//他人主页 需要userId
                startARouter(ARouterMyPath.PersonCenterActivity, bundle, true)

            }
            36 -> {//聚合订单列表页
                startARouter(ARouterShopPath.AllOrderActivity, bundle, true)
            }
            37 -> {//签到
                when {
                    MConstant.token.isNullOrEmpty() -> {
                        startARouter(ARouterMyPath.SignUI)
                    }
                    MineUtils.getBindMobileJumpDataType() -> {
                        startARouter(ARouterMyPath.MineBindMobileUI)
                    }
                    else -> {
                        mineDaySign()
                    }
                }
            }
            38 -> {//我的足迹
                startARouter(ARouterMyPath.MineFootprintUI, bundle, true)
            }
            39 -> {//常见问题
                startARouter(ARouterMyPath.MineFeedbackUI)
            }
            40 -> {//我的粉丝
                bundle.putInt(RouterManger.KEY_TO_ID, 1)
                startARouter(ARouterMyPath.MineFansUI, bundle, true)
            }
            41 -> {//我的爱车
                startARouter(ARouterMyPath.MineLoveCarListUI, true)
            }
            42 -> {//意见反馈记录
                startARouter(ARouterMyPath.MineFeedbackListUI)
            }
            43 -> {//公民认证详情页
                try {
                    var json = JSON.parseObject(value)
//                    bundle.putString(
//                        LiveDataBusKey.MINE_MEMBER_INFO_ID,
//                        json["memberId"]?.toString()
//                    )
//                    bundle.putString(
//                        LiveDataBusKey.MINE_MEMBER_INFO_TYPE,
//                        json["memberKey"].toString()
//                    )
                    bundle.putString("title", json["memberName"]?.toString())
                    startARouter(ARouterMyPath.UniUserAuthUI, bundle)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            44 -> {//邀请新用户
                if (MConstant.token.isNullOrEmpty()) {
                    startARouter(ARouterMyPath.SignUI)
                } else {
                    toShare()
                }
            }
            45 -> {
                //编辑已发布的活动
                val bundle = Bundle()
                if (value != null) {
                    bundle.putInt("wonderid", value.toInt())
                    startARouter(ARouterCirclePath.ReleaseEditActivity, bundle)
                }
            }
            46 -> {
                //问卷编辑
                val bundle = Bundle()
                if (value != null) {
                    bundle.putInt("queryId", value.toInt())
                }
                startARouter(ARouterCirclePath.ReleaseUpActivity, bundle)
            }
            47 -> { //messageType(1->系统消息 2->互动消息 3->交易消息) |消息列表
                if (!value.isNullOrEmpty()) {
                    bundle.putInt("messageType", value.toInt())
                    bundle.putInt("value", value.toInt())
                    when (value) {
                        "1" -> startARouter(ARouterMyPath.MineMessageSysInfoUI)
                        "2", "3" -> startARouter(ARouterMyPath.MineMessageInfoUI, bundle)
                    }
                }
            }
            48 -> {//秒杀列表
                startARouter(ARouterShopPath.GoodsKillAreaActivity)
            }
            49 -> {
                RouterManger
                    .needLogin(true)
                    .param(LiveDataBusKey.MINE_MEMBER_INFO_TYPE, "ford_user")
                    .param(LiveDataBusKey.MINE_MEMBER_INFO_ID, 8)
                    .param("title", "福特员工")
                    .startARouter(ARouterMyPath.FordUserAuthUI)
            }
            50 -> {//认证详情
                try {
                    value?.let {
                        val json = JSON.parseObject(it)
                        val vin = json.getString("vin")
                        val authId = json.getString("authId")
                        val status = json.getIntValue("status")
                        var carSalesInfoId=""
                        if(json.containsKey("carSalesInfoId")){
                           carSalesInfoId = json.getString("carSalesInfoId")
                        }
                        var isNeedChangeBind=0
                        if(json.containsKey("isNeedChangeBind")){
                             isNeedChangeBind = json.getIntValue("isNeedChangeBind")
                        }

                        RouterManger.param(
                            RouterManger.KEY_TO_OBJ,
                            CarItemBean(vin = vin, authId = authId, carSalesInfoId = carSalesInfoId)
                        ).startARouter(
                            when {
                                CommonUtils.isCrmSuccess(status) -> {
                                    ARouterMyPath.MineLoveCarInfoUI
                                }
                                CommonUtils.isCrmStatusIng(status) || (CommonUtils.isCrmFail(status) && CommonUtils.isCrmChangeBindFail(
                                    isNeedChangeBind
                                )) -> {
                                    ARouterMyPath.CarAuthIngUI
                                }
                                else -> {
                                    ARouterMyPath.UniCarAuthUI
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    RouterManger.param(
                        RouterManger.KEY_TO_OBJ, CarItemBean()
                    ).startARouter(ARouterMyPath.UniCarAuthUI)
                }
            }
            52 -> {//商城订单列表
                if (!TextUtils.isEmpty(value)) bundle.putInt(
                    "states", value!!.toInt()
                )//指定选中状态 0全部 1待付款,2待发货,3待收货,4待评价
                startARouter(ARouterShopPath.OrderGoodsActivity, bundle, true)
            }
            54 -> {// 车主认证列表(车主认证部分)
                "此功能暂未开放".toast()
//                startARouter(ARouterMyPath.MineLoveCarListUI)
            }
            55 -> {//月签到详情
                startARouter(ARouterMyPath.SignMonth)

            }
            59 -> {
                value?.let {
                    it.toast()
                }
            }
            60 -> {
                value?.let {
                    try {
                        val json = JSON.parseObject(it)
                        val content = json.getString("content") ?: ""
                        val buttons = json.getJSONArray("buttons")
                        var jumpDataType1 = 0
                        var jumpDataValue1 = ""
                        var jumpDataType2 = 0
                        var jumpDataValue2 = ""
                        var button1 = ""
                        var button2 = ""
                        for (index in 0 until buttons.size) {
                            val job = buttons.getJSONObject(index)
                            val mJson = job.getJSONObject("jumpData")
                            if (index == 0) {
                                jumpDataType1 = mJson.getInteger("jumpDataType") ?: 0
                                jumpDataValue1 = mJson.getString("jumpDataValue") ?: ""
                                button1 = job.getString("btName") ?: ""
                            } else {
                                jumpDataType2 = mJson.getInteger("jumpDataType") ?: 0
                                jumpDataValue2 = mJson.getString("jumpDataValue") ?: ""
                                button2 = job.getString("btName") ?: ""
                            }

                        }
                        AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                            .setMsg(content)
                            .setCancelable(true)
                            .setNegativeButton(
                                button1, R.color.color_7174
                            ) {
                                jump(jumpDataType1, jumpDataValue1)
                            }
                            .setPositiveButton(button2, R.color.black) {
                                jump(jumpDataType2, jumpDataValue2)
                            }.show()
                    } catch (e: Exception) {
                        "数据错误".toast()
                    }
                }
            }
            61 -> {//扫一扫
                SoulPermission.getInstance().checkAndRequestPermission(
                    Manifest.permission.CAMERA, object : CheckRequestPermissionListener {
                        override fun onPermissionOk(permission: Permission?) {
                            startARouter(ARouterHomePath.CaptureActivity)
                        }

                        override fun onPermissionDenied(permission: Permission?) {
                            "没有获取到相机权限,请手动去设置页打开权限,或者重试授权权限".toast()
                        }
                    }
                )
            }
            69 -> {
                showMapDialog(value)
            }
            71 -> {////'71'|在线客服链接|跳转新版意见反馈页面|有意见反馈跳列表，无意见反馈跳添加页面
                /**
                 * 需要参数，在线客服H5链接（string），是否有意见反馈（意见反馈 等于1 已提交意见反馈）
                 * {"hasFeedback":"1","onlineH5":"https://www.baidu.com"}
                 */
//                when {
//                    MConstant.token.isNullOrEmpty() -> {//登录页面
//                        startARouter(ARouterMyPath.SignUI)
//                    }
//                    else -> {
                startARouter(ARouterMyPath.MineCenterFeedbackUI, bundle)
//                    }
//                }
            }
            73 -> { //新增跳转类型73，需要位置权限的H5页面
                if (isOPen(BaseApplication.curActivity)) {
                    SoulPermission.getInstance().checkAndRequestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        object : CheckRequestPermissionListener {
                            override fun onPermissionOk(permission: Permission?) {
                                startARouter(ARouterHomePath.AgentWebActivity, bundle)
                            }

                            override fun onPermissionDenied(permission: Permission?) {
                                toastShow("没有获取到定位权限,请手动去设置页打开权限,或者重试授权权限")
                            }
                        }
                    )
                } else {
                    toastShow("手机没有打开定位权限,请手动去设置页打开权限")
                }

            }
            99 -> {//不跳转
            }
            100 -> {//登录页面
                startARouter(ARouterMyPath.SignUI)
            }
            101 -> {
                bundle.putInt("jumpValue", 1)
                startARouter(ARouterHomePath.MainActivity, bundle)
            }
            102 -> {//' |=> |'社区',
                bundle.putInt("jumpValue", 2)
                startARouter(ARouterHomePath.MainActivity, bundle)
            }
            103 -> {//' |=> |'爱车',
                bundle.putInt("jumpValue", 3)
                startARouter(ARouterHomePath.MainActivity, bundle)
            }
            104 -> {//' |=> |'商城',
                bundle.putInt("jumpValue", 4)
                startARouter(ARouterHomePath.MainActivity, bundle)
            }
            105 -> {//' |=> |'我的',
                bundle.putInt("jumpValue", 5)
                startARouter(ARouterHomePath.MainActivity, bundle)
            }
            106 -> {//'H5页面'（需要登录）,
                when {
                    MConstant.token.isNullOrEmpty() -> {
                        startARouter(ARouterMyPath.SignUI)
                    }
                    else -> {
                        startARouter(ARouterHomePath.AgentWebActivity, bundle)
                    }
                }
            }
            107 -> {//'H5页面'（不仅登录需要绑定手机号）,
                when {
                    MConstant.token.isNullOrEmpty() -> {
                        startARouter(ARouterMyPath.SignUI)
                    }
                    MineUtils.getBindMobileJumpDataType() -> {
                        startARouter(ARouterMyPath.MineBindMobileUI)
                    }
                    else -> {
                        startARouter(ARouterHomePath.AgentWebActivity, bundle)
                    }
                }
            }
            108 -> {// 聚合搜索。
                bundle.putString(JumpConstant.SEARCH_TYPE, value)
                startARouter(ARouterHomePath.PolySearchActivity, bundle = bundle)
            }
            109 -> {// 商品订单确认 需要绑定手机号
                when {
                    MConstant.token.isNullOrEmpty() -> {
                        startARouter(ARouterMyPath.SignUI)
                    }
                    MineUtils.getBindMobileJumpDataType() -> {
                        startARouter(ARouterMyPath.MineBindMobileUI)
                    }
                    else -> {
                        if (!TextUtils.isEmpty(value)) {
                            bundle.putString("goodsInfo", value)//商品信息列表
                            startARouter(ARouterShopPath.OrderConfirmActivity, bundle, true)
                        }
                    }
                }
            }
            110 -> {// 支付确认
                if (!TextUtils.isEmpty(value)) {
                    bundle.putString("orderNo", value)//订单号
                    startARouter(ARouterShopPath.PayConfirmActivity, bundle, true)
                }
            }
            111 -> {//商品评价列表
                if (!TextUtils.isEmpty(value)) {
                    /*"{\"spuId\": \"维保商品ID\",\"spuPageType\": \"MAINTENANCE\"}"
                    * 或者直接传商品id
                    * */
                    bundle.putString("goodsInfo", value)
                    startARouter(ARouterShopPath.GoodsEvaluateActivity, bundle, true)
                }
            }
            112 -> {//商品订单评价
                if (!TextUtils.isEmpty(value)) {
                    // "{\"orderNo\": \"M0565984864114180096\",\"skuList\":[{\"skuImg\":\"pg\",\"mallOrderSkuId\":104,\"mallMallspuId\":1292,\"spuName\": \"石头\"}],\"reviewEval\": false}"
                    bundle.putString("info", value)
                    startARouter(ARouterShopPath.PostEvaluationActivity, bundle, true)
                }
            }
            113 -> {//话题列表
                startARouter(ARouterCirclePath.HotTopicActivity)
            }
            114 -> {//我的问答它的问答
                if (!TextUtils.isEmpty(value)) {
                    bundle.putString("value", value)
                    startARouter(ARouterCirclePath.QuestionActivity, bundle, true)
                }
            }
            115 -> { // 自己可以编辑技术详情的主页
                if (!TextUtils.isEmpty(value)) {
                    bundle.putString("value", value)
                    startARouter(ARouterCirclePath.MechanicMainActivity, bundle, true)
                }
            }
            116 -> { //提问
                startARouter(ARouterCirclePath.CreateQuestionActivity, bundle, true)
            }
            117 -> { //圈子成员列表
                if (!TextUtils.isEmpty(value)) {
                    JSON.parseObject(value)?.apply {
                        val circleId = getString("circleId")
                        val isApply = getString("isApply")
                        bundle.putString("circleId", circleId)
                        bundle.putString("isApply", isApply)
                        startARouter(ARouterCirclePath.PersonalActivity, bundle)
                    }
                }
            }
            118 -> { // 优惠券列表
                startARouter(ARouterShopPath.CouponActivity, bundle, true)
            }
            119 -> { // 购物车
                startARouter(ARouterShopPath.ShoppingCartActivity, bundle, true)
            }
            120 -> { // 申请发票  start 已实现
                startARouter(ARouterShopPath.InvoiceActivity, bundle, true)
            }
            121 -> {// 申请退款--- 未发货
                val gson = Gson()
                val orderString = bundle.getString("value")
                val refundBean: RefundBean = gson.fromJson(orderString, RefundBean::class.java)
                var orderItemBean: RefundOrderItemBean? = null
                if (refundBean.skuItem != null) {
                    orderItemBean = refundBean.skuItem
                    orderItemBean?.orderNo = refundBean.orderNo
                }
                if (refundBean.refundType == "allOrderRefund") { // 整单退
                    startARouter(ARouterShopPath.RefundNotShippedActivity, bundle, true)
                } else if (refundBean.refundType == "onlySkuSingleRefund") {
                    orderItemBean?.singleRefundType = "ONLY_COST"
                    val gson = Gson()
                    val toJson = gson.toJson(orderItemBean)
                    instans?.jump(125, toJson)
                } else if (refundBean.refundType == "onlySkuAllRefund") {
                    orderItemBean?.singleRefundType = "CONTAIN_GOODS"
                    val gson = Gson()
                    val toJson = gson.toJson(orderItemBean)
                    instans?.jump(125, toJson)
                } else {// 发货了，选一下退货还是退款
                    val gson = Gson()
                    val toJson = gson.toJson(orderItemBean)
                    if (refundBean.busSource == "WB"){//如果是维保订单直接跳仅退款
                        instans?.jump(125,toJson)
                        return
                    }
                    bundle.putString("value", toJson)
                    startARouter(ARouterShopPath.AfterSaleActivity, bundle, true)
                }

            }
            122 -> { // 跳优惠券弹窗
                val getCoupopPop =
                    GetCoupopBindingPop(BaseApplication.curActivity, BaseApplication.curActivity)
                getCoupopPop.showPopupWindow()
            }
            123 -> { // 查看发票详情 start 已实现
                startARouter(ARouterShopPath.InvoiceLookActivity, bundle, true)
            }
            124 -> {
                // 整单退退款进度 start 已实现
                startARouter(ARouterShopPath.RefundProgressActivity, bundle, true)
//                startARouter(ARouterShopPath.RefundProgressHasShopActivity, bundle, true)
            }
            125 -> { //单个sku退款申请。
                startARouter(ARouterShopPath.RefundApplySingleActivity, bundle, true)
            }
            126 -> {// 单个sku 退款进度  start 已实现
                startARouter(ARouterShopPath.RefundProgressHasShopActivity, bundle, true)
            }
            127 -> { // 多包裹信息  start 已实现
                startARouter(ARouterShopPath.MultiplePackageActivity, bundle, true)
            }
            128 -> { // 使用优惠券
                if (!TextUtils.isEmpty(value)) {
                    bundle.putString("itemBean", value)//CouponsItemBean
                    startARouter(ARouterShopPath.UseCouponsActivity, bundle, true)
                }
            }
            129 -> { //爱车养护
                startARouter(ARouterShopPath.CarMaintenanceActivity, bundle)
            }
            130 -> {//我的勋章
                startARouter(ARouterMyPath.MineMedalUI, true)
//                startARouter(ARouterMyPath.AllMedalUI, bundle, true)
            }
            131 -> { // 发帖----///发帖 1、图文      2、长贴       3、视频
                //长帖
                value?.let { s ->
                    // 图文
                    when (s.toInt()) {
                        1 -> {
                            startARouter(ARouterCirclePath.PostActivity, bundle, true)
                        }
                        2 -> {
                            startARouter(ARouterCirclePath.LongPostAvtivity, bundle, true)
                        }
                        3 -> {
                            // 视频
                            startARouter(ARouterCirclePath.VideoPostActivity, bundle, true)
                        }
                        else -> {
                            startARouter(ARouterCirclePath.PostActivity, bundle, true)
                        }
                    }
                }

            }
            133-> {//圈子分类页    value  = 圈子分类名称
                startARouter(ARouterCirclePath.CircleListActivity,bundle)
            }
            134-> {//创建圈子
                startARouter(ARouterCirclePath.CreateCircleActivity,true)
            }
            135-> {//圈子热门榜单页
                startARouter(ARouterCirclePath.HotListActivity,bundle)
            }
            138-> {//商城-推荐榜单-榜单列表页：type = 138 value = 榜单名称
                startARouter(ARouterShopPath.RecommendActivity, bundle)
            }
            139-> {//勋章详情
                startARouter(ARouterMyPath.MedalDetailUI, bundle, true)
            }
            140-> {//新增收货地址
                startARouter(ARouterMyPath.EditAddressUI, bundle, true)
            }
            10000 -> {
                //外部H5
                if (!value.isNullOrEmpty()) {
                    val url = if (value.startsWith("http")) value else "http://$value"
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val contentUrl = Uri.parse(url)
                    intent.data = contentUrl
                    BaseApplication.INSTANT.startActivity(intent)
                }
            }

            else -> toastShow("暂不支持该功能, 请升级到新版app")
        }
    }


    companion object {
        @JvmStatic
        var instans: JumpUtils? = null
            get() {
                if (field == null) {
                    field = JumpUtils()
                }
                return field
            }
            private set
    }


    /**
     * 每日签到
     */
    private fun mineDaySign() {
        currentViewModelScope?.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.daySign(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    var bundle = Bundle()
                    bundle.putString("signInfo", JSON.toJSONString(it))
                    startARouter(ARouterMyPath.SignTransparentUI, bundle)
                }
            }.onWithMsgFailure {
                it?.let { it1 -> toastShow(it1) }
            }
        }

    }


    private fun toCarAuth() {
        var body = HashMap<String, Any>()
        var rkey = getRandomKey()
        currentViewModelScope.launch {
            fetchRequest {
                apiService.queryAuthCarList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    var isAuth: Int = 0 //已登录 1 全部数据都是未通过的
                    var failNum: Int = 0
                    when (it.isCarOwner) {
                        1 -> {//已认证成功
                            isAuth = 2
                        }
                        else -> {
                            it.carList?.let {
                                it.forEach {
                                    if (CommonUtils.isCrmFail(it.authStatus)) {
                                        failNum++
                                    }
                                }
                                isAuth = when {
                                    it.size == 0 -> {
                                        0
                                    }
//                                    failNum == it.size -> {//列表数据全部失败
//                                        1
//                                    }
                                    else -> {
                                        3 //有认证中的数据
                                    }
                                }
                            }
                        }
                    }
                    // ToDo
                    when (isAuth) {
                        0, 1 -> {//未认证，或者是认证
                            startARouter(ARouterMyPath.UniCarAuthUI)
                        }
                        2, 3 -> {//有认证成功的数据
                            instans?.jump(41)
                        }
                    }

                }
            }.onWithMsgFailure {
                it?.toast()
            }
        }
    }


    /**
     * 邀请好友
     */
    private fun toShare() {
        var body = HashMap<String, Any>()
        var rkey = getRandomKey()
        currentViewModelScope?.launch {
            fetchRequest {
                apiService.inviteShare(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    var shareBean =
                        ShareBean(it.shareUrl, it.shareImg, it.shareTitle, it.shareDesc, "", "")
                    ShareViewModule().share(
                        BaseApplication.curActivity,
                        shareBean = shareBean
                    )
                }
            }.onWithMsgFailure {
                it?.toast()
            }
        }
//        RepositoryManager.obtainService(ApiService::class.java).inviteShare(
//            getHeader(body, rkey),
//            getRequestBody(body, rkey)
//        )
//            .compose(ResponseTransformer())
//            .subscribe(object : ResponseObserver<BaseBean<TaskShareBean>>() {
//                override fun onSuccess(response: BaseBean<TaskShareBean>) {
//                    response.data?.let {
//                        var shareBean =
//                            ShareBean(it.shareUrl, it.shareImg, it.shareTitle, it.shareDesc, "", "")
//
//                        ShareViewModule(BaseApplication.INSTANT).share(
//                            BaseApplication.curActivity,
//                            shareBean = shareBean
//                        )
//                    }
//                }
//
//                override fun onFail(e: ApiException) {
//                    toastShow(e.msg)
//                }
//
//            })
    }


    /**
     * 选择地图功能
     */
    fun showMapDialog(value: String?) {
        if (!value.isNullOrEmpty()) {
            try {
                val json = JSON.parseObject(value)
                val shareWithType = json.getString("shareWithType")
                SelectMapDialog(BaseApplication.curActivity, object : SelectMapDialog.CheckedView {
                    override fun checkBaiDu() {
                        if ("test_drive_navigation" == shareWithType) WBuriedUtil.clickCarNavigateMap(
                            "百度地图"
                        )
                        JumpMap.openBaiduMap(
                            BaseApplication.curActivity,
                            json.getDouble("latY"), json.getDouble("lngX"),
                            json.getString("name")
                        )
                    }

                    override fun checkGaoDe() {
                        if ("test_drive_navigation" == shareWithType) WBuriedUtil.clickCarNavigateMap(
                            "高德地图"
                        )
                        JumpMap.openGaoDeMap(
                            BaseApplication.curActivity,
                            json.getDouble("latY"), json.getDouble("lngX"),
                            json.getString("name")
                        )
                    }

                    override fun checkCancel() {
                        if ("test_drive_navigation" == shareWithType) WBuriedUtil.clickCarNavigateMap(
                            "取消"
                        )
                    }

                }).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 跳Incall车主认证页面
     */
    fun skipIncallAuth() {//未认证 添加新车
        if (!NetworkUtils.isConnected()) {
            toastShow("无网无法进入")
            return
        }
        var map = mapOf<String, Any>(
            "isIncall" to false,
            "vin" to "",
            "carId" to "",
            "carManage" to false,
            "crmAuthState" to 0
        )
        jump(56, JSON.toJSONString(map))
    }


    fun getTopActivity(): String? {
        var className: String? = null
        val mActivityManager =
            BaseApplication.curActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = mActivityManager.getRunningTasks(1)
        if (list.isNotEmpty() && list[0] != null && list[0]!!.topActivity != null) {
            className = list[0]!!.topActivity!!.className
        }
        return className
    }


    fun isOPen(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps
    }

//    fun gotoNewsDetails(newsValueData: NewsValueData) {////资讯类型 1图文 2 图片 3 视频
//        val bundle = Bundle()
//        bundle.putString(JumpConstant.NEWS_ART_ID, newsValueData.artId)
//        when (newsValueData.type) {
//            1 -> {
//                startARouter(ARouterHomePath.NewsDetailActivity, bundle)
//            }
//            2 -> {
//                startARouter(ARouterHomePath.NewsPicsActivity, bundle)
//            }
//            3 -> {
//                startARouter(ARouterHomePath.NewsVideoDetailActivity, bundle)
//            }
//        }
//    }

}