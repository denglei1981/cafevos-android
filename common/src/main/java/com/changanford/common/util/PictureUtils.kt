package com.changanford.common.util

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.toast
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.qw.soul.permission.bean.Permissions

object PictureUtils {
    /**
     * 启动裁剪
     *
     * @param activity       上下文
     * @param sourceFilePath 需要裁剪图片的绝对路径
     * @param requestCode    比如：UCrop.REQUEST_CROP
     * @param aspectRatioX   裁剪图片宽高比
     * @param aspectRatioY   裁剪图片宽高比
     * @return
     */
    const val maxvideoTime = 4 * 60 //秒
    const val minvideTime = 3 //秒
    const val maxPaisheTime = 70
    private val permissions = Permissions.build(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val fail = {
        "未获取相册权限".toast()
    }

    /**
     * 单独打开自定义相机
     */
    fun opencarcme(
        activity: Activity?,
        isEnableCrop: Boolean,
        isCompress: Boolean,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val permissionsCarcme = Permissions.build(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val success = {
            PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .isUseCustomCamera(false) // 是否使用自定义相机
                .Paizhao() //新添加的方法 只打开自定义拍照功能
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .isEnableCrop(isEnableCrop) // 是否裁剪
                .setCropTitleBarBackgroundColor(BaseApplication.INSTANT.resources.getColor(R.color.transparent))
                .isCompress(isCompress) // 是否压缩
                .isCamera(true)
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .minimumCompressSize(500) // 小于100kb的图片不压缩
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissionsCarcme, success, fail)
    }

    fun opencarcme(
        activity: Activity?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val permissionsCarcme = Permissions.build(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val success = {
            opencarcme(activity, true, false, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissionsCarcme, success, fail)
    }

    /**
     * 单独打开自定义相机
     */
    fun opencarcme(
        activity: Activity?,
        CompressSize: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val permissionsCarcme = Permissions.build(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val success = {
            PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .isUseCustomCamera(false) // 是否使用自定义相机
                .Paizhao() //新添加的方法 只打开自定义拍照功能
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .isEnableCrop(true) // 是否裁剪
                .setCropTitleBarBackgroundColor(BaseApplication.INSTANT.resources.getColor(R.color.transparent))
                .isCompress(true) // 是否压缩
                .isCamera(true)
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .minimumCompressSize(CompressSize) // 小于100kb的图片不压缩
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissionsCarcme, success, fail)
    }

    /**
     * 单独打开自定义相机
     */
    @JvmStatic
    fun opencarcme(
        activity: Activity?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>,
        x: Int,
        y: Int
    ) {
        val permissionsCarcme = Permissions.build(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val success = {
            PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .isUseCustomCamera(false) // 是否使用自定义相机
                .Paizhao() //新添加的方法 只打开自定义拍照功能
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(false) // 是否可拖动裁剪框(固定)
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .isEnableCrop(true) // 是否裁剪
                .setCropTitleBarBackgroundColor(BaseApplication.INSTANT.resources.getColor(R.color.transparent))
                .isCompress(false) // 是否压缩
                .withAspectRatio(x, y)
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissionsCarcme, success, fail)
    }

    /**
     * 可以设置 最大选择的图片
     *
     * @param activity
     * @param onResultCallbackListener
     */
    fun openGarlly(
        activity: Activity?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            openGarlly(activity, 1, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 可以设置 最大选择的图片
     *
     * @param activity
     * @param onResultCallbackListener
     */
    fun openGarlly(
        CompressSize: Int,
        activity: Activity?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            openGarlly(CompressSize, activity, 1, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun openGarlly(
        activity: Activity?,
        maxNum: Int,
        datas: ArrayList<LocalMedia?>?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(true) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(true) // 选择数到了最大阀值列表是否启用蒙层效果
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(false) // 是否裁剪
                //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(false) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                //                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .selectionData(datas) // 是否传入已选图片
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun openGarlly2(
        activity: Activity?,
        maxNum: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                //                .basicUCropConfig(UCropoptions(activity,16,9))
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(false) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                .withAspectRatio(375, 154) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 不能选择视频
     * @param activity
     * @param onResultCallbackListener
     */
    fun openGarlly5(
        activity: Activity?,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                .maxSelectNum(9) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(false) // 是否裁剪
                //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(false) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                //                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                //                .selectionData(datas)// 是否传入已选图片
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                .recordVideoSecond(maxPaisheTime) //录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun openGarlly(
        activity: Activity?,
        maxNum: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            openGarlly(activity, maxNum, false, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun openAllGarlly(
        activity: Activity?,
        maxNum: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            openAllGarlly(activity, maxNum, false, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 直接打开相册
     */
    fun openGarlly(
        activity: Activity?,
        maxNum: Int,
        isCompress: Boolean,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                //                .basicUCropConfig(UCropoptions(activity,16,9))
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(isCompress) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                .withAspectRatio(16, 9) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    fun openAllGarlly(
        activity: Activity?,
        maxNum: Int,
        isCompress: Boolean,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(true) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                //                .basicUCropConfig(UCropoptions(activity,16,9))
                .maxSelectNum(maxNum) // 最大图片选择数量
//                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
//                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(isCompress) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                .queryMaxFileSize(200f)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                .withAspectRatio(16, 9) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 直接打开相册
     */
    fun openHeadGarlly(
        activity: Activity?,
        maxNum: Int,
        isCompress: Boolean,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                //                .basicUCropConfig(UCropoptions(activity,16,9))
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(isCompress) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                .withAspectRatio(1, 1) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(false) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 直接打开相册
     */
    fun openGarlly(
        CompressSize: Int,
        activity: Activity?,
        maxNum: Int,
        isEnableCrop: Boolean,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(true) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(isEnableCrop) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(true) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                //                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(true) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(CompressSize) // 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 直接打开相册
     */
    fun openGarlly(
        CompressSize: Int,
        activity: Activity?,
        maxNum: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>
    ) {
        val success = {
            openGarlly(CompressSize, activity, maxNum, true, onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    /**
     * 直接打开相册 裁剪固定宽高
     */
    @JvmStatic
    fun openGarlly(
        activity: Activity?,
        maxNum: Int,
        onResultCallbackListener: OnResultCallbackListener<LocalMedia?>,
        x: Int,
        y: Int
    ) {
        val success = {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style) // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .isUseCustomCamera(false) // 是否使用自定义相机
                .isPageStrategy(true) // 是否开启分页策略 & 每页多少条；默认开启
                .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                .maxSelectNum(maxNum) // 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .videoMaxSecond(maxvideoTime) // 查询多少秒以内的视频
                .videoMinSecond(minvideTime) // 查询多少秒以内的视频
                .maxVideoSelectNum(1) // 视频最大选择数量
                //.minVideoSelectNum(1)// 视频最小选择数量
                //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(4) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(
                    PictureConfig.MULTIPLE
                ) // 多选 or 单选
                .isSingleDirectReturn(true) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true) // 是否可预览图片
                .isPreviewVideo(false) // 是否可预览视频
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(false) // 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true) // 是否裁剪
                .setPictureCropStyle(PictureCropParameterStyle.ofSelectTotalStyle()) //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(false) // 是否压缩
                .compressQuality(90) // 图片压缩后输出质量 0~ 100
                .synOrAsy(true) //同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                //                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                .isGif(false) // 是否显示gif图片
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true) // 是否开启点击声音
                .isDragFrame(false) // 是否可拖动裁剪框(固定)
                //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(1024) // 小于100kb的图片不压缩
                .withAspectRatio(x, y) //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(onResultCallbackListener)
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }
}