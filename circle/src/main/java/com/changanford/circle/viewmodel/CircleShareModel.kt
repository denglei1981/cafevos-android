package com.changanford.circle.viewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleShareBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.utils.launchWithCatch
import com.changanford.circle.widget.dialog.DislikeDialog
import com.changanford.circle.widget.dialog.ReportDialog
import com.changanford.common.MyApp
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.sharelib.bean.IMediaObject
import com.changanford.common.sharelib.manager.ShareManager
import com.changanford.common.sharelib.util.SharePlamFormData
import com.changanford.common.util.MConstant
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

private var shareto: String? = null

/**
 * @Author: lcw
 * @Date: 2021/10/22
 * @Des: 社区分享封装
 */
object CircleShareModel {

    @JvmStatic
    fun shareDialog(
        activity: AppCompatActivity?,
        type: Int,
        shareBean: CircleShareBean?,
        body: ReportDislikeBody? = null,
        is_good: Int? = null,
        userName: String? = null,
        topicName: String? = null,
        postType: Int? = null
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
                        shareto = "2"
                        bus(activity, shareBean)
                    }
                    1 -> {
                        shareto = "1"
                        bus(activity, shareBean)
                    }
                    2 -> {
                        shareto = "4"
                        bus(activity, shareBean)
                    }
                    3 -> {
                        shareto = "3"
                        bus(activity, shareBean)
                    }
                    4 -> {
                        shareto = "6"
                        bus(activity, shareBean)

                    }
                    5 -> {
                        if (MConstant.userId.isNotEmpty()) {
                            ReportDialog(activity, body).show()
                        } else {
                            startARouter(ARouterMyPath.SignUI)//跳转登录
                        }
                    }
                    6 -> {
                        if (MConstant.userId.isNotEmpty()) {
                            DislikeDialog(activity, body).show()
                        } else {
                            startARouter(ARouterMyPath.SignUI)//跳转登录
                        }
                    }
                    7 -> {
//                        toastShow("结束发布")
                    }
                    8 -> {
//                        toastShow("点击海报")
                    }
                    9 -> {
                        MTextUtil.copystr(activity, shareBean.shareUrl)
                    }
                    10 -> {//加精
                        if (is_good == 2) {
                            activity.launchWithCatch {
                                val bodyPostSet = MyApp.mContext.createHashMap()
                                bodyPostSet["postsId"] = shareBean.bizId

                                val rKey = getRandomKey()
                                ApiClient.createApi<CircleNetWork>()
                                    .postSetGood(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                                    .also {
                                        it.msg.toast()
                                    }
                            }
                        }
                    }
                    11 -> {//编辑
                        val bundle = Bundle()
                        bundle.putString("postsId", shareBean.bizId)
                        when (postType) {
                            2 -> {//图文
                                RouterManger.startARouter(ARouterCirclePath.PostActivity, bundle)
                            }
                            3 -> {//视频
                                RouterManger.startARouter(
                                    ARouterCirclePath.VideoPostActivity,
                                    bundle
                                )
                            }
                            4 -> {//长图页
                                RouterManger.startARouter(
                                    ARouterCirclePath.LongPostAvtivity,
                                    bundle
                                )
                            }
                        }
                    }
                    12 -> {//删除
                        activity.launchWithCatch {
                            val bodyDetails = MyApp.mContext.createHashMap()
                            bodyDetails["postIds"] = arrayOf(shareBean.bizId)

                            val rKey = getRandomKey()
                            ApiClient.createApi<CircleNetWork>()
                                .postDelete(bodyDetails.header(rKey), bodyDetails.body(rKey)).also {
                                    LiveDataBus.get().with(CircleLiveBusKey.DELETE_CIRCLE_POST)
                                        .postValue(false)
                                }
                        }
                    }
                    13 -> {//屏蔽
                        activity.launchWithCatch {
                            val bodyDetails = MyApp.mContext.createHashMap()
                            bodyDetails["postsId"] = shareBean.bizId

                            val rKey = getRandomKey()
                            ApiClient.createApi<CircleNetWork>()
                                .postPrivate(bodyDetails.header(rKey), bodyDetails.body(rKey))
                                .also {
                                    LiveDataBus.get().with(CircleLiveBusKey.DELETE_CIRCLE_POST)
                                        .postValue(false)
                                }
                        }
                    }
                }
            }
        if (is_good != null) {
            shareManager.shareDialog.goodJJ(is_good)
        }
        shareManager.open()
    }

}

private fun bus(activity: AppCompatActivity, shareBeanVO: CircleShareBean) {
    //分享
//    LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(activity, {
//        if (it == 0) {
    activity.launchWithCatch {
        val body = MyApp.mContext.createHashMap()
        shareBeanVO.let { bean ->
            body["type"] = bean.type
            body["bizId"] = bean.bizId
            body["content"] = bean.shareDesc
            shareto?.let { shareto ->
                body["shareTo"] = shareto
            }
            body["shareTime"] = System.currentTimeMillis().toString()
            body["userId"] = MConstant.userId

        }
        val rKey = getRandomKey()
        ApiClient.createApi<CircleNetWork>()
            .shareCallBack(body.header(rKey), body.body(rKey))
        LiveDataBus.get().with(CircleLiveBusKey.ADD_SHARE_COUNT).postValue(false)
//            }
    }

//    }
//)
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
fun shareBackUpHttp(shareBean: CircleShareBean?, type: Int) {
    when (type) {
        0 -> {
            "分享成功啦~".toast()
            if (shareBean != null) {
//                HomeApi.shareBackApi(
//                    shareBean.type,
//                    shareBean.bizId,
//                    JSON.toJSONString(shareBean),
//                    shareto ?: "1",
//                    System.currentTimeMillis().toString(),
//                    MConstant.userId,
//                    ""
//                )
            }

        }
        1 -> {
            "分享失败~".toast()
        }
        2 -> {
            "分享失败~".toast()
        }
    }
}