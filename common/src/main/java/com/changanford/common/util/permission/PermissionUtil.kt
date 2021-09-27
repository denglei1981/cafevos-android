package com.changanford.common.util.permission

import android.Manifest
import android.app.Activity
import android.util.Log
import cn.hchstudio.kpermissions.KPermission
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus

object PermissionUtil {

    //拍照
    var CAMERA = false

    //相册
    var ALBUM_READ = false
    var ALBUM_WRITE = false

    //定位权限
    var LOCATION = false
    private var kPermission: KPermission? = null

    private val permissionsGroup =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

    fun applyPermissions(activity: Activity) {
        kPermission = KPermission(activity)
        kPermission?.requestPermission(permissionsGroup, {
//            if (!it) showToast("您已关闭某些权限,若想正常适用软件,请到设置打开相应权限")
            Log.i("APP_TAG", "isAllow---$it")
        }, {
            Log.i("APP_TAG", "permission---$it")
            if (it.name == Manifest.permission.CAMERA) {
                CAMERA = it.granted
            }
            if (it.name == Manifest.permission.READ_EXTERNAL_STORAGE) {
                ALBUM_READ = it.granted
            }
            if (it.name == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                ALBUM_WRITE = it.granted
            }
            if (it.name == Manifest.permission.ACCESS_COARSE_LOCATION) {
                LOCATION = it.granted
            }
            if (it.name == Manifest.permission.ACCESS_FINE_LOCATION) {
                LOCATION = it.granted
                LiveDataBus.get().with(CircleLiveBusKey.LOCATION_RESULT).postValue(LOCATION)
            }
        })
    }

    fun getPermission(): KPermission {
        return kPermission!!
    }

    fun clearKPermission() {
        kPermission = null
    }
}