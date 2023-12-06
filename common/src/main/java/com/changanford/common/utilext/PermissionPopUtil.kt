package com.changanford.common.utilext

import android.Manifest
import com.changanford.common.basic.BaseApplication
import com.changanford.common.widget.pop.PermissionTipsPop
import com.changanford.common.wutil.OSPermissionUtils
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
        "相机权限用于扫一扫、发帖、图片、视频、提问、修改头像等需要拍摄照片或视频的功能"

    //照片视频
    private const val ALBUM =
        "福域申请存储权限的使用说明，用于发帖、图片、视频、提问、修改头像、扫描二维码等需要获取图片或视频的功能"

    //手机存储
    private const val STORAGE =
        "福域申请存储权限的目的说明，用于发帖、图片、视频、提问、修改头像、二维码、保存海报等需要上传或者保存图片视频的功能"

    //定位
    private const val LOCATION = "福域申请定位权限的目的说明，用于查找附近的经销商等功能"

    fun checkPermissionAndPop(
        permissions: Permissions,
        success: () -> Unit,
        fail: () -> Unit
    ) {

        val pop = PermissionTipsPop(BaseApplication.curActivity).apply {
            setBackgroundColor(MColor.PERMISSION_BG)
        }
        permissions.permissions.forEach {
            val permissionName = it.permissionName

            val useTips = when (permissionName) {
                Manifest.permission.CAMERA -> CAMERA
                Manifest.permission.READ_EXTERNAL_STORAGE -> ALBUM
                Manifest.permission.WRITE_EXTERNAL_STORAGE-> STORAGE
                else -> ""
            }

            when (OSPermissionUtils.getAuthorizeStaus(
                BaseApplication.curActivity,
                permissionName
            )) {
                3 -> {//未申请过权限
                    if (!pop.isShowing) {
                        pop.setContent(useTips)
                        pop.showPopupWindow()
                    }
                }

                2 -> {//申请过但是永久禁止弹窗
                }

                0 -> {//申请权限成功

                }
            }
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
    }

}