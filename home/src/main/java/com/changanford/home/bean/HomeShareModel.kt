package com.changanford.home.bean

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.alibaba.fastjson.JSON
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.sharelib.bean.IMediaObject
import com.changanford.common.sharelib.manager.ShareManager
import com.changanford.common.sharelib.util.SharePlamFormData
import com.changanford.common.util.MConstant
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.api.HomeNetWork
import com.changanford.home.news.data.ReportDislikeBody
import com.changanford.home.news.data.Shares
import com.changanford.home.news.dialog.DislikeDialog
import com.changanford.home.news.dialog.ReportDialog
import com.changanford.home.util.launchWithCatch


private var shareto: String? = null

/**
 * @Author: hpb
 * @Date: 2020/5/22
 * @Des: 首页分析封装
 */
object HomeShareModel {

    @JvmStatic
    fun shareDialog(
        activity: Activity?,
        type: Int,
        shareBean: Shares?,
        body: ReportDislikeBody? = null,
        is_good: Int? = null,
        userName: String? = null,
        topicName: String? = null
    ) {
        if (activity == null || shareBean == null) return
        val data1 = SharePlamFormData()
        data1.withSinaMessageBuilder(
            SharePlamFormData.SinaMessageBuilder().buidWebMessage(
                shareBean.shareUrl,
                GlideUtils.handleImgUrl(shareBean.shareImg),
                shareBean.shareTitle,
                shareBean.shareDesc
            )
        )
        data1.withQqMessageBuilder(
            SharePlamFormData.QQMessageBuilder().buidWebMessagezoom(
                shareBean.shareUrl,
                GlideUtils.handleImgUrl(shareBean.shareImg),
                shareBean.shareTitle,
                shareBean.shareDesc
            )
        )
        data1.withQqMessageBuilder(
            SharePlamFormData.QQMessageBuilder().buidWebMessage(
                shareBean.shareUrl,
                GlideUtils.handleImgUrl(shareBean.shareImg),
                shareBean.shareTitle,
                shareBean.shareDesc
            )
        )
        data1.withWxChatMessageBuilder(
            SharePlamFormData.WxChatMessageBuilder().buidWebMessage(
                shareBean.shareUrl,
                GlideUtils.handleImgUrl(shareBean.shareImg),
                shareBean.shareTitle,
                shareBean.shareDesc
            )
        )
        data1.withWxMomentMessageBuilder(
            SharePlamFormData.WxMomentMessageBuilder().buidWebMessage(
                shareBean.shareUrl,
                GlideUtils.handleImgUrl(shareBean.shareImg),
                shareBean.shareTitle,
                shareBean.shareDesc
            )
        )
        val shareManager = ShareManager<IMediaObject>(activity, type, false)
            .withPlamFormData(data1.plamFormDatas as MutableList<IMediaObject>?)
            .withPlamformClickListener { view, plamForm ->
                when (plamForm) {
                    0 -> {
                        GioPageConstant.infoShareType = "微信好友"
                        shareto = "2"
                        if (GioPageConstant.isInInfoActivity) {
                            GIOUtils.infoShareSuccess()
                        }
                        //                                        SPUtils.saveBuried(
                        //                                            "share",
                        //                                            "微信好友分享",
                        //                                            UmengUtils.WXPY,
                        //                                            "",
                        //                                            "微信好友分享",
                        //                                            "",
                        //                                            "",
                        //                                            "微信好友",
                        //                                            "",
                        //                                            ""
                        //                                        )

//                        GrowingIO.getInstance().track(
//                            "yl_shareContent",
//                            JsonUtil.getJson(
//                                JSONObject().put(
//                                    "yl_contentName_var",
//                                    shareBean.shareTitle
//                                ).apply {
//                                    put("yl_shareType_var", "微信")
//                                    put("yl_authorName_var", userName)
//                                    put("yl_topicName_var", topicName)
//                                    put(
//                                        "yl_contentType_var",
//                                        if (type == 1) "资讯" else if (type == 2) "帖子" else "活动"
//                                    )
//                                })
//                        )
                    }
                    1 -> {
                        shareto = "1"
                        GioPageConstant.infoShareType = "朋友圈"
                        if (GioPageConstant.isInInfoActivity) {
                            GIOUtils.infoShareSuccess()
                        }
                        //                                        SPUtils.saveBuried(
                        //                                            "share",
                        //                                            "微信朋友圈分享",
                        //                                            UmengUtils.WXPYQ,
                        //                                            "",
                        //                                            "微信朋友圈分享",
                        //                                            "",
                        //                                            "",
                        //                                            "微信朋友圈",
                        //                                            "",
                        //                                            ""
                        //                                        )
//                        GrowingIO.getInstance().track(
//                            "yl_shareContent",
//                            JsonUtil.getJson(
//                                JSONObject().put(
//                                    "yl_contentName_var",
//                                    shareBean.shareTitle
//                                ).apply {
//                                    put("yl_shareType_var", "朋友圈")
//                                    put("yl_authorName_var", userName)
//                                    put("yl_topicName_var", topicName)
//                                    put(
//                                        "yl_contentType_var",
//                                        if (type == 1) "资讯" else if (type == 2) "帖子" else "活动"
//                                    )
//                                })
//                        )
                    }
                    2 -> {
                        shareto = "4"
                        GioPageConstant.infoShareType = "微博"
                        if (GioPageConstant.isInInfoActivity) {
                            GIOUtils.infoShareSuccess()
                        }
                        //                                        MobclickAgent.onEvent(activity, UmengUtils.SINA)
                        //                                        SPUtils.saveBuried(
                        //                                            "share",
                        //                                            "新浪分享",
                        //                                            UmengUtils.SINA,
                        //                                            "",
                        //                                            "新浪分享",
                        //                                            "",
                        //                                            "",
                        //                                            "微信好友",
                        //                                            "",
                        //                                            ""
                        //                                        )
//                        GrowingIO.getInstance().track(
//                            "yl_shareContent",
//                            JsonUtil.getJson(
//                                JSONObject().put(
//                                    "yl_contentName_var",
//                                    shareBean.shareTitle
//                                ).apply {
//                                    put("yl_shareType_var", "新浪")
//                                    put("yl_authorName_var", userName)
//                                    put("yl_topicName_var", topicName)
//                                    put(
//                                        "yl_contentType_var",
//                                        if (type == 1) "资讯" else if (type == 2) "帖子" else "活动"
//                                    )
//                                })
//                        )
                    }
                    3 -> {
                        shareto = "3"
                        GioPageConstant.infoShareType = "QQ好友"
                        if (GioPageConstant.isInInfoActivity) {
                            GIOUtils.infoShareSuccess()
                        }
                        //                                        MobclickAgent.onEvent(activity, UmengUtils.QQ)
                        //                                        SPUtils.saveBuried(
                        //                                            "share",
                        //                                            "QQ好友分享",
                        //                                            UmengUtils.QQ,
                        //                                            "",
                        //                                            "QQ好友分享",
                        //                                            "",
                        //                                            "",
                        //                                            "微信好友",
                        //                                            "",
                        //                                            ""
                        //                                        )
//                        GrowingIO.getInstance().track(
//                            "yl_shareContent",
//                            JsonUtil.getJson(
//                                JSONObject().put(
//                                    "yl_contentName_var",
//                                    shareBean.shareTitle
//                                ).apply {
//                                    put("yl_shareType_var", "QQ")
//                                    put("yl_authorName_var", userName)
//                                    put("yl_topicName_var", topicName)
//                                    put(
//                                        "yl_contentType_var",
//                                        if (type == 1) "资讯" else if (type == 2) "帖子" else "活动"
//                                    )
//                                })
//                        )
                    }
                    4 -> {
                        shareto = "6"
                        GioPageConstant.infoShareType = "QQ空间"
                        if (GioPageConstant.isInInfoActivity) {
                            GIOUtils.infoShareSuccess()
                        }
                        //                                        MobclickAgent.onEvent(activity, UmengUtils.QQZOOM)
                        //                                        SPUtils.saveBuried(
                        //                                            "share",
                        //                                            "QQ朋友圈分享",
                        //                                            UmengUtils.QQZOOM,
                        //                                            "",
                        //                                            "QQ朋友圈分享",
                        //                                            "",
                        //                                            "",
                        //                                            "微信好友",
                        //                                            "",
                        //                                            ""
                        //                                        )
//                        GrowingIO.getInstance().track(
//                            "yl_shareContent",
//                            JsonUtil.getJson(
//                                JSONObject().put(
//                                    "yl_contentName_var",
//                                    shareBean.shareTitle
//                                ).apply {
//                                    put("yl_shareType_var", "QQ空间")
//                                    put("yl_authorName_var", userName)
//                                    put("yl_topicName_var", topicName)
//                                    put(
//                                        "yl_contentType_var",
//                                        if (type == 1) "资讯" else if (type == 2) "帖子" else "活动"
//                                    )
//                                })
//                        )
                    }
                    5 -> {
                        if (MConstant.userId.isNotEmpty()) {
                            ReportDialog(activity as AppCompatActivity, body).show()
                        } else {
                            startARouter(ARouterMyPath.SignUI)//跳转登录
                        }
                    }
                    6 -> {
                        if (MConstant.userId.isNotEmpty()) {
                            DislikeDialog(activity as AppCompatActivity, body).show()
                        } else {
                            startARouter(ARouterMyPath.SignUI)//跳转登录
                        }
                    }
                    7 -> {
                        toastShow("结束发布")
                    }
                    8 -> {
                        toastShow("点击海报")
                    }
                    9 -> {
                        MTextUtil.copystr(activity, shareBean.shareUrl)
                    }
                    10 -> {//加精
//                        if (is_good == 2) {
//                            HomeApi.postSetGood(shareBean.bizId,
//                                object : ResponseObserver<BaseBean<Any>>() {
//                                    override fun onSuccess(response: BaseBean<Any>) {
//                                        toastShow("申请提交成功")
//                                        LiveDataBus.get().with("post_sqjj").value =
//                                            System.currentTimeMillis()
//                                    }
//
//                                    override fun onFail(e: ApiException) {
//                                        toastShow(e.msg)
//                                    }
//                                })
//                        }
                    }
                    11 -> {//编辑
//                        val bundle = Bundle()
//                        bundle.putString("postsId", shareBean.bizId)
//                        startARouter(ARouterHomePath.EditPostActivity, bundle)
                    }
                    12 -> {//删除
//                        HomeApi.postDelete(
//                            arrayOf(shareBean.bizId),
//                            object : ResponseObserver<BaseBean<Any>>() {
//                                override fun onSuccess(response: BaseBean<Any>) {
//                                    LiveDataBus.get().with("post_delete_position").value =
//                                        System.currentTimeMillis()
//                                }
//
//                                override fun onFail(e: ApiException) {
//                                    toastShow(e.msg)
//                                }
//                            })
                    }
                    13 -> {//屏蔽
//                        HomeApi.postPrivate(
//                            shareBean.bizId,
//                            object : ResponseObserver<BaseBean<Any>>() {
//                                override fun onSuccess(response: BaseBean<Any>) {
//                                    LiveDataBus.get().with("post_delete_position").value =
//                                        System.currentTimeMillis()
//                                }
//
//                                override fun onFail(e: ApiException) {
//                                    toastShow(e.msg)
//                                }
//                            })
                    }
                }
            }
        if (is_good != null) {
            shareManager.shareDialog.goodJJ(is_good)
        }
        shareManager.open()
    }


}

/**
 * type	string
必须
业务类型 1 资讯 2 帖子 3 活动 4 用户  5 专题 6 商品  7 圈子 8 话题
bizId	string
必须
业务ID
content	string
非必须
分享内容
shareTo	string
必须
分享目标渠道 1微信朋友圈 2 微信朋友 3QQ好友 4 新浪微博 5 短信 6 QQ空间
shareTime	string
非必须
分享时间 不传为当前默认
userId	string
非必须
用户ID
device	string
非必须
分享设备id
 */
fun shareBackUpHttp(lifecycleOwner: LifecycleOwner, shareBean: Shares?, type: Int = 0) {
    when (type) {
        0 -> {
            if (shareBean != null) {
                lifecycleOwner.launchWithCatch {
                    val body = HashMap<String, Any>()
                    body["type"] = shareBean.type
                    body["bizId"] = shareBean.bizId
                    body["content"] = JSON.toJSONString(shareBean)
                    body["shareTo"] = shareto ?: "1"
                    body["shareTime"] = System.currentTimeMillis()
                    body["userId"] = MConstant.userId
                    body["device"] = ""
                    val rkey = getRandomKey()
                    ApiClient.createApi<HomeNetWork>()
                        .ShareBack(body.header(rkey), body.body(rkey))
                        .onSuccess {
                            LiveDataBus.get().with(CircleLiveBusKey.ADD_SHARE_COUNT)
                                .postValue(false)
                        }.onWithMsgFailure {

                        }
                }
            }

        }
        1 -> {
            toastShow("分享失败")
        }
        2 -> {
            toastShow("分享失败")
        }
    }
}