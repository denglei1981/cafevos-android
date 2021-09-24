package com.changanford.common.util

import android.app.Activity
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.changanford.common.MyApp
import com.changanford.common.R
import com.changanford.common.utilext.toast
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

object PictureUtil {


    /**
     * 获取最终地址
     */
    fun getFinallyPath(media: LocalMedia): String {
        var path = ""
        return if (media.isCut && !media.isCompressed) {
            media.cutPath
        } else if (media.isCompressed || media.isCut && media.isCompressed) {
            media.compressPath
        } else {
            media.realPath
        }
        return path
    }

    fun startUCrop(
        activity: Activity,
        path: String,
        requestCode: Int,
        aspectRatioX: Float,
        aspectRatioY: Float
    ) {
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

    fun openGallery(
        activity: Activity,
        datas: ArrayList<LocalMedia>,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia>,
        maxVideoTime: Int = 4 * 60,
        minVideoTime: Int = 3,
        maxNum: Int = 9
    ) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .imageEngine(GlideEngine.createGlideEngine())
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
                PictureConfig.MULTIPLE
            )// 多选 or 单选
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
     * 单选图片
     */
    fun openGalleryOnePic(
        activity:Activity,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia>
    ){
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
            .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
            .isWeChatStyle(true)// 是否开启微信图片选择风格
            .isUseCustomCamera(false)// 是否使用自定义相机
            .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
            .isWithVideoImage(false)// 图片和视频是否可以同选,只在ofAll模式下有效
            .isMaxSelectEnabledMask(false)// 选择数到了最大阀值列表是否启用蒙层效果
            //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
            //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
            //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
            //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
            .maxSelectNum(1)// 最大图片选择数量
            .minSelectNum(1)// 最小选择数量
            .maxVideoSelectNum(1) // 视频最大选择数量
            //.minVideoSelectNum(1)// 视频最小选择数量
            //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
            .imageSpanCount(4)// 每行显示个数
            .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
            //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
            .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
            //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
            //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
            //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
            //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
            //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
            .selectionMode(
                PictureConfig.MULTIPLE)// 多选 or 单选
            .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isPreviewImage(true)// 是否可预览图片
            .isPreviewVideo(false)// 是否可预览视频
            //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
            .isEnablePreviewAudio(false) // 是否可播放音频
            .isCamera(true)// 是否显示拍照按钮
            //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
            //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
            .isEnableCrop(false)// 是否裁剪
            //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
            .isCompress(false)// 是否压缩
            .compressQuality(90)// 图片压缩后输出质量 0~ 100
            .synOrAsy(true)//同步true或异步false 压缩 默认同步
            //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
            //.compressSavePath(getPath())//压缩图片保存地址
            //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
            //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
            .isGif(false)// 是否显示gif图片
            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
            .circleDimmedLayer(false)// 是否圆形裁剪
            .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
            .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
            .isOpenClickSound(true)// 是否开启点击声音
            .isDragFrame(true)// 是否可拖动裁剪框(固定)
            //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
            //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
            //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
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