package com.changanford.circle.viewmodel

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.changanford.circle.bean.CircleShareBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.sharelib.bean.IMediaObject
import com.changanford.common.sharelib.manager.ShareManager
import com.changanford.common.sharelib.util.SharePlamFormData
import com.changanford.common.util.MConstant
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.huawei.hms.common.ApiException
import org.json.JSONObject

private var shareto: String? = null

/**
 * @Author: hpb
 * @Date: 2020/5/22
 * @Des: 首页分析封装
 */
object CircleShareModel {

    @JvmStatic
    fun shareDialog(
        activity: Activity?,
        type: Int,
        shareBean: CircleShareBean?,
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
                        shareto = "2"
                    }
                    1 -> {
                        shareto = "1"
                    }
                    2 -> {
                        shareto = "4"
                    }
                    3 -> {
                        shareto = "3"
                    }
                    4 -> {
                        shareto = "6"

                    }
                    5 -> {
//                        ReportDialog(activity as AppCompatActivity, body).show()
                    }
                    6 -> {
//                        DislikeDialog(activity as AppCompatActivity, body).show()
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
                        }
                    }
                    11 -> {//编辑
                        val bundle = Bundle()
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