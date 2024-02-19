package com.changanford.common.utilext

import android.Manifest
import com.changanford.common.basic.BaseApplication
import com.changanford.common.util.MConstant
import com.changanford.common.widget.pop.PermissionTipsPop
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener

/**
 *Author lcw
 *Time on 2023/12/6
 *Purpose
 */
object PermissionPopUtil {

    //拍照
    private const val CAMERA =
        "相机权限用于发帖、图片、视频、提问、修改头像等需要拍摄照片或视频的功能"

    //照片视频
//    private const val ALBUM =
//        "用于发帖、图片、视频、提问、修改头像、扫描二维码等需要获取图片或视频的功能"

    //手机存储
    private const val STORAGE =
        "用于发帖、图片、视频、提问、修改头像、二维码、保存海报等需要上传或者保存图片视频的功能"

    //定位
    private const val LOCATION = "用于查找附近的经销商等功能"

    private const val CALENDAR = "添加日历提示签到功能"

    private const val CAMERA_TITLE = "福域申请相机权限目的说明"
    private const val ALBUM_TITLE = "福域申请存储权限的使用说明"
    private const val STORAGE_TITLE = "福域申请存储权限的目的说明"
    private const val LOCATION_TITLE = "福域申请定位权限的目的说明"
    private const val CALENDAR_TITLE = "福域申请日历权限的目的说明"

    fun checkPermissionAndPop(
        permissions: Permissions,
        success: () -> Unit,
        fail: () -> Unit
    ) {

        val pop = PermissionTipsPop(BaseApplication.curActivity).apply {
            setBackgroundColor(MColor.PERMISSION_BG)
        }


        SoulPermission.getInstance()
            .checkAndRequestPermissions(permissions,
                object : CheckRequestPermissionsListener {
                    override fun onAllPermissionOk(allPermissions: Array<out Permission>?) {
                        pop.dismiss()
                        success.invoke()
                    }

                    override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                        pop.dismiss()
                        fail.invoke()
                    }

                })

        permissions.permissions.forEach {
            val permissionName = it.permissionName

            val useTips = when (permissionName) {
                Manifest.permission.CAMERA -> CAMERA
                Manifest.permission.READ_EXTERNAL_STORAGE -> STORAGE
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> STORAGE
                Manifest.permission.ACCESS_FINE_LOCATION -> LOCATION
                Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR -> CALENDAR
                else -> ""
            }
            val useTitle = when (permissionName) {
                Manifest.permission.CAMERA -> CAMERA_TITLE
                Manifest.permission.READ_EXTERNAL_STORAGE -> ALBUM_TITLE
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> STORAGE_TITLE
                Manifest.permission.ACCESS_FINE_LOCATION -> LOCATION_TITLE
                Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR -> CALENDAR_TITLE
                else -> ""
            }
            Thread{
                kotlin.run {
                    Thread.sleep(800)
                    if (MConstant.isOnBackground) {
                        if (!pop.isShowing) {
                            pop.setTitle(useTitle)
                            pop.setContent(useTips)
                            BaseApplication.curActivity.runOnUiThread {
                                pop.showPopupWindow()
                            }
                        }
//                Hawk.put(permissionName, true)
                    }
                }
            }.start()

//            else {
//                if (ContextCompat.checkSelfPermission(
//                        BaseApplication.curActivity,
//                        permissionName
//                    ) == PackageManager.PERMISSION_DENIED && !ActivityCompat.shouldShowRequestPermissionRationale(
//                        BaseApplication.curActivity,
//                        permissionName
//                    )
//                ) {
//                    //判断权限是否处于不再询问状态的代码有点小BUG，它必须申请过一次权限并且用户做出了选择之后判断才能够准确。
//                    // 该权限已经被永久禁止了
//                } else if (ContextCompat.checkSelfPermission(
//                        BaseApplication.curActivity,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // 需要向用户请求获取该权限
//                    if (!pop.isShowing) {
//                        pop.setTitle(useTitle)
//                        pop.setContent(useTips)
//                        pop.showPopupWindow()
//                    }
//
//                } else {
//                    // 有权限访问外部存储空间
//
//                }
//            }

        }
    }


}