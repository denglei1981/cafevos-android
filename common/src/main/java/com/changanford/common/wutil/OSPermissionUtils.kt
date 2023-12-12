package com.changanford.common.wutil

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


object OSPermissionUtils {
    //获取权限成功
    const val STATUS_SUCCESS = 0
    //申请权限拒绝, 但是下次申请权限还会弹窗
    const val STATUS_REFUSE = 1
    //申请权限拒绝，并且是永久，不会再弹窗
    const val STATUS_REFUSE_PERMANENT = 2
    //默认未请求授权状态
    const val STATUS_DEFAULT = 3

    private const val REQUEST_CODE = 10000
    private lateinit var permissions: Array<out String>
    private var listener: PermissionListener?= null


    var alertDialog: AlertDialog? = null
    /**
     * 判断是否已授权
     */
    fun isAuthorized(activity: Activity, authorize: String): Boolean{
        val isShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, authorize)
        val flag = ActivityCompat.checkSelfPermission(activity, authorize)
        if (flag!= PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    /**
     * 获取权限状态
     */
    fun getAuthorizeStaus(activity: Activity,authorize: String): Int{
        val flag = ActivityCompat.checkSelfPermission(activity, authorize)
        val isShould = ActivityCompat.shouldShowRequestPermissionRationale(activity, authorize)
        if (isShould){
            return STATUS_REFUSE
        }
        if (flag == PackageManager.PERMISSION_GRANTED){
            //获取到权限
            return STATUS_SUCCESS
        }
        if (!SharedPermissionUtils.contains(authorize)){
            return STATUS_DEFAULT
        }
        return STATUS_REFUSE_PERMANENT
    }

//    /**
//     * 申请单个权限权限
//     */
//    fun requestPermission(activity: Activity, authorize: String, listener: PermissionListener, msg :String){
//        alertDialog = AlertDialog(activity).builder()
//        alertDialog?.setMsg(msg)
//        alertDialog?.setCanceledOnTouchOutside(false)
//        alertDialog?.setCancelable(false)
//        OSPermissionUtils.listener = listener
//        alertDialog?.setPositiveButton("确定") {
//
//            permissions = arrayOf(authorize)
//            val flag = ActivityCompat.checkSelfPermission(activity, authorize)
//            if (flag!= PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
//            }else{
//                OSPermissionUtils.listener?.requestResult(true)
//            }
//        }
//        alertDialog?.setNegativeButton("取消") {
//            OSPermissionUtils.listener?.cancle()
//        }
//        alertDialog?.show()
//
//
//    }

    /**
     * 返回结果
     */
    fun onRequestPermissionsResult(activity: Activity,requestCode: Int, permissions: Array<out String>,  grantResults: IntArray){
        if (requestCode != REQUEST_CODE){
            return
        }
        permissions.forEach {
            val isShould = ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            SharedPermissionUtils.putBoolean(it,isShould)
        }
        grantResults.forEach {

            if (it != PackageManager.PERMISSION_GRANTED){
                listener?.requestResult(false)
                return
            }
        }
        listener?.requestResult(true)
    }
}

interface PermissionListener {
    /**
     * 授权结果
     */
    fun requestResult(isFlog: Boolean)
    fun cancle()
}