package com.changanford.common.util

import android.app.Activity
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.fadeOut
import androidx.core.app.ActivityCompat
import com.changanford.common.MyApp
import com.changanford.common.R
import com.changanford.common.utilext.toast
import com.dueeeke.videoplayer.util.PlayerUtils.getApplication
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.PictureSelectionConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.luck.picture.lib.tools.DoubleUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

object PictureUtil {


    /**
     * 获取最终地址
     */
    fun getFinallyPath(media: LocalMedia): String {
        var path =""
        return if (media.isCut&&!media.isCompressed){
            media.cutPath
        }else if(media.isCompressed || media.isCut && media.isCompressed){
            media.compressPath
        }else{
            media.realPath
        }
        return path
    }

    fun startUCrop(activity: Activity,path:String,requestCode:Int,aspectRatioX:Float,aspectRatioY:Float){
        val sourceUri = Uri.fromFile(File(path))
        val outDir = MyApp.mContext.getExternalFilesDir("")?.absolutePath
        val outFile = File(outDir, System.currentTimeMillis().toString() + ".jpg")
        //裁剪后图片的绝对路径
        //裁剪后图片的绝对路径
        val cameraScalePath = outFile.absolutePath
        val destinationUri = Uri.fromFile(outFile)
        //初始化，第一个参数：需要裁剪的图片；第二个参数：裁剪后图片
        val uCrop = UCrop.of(sourceUri, destinationUri)
        //初始化UCrop配置
        val options = UCrop.Options()
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true)
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(activity, R.color.white))
        options.setToolbarWidgetColor(ActivityCompat.getColor(activity, R.color.white))

        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(activity, R.color.white))
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(true)
        //UCrop配置
        uCrop.withOptions(options)
        //设置裁剪图片的宽高比，比如16：9
        uCrop.withAspectRatio(aspectRatioX, aspectRatioY)
        //uCrop.useSourceImageAspectRatio();
        //跳转裁剪页面
        //uCrop.useSourceImageAspectRatio();
        //跳转裁剪页面
        uCrop.start(activity, requestCode)
    }

    fun openGallery(activity:Activity,datas:ArrayList<LocalMedia>,onResultCallbackListener:OnResultCallbackListener<LocalMedia>,maxVideoTime:Int=4*60,minVideoTime:Int =3,maxNum:Int = 9 ){
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .imageEngine(GlideEngine.createGlideEngine())
            .setLanguage(LanguageConfig.JAPAN)
            .theme(R.style.picture_WeChat_style)
            .isWeChatStyle(true)
            .isUseCustomCamera(false)
            .isPageStrategy(true)
            .isWithVideoImage(true)
            .isMaxSelectEnabledMask(true)
            .videoMaxSecond(maxVideoTime)
            .videoMinSecond(minVideoTime)
            .maxSelectNum(maxNum)
            .minSelectNum(1)
            .maxVideoSelectNum(1)
            .imageSpanCount(4)
            .isReturnEmpty(false)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .isOriginalImageControl(false)
            .selectionMode(
            PictureConfig.MULTIPLE)// 多选 or 单选
            .isSingleDirectReturn(true)
            .isPreviewImage(false)
            .isPreviewVideo(false)
            .isEnablePreviewAudio(false)
            .isCamera(true)
            .isZoomAnim(true)
            .isEnableCrop(false)
            .isCompress(false)
            .compressQuality(90)
            .synOrAsy(true)
            .isGif(false)
            .freeStyleCropEnabled(true)
            .circleDimmedLayer(false)
            .showCropFrame(true)
            .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
            .isOpenClickSound(true)// 是否开启点击声音
            .selectionData(datas)// 是否传入已选图片
            .isDragFrame(true)// 是否可拖动裁剪框(固定)
            //.videoMinSecond(10)// 查询多少秒以内的视频
            //.videoMaxSecond(15)// 查询多少秒以内的视频
            //.recordVideoSecond(10)//录制视频秒数 默认60s
            //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
            //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
            .cutOutQuality(90)// 裁剪输出质量 默认100
            .minimumCompressSize(1024)// 小于100kb的图片不压缩
            //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
            //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
            //.rotateEnabled(false) // 裁剪是否可旋转图片
            //.scaleEnabled(false)// 裁剪是否可放大缩小图片
            //.videoQuality()// 视频录制质量 0 or 1
            //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            .forResult(onResultCallbackListener);
    }


    /**
     * 保存图片到相册(适配安卓11)
     */
    fun saveBitmapPhoto(bm: Bitmap) {
        val resolver = MyApp.mContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "ft${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, it)
                "保存成功".toast()
            }
        }

    }

}