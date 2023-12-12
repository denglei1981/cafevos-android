package com.changanford.common.web

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.ShareBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.sharelib.bean.IMediaObject
import com.changanford.common.sharelib.manager.ShareManager
import com.changanford.common.sharelib.util.SharePlamFormData
import com.changanford.common.util.MConstant
import com.changanford.common.util.MTextUtil
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.toastShow
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.repository.ShareViewModule
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/20 09:24
 * @Description: 　
 * *********************************************************************************
 */
class ShareViewModule : ViewModel() {

    var shareto :String = ""
    /**
     * 分享回调
     */
    fun shareBack(shareBean: ShareBean?){
        if (shareBean!=null){
            val body = HashMap<String, Any>()
            body["type"] = shareBean.type
            body["bizId"] = shareBean.bizId
            body["content"] = shareBean.content
            body["shareTo"] = shareto
            body["shareTime"] = System.currentTimeMillis()
            body["userId"] = MConstant.userId
            body["device"] = ""
            val rkey = getRandomKey()
            viewModelScope.launch {
                fetchRequest {
                    apiService.ShareBack(body.header(rkey),body.body(rkey))
                }
            }
        }
    }
    fun share(activity: Activity,shareBean: ShareBean) {
        val data1 = SharePlamFormData()
//        toastShow("调起分享")
        if (shareBean.isimg == "1") {
            val permissions = Permissions.build(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            val success={
                getBitmap2Share(shareBean.imageUrl) { bitmap->
                    data1.withSinaMessageBuilder(
                        SharePlamFormData.SinaMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
                    )
                    data1.withQqMessageBuilder(
                        SharePlamFormData.QQMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
                    )
                    data1.withQqMessageBuilder(
                        SharePlamFormData.QQMessageBuilder().buildedrImageMessagezoom(shareBean.imageUrl,bitmap)
                    )
                    data1.withWxChatMessageBuilder(
                        SharePlamFormData.WxChatMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
                    )
                    data1.withWxMomentMessageBuilder(
                        SharePlamFormData.WxMomentMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
                    )
//                        showShareDialog(activity,shareBean,data1)
                }
            }
            val fail = {
                toastShow("没有权限")
            }
            PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//            SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,object:CheckRequestPermissionListener{
//                override fun onPermissionOk(permission: Permission?) {
//                    getBitmap2Share(shareBean.imageUrl) { bitmap->
//                        data1.withSinaMessageBuilder(
//                            SharePlamFormData.SinaMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
//                        )
//                        data1.withQqMessageBuilder(
//                            SharePlamFormData.QQMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
//                        )
//                        data1.withQqMessageBuilder(
//                            SharePlamFormData.QQMessageBuilder().buildedrImageMessagezoom(shareBean.imageUrl,bitmap)
//                        )
//                        data1.withWxChatMessageBuilder(
//                            SharePlamFormData.WxChatMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
//                        )
//                        data1.withWxMomentMessageBuilder(
//                            SharePlamFormData.WxMomentMessageBuilder().buildedrImageMessage(shareBean.imageUrl,bitmap)
//                        )
////                        showShareDialog(activity,shareBean,data1)
//                    }
//                }
//
//                override fun onPermissionDenied(permission: Permission?) {
//                    toastShow("没有权限")
//                }
//
//            })

        }else{
            data1.withSinaMessageBuilder(
                SharePlamFormData.SinaMessageBuilder().buidWebMessage(
                    shareBean.targetUrl,
                    GlideUtils.handleImgUrl(shareBean.imageUrl),
                    shareBean.title,
                    shareBean.content
                )
            )
            data1.withQqMessageBuilder(
                SharePlamFormData.QQMessageBuilder().buidWebMessagezoom(
                    shareBean.targetUrl,
                    GlideUtils.handleImgUrl(shareBean.imageUrl),
                    shareBean.title,
                    shareBean.content
                )
            )
            data1.withQqMessageBuilder(
                SharePlamFormData.QQMessageBuilder().buidWebMessage(
                    shareBean.targetUrl,
                    GlideUtils.handleImgUrl(shareBean.imageUrl),
                    shareBean.title,
                    shareBean.content
                )
            )
            data1.withWxChatMessageBuilder(
                SharePlamFormData.WxChatMessageBuilder().buidWebMessage(
                    shareBean.targetUrl,
                    GlideUtils.handleImgUrl(shareBean.imageUrl),
                    shareBean.title,
                    shareBean.content
                )
            )
            data1.withWxMomentMessageBuilder(
                SharePlamFormData.WxMomentMessageBuilder().buidWebMessage(
                    shareBean.targetUrl,
                    GlideUtils.handleImgUrl(shareBean.imageUrl),
                    shareBean.title,
                    shareBean.content
                )
            )
            showShareDialog(activity,shareBean,data1)
        }
    }

    private fun showShareDialog(activity: Activity, shareBean: ShareBean, data1: SharePlamFormData){
        ShareManager<IMediaObject>(activity, 0, false)
            .withPlamFormData(data1.plamFormDatas as MutableList<IMediaObject>?)
            .withPlamformClickListener { view, plamForm ->
                when (plamForm) {
                    0 -> {
                        shareto = "1";
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
                    }
                    1 -> {
                        shareto = "2";
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
                    }
                    2 -> {
                        shareto = "4";
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
                    }
                    3 -> {
                        shareto = "3";
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
                    }
                    4 -> {
                        shareto = "6";
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
                    }
                    5 -> {
                        toastShow("举报")
                    }
                    6 -> {
                        toastShow("不喜欢")
                    }
                    7 -> {
                        toastShow("结束发布")
                    }
                    8 -> {
                        toastShow("点击海报")
                    }
                    9 -> {
                        MTextUtil.copystr(BaseApplication.INSTANT, shareBean.targetUrl)
                    }
                }
                buriedShare(shareBean,shareto)
            }
            .open()
    }
    fun getBitmap2Share(url:String,callBack:(Bitmap)->Unit){
        var bytes = ByteArray(0)
        var bitmap :Bitmap? = null
        Glide.with(MyApp.mContext).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmap = resource
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                if (baos.toByteArray().size / 1024 > 32) {
                    baos.reset()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                }
                bytes = baos.toByteArray()
                callBack(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                super.onLoadCleared(placeholder)
            }
        })
    }
    private fun buriedShare(shareBean: ShareBean,type:String){
        shareBean.shareWithType?.apply {
            //爱车海报分享
            if("love_car_poster"==this){
                when(type){
                    "1"->WBuriedUtil.clickCarShareWX()
                    "2"->WBuriedUtil.clickCarShareWXMoments()
                    "3"->WBuriedUtil.clickCarShareQQ()
                    "4"->WBuriedUtil.clickCarShareWB()
                    "6"->WBuriedUtil.clickCarShareQQZone()
                }
            }
        }
    }
}