package com.changanford.common.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.PermissionUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/23 15:25
 * @Description: 　权限管理
 * *********************************************************************************
 */

/**
 * 单一权限
 */
private inline fun checkPermission(context: Context, p: () -> String): Int =
    ContextCompat.checkSelfPermission(context, p())

/**
 * 多权限
 */
private inline fun checkPermissions(context: Context, p: () -> Array<String>): Int {
    for (i in p()) {
        if (ContextCompat.checkSelfPermission(context, i) == PackageManager.PERMISSION_DENIED) {
            return PackageManager.PERMISSION_DENIED
        }
    }
    return PackageManager.PERMISSION_GRANTED
}

/**
 * 获取权限监听
 * 需要在onStart之前调用
 * @sample getPermissionLauncher(this) {}.launch(Manifest.permission.CAMERA)
 *
 */
inline fun getPermissionLauncher(
    context: ComponentActivity,
    result: ActivityResultCallback<Boolean>
) =
    context.registerForActivityResult(ActivityResultContracts.RequestPermission(), result)

/**
 * 在Fragment中使用权限的问题
 */
inline fun getPermissionLauncher(
    context: Fragment,
    result: ActivityResultCallback<Boolean>
) =
    context.registerForActivityResult(ActivityResultContracts.RequestPermission(), result)
/**
 * 同上
 */
inline fun getPermissionsLauncher(
    context: ComponentActivity,
    result: ActivityResultCallback<Map<String, Boolean>>
) =
    context.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), result)


/**
 * 检测是否有相应权限
 * @param tipUi -> 展示给用户自定义提示UI,<0表示用户已经禁止不再提示了，需要跳系统设置
 */
fun hasPermission(
    context: Activity,
    permission: String,
    tipUi: (Int) -> Unit = {},
): Boolean {
    checkPermission(context) {
        permission
    }.apply {
        return if (this == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(context, permission)) {
                    tipUi(0)
                } else {
                    tipUi(-1)
                }
            }
            false
        }
    }
}

/**
 * 是否有所有权限
 */
fun hasPermissions(
    context: Activity,
    permission: Array<String>,
): Boolean {
    checkPermissions(context) {
        permission
    }.apply {
        return this == PackageManager.PERMISSION_GRANTED
    }
}