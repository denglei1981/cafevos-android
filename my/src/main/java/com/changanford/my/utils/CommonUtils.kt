package com.changanford.my.utils

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.H5_REGISTER_AGREEMENT
import com.changanford.common.util.MConstant.H5_USER_AGREEMENT
import com.changanford.common.util.MConstant.loginBgVideoUrl
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.logE
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 *  文件名：CommonUtils
 *  创建者: zcy
 *  创建日期：2021/9/16 20:10
 *  描述: TODO
 *  修改描述：TODO
 */

/**
 * 登录协议
 */
fun TextView.signAgreement() {
    val title = "点击注册/登录，即表示已阅读并同意"
    var content = "《福域APP/小程序个人信息保护政策》"
    var content1 = "《福域APP会员服务协议》"

    var spannable = SpannableString("$title$content、$content1")

    //设置颜色
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#ffffff")
                ds.isUnderlineText = true //去除超链接的下划线
            }
        }, title.length,
        title.length + content.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                JumpUtils.instans?.jump(1, H5_REGISTER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#ffffff")
                ds.isUnderlineText = true //去除超链接的下划线
            }
        }, title.length + content.length + 1,
        spannable.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance()
}


/**
 * 下载文件
 */
fun downFile(url: String, listener: OnDownloadListener) {
    val request = Request.Builder().url(url).build()
    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, p: IOException) {
            listener.onFail()
        }

        override fun onResponse(call: Call, response: Response) {
            var `is`: InputStream? = null
            val buf = ByteArray(2048)
            var len = 0
            var fos: FileOutputStream? = null
            var saveFile: File? = null
            try {
                `is` = response.body?.byteStream()
                val total = response.body?.contentLength() ?: 1
                saveFile =
                    getDiskCachePath(BaseApplication.INSTANT)?.let {
                        getDiskCacheDir(
                            it,
                            MConstant.loginBgVideoPath
                        )
                    }
                if (saveFile == null) {
                    listener.onFail()
                    return
                }
                if (saveFile.exists()) saveFile.delete()
                fos = FileOutputStream(saveFile)
                var sum = 0
                while ((`is`?.read(buf).also { len = it ?: 0 }) != -1) {
                    fos?.write(buf, 0, len)
                    sum += len
                    val progress = (sum * 1f / total * 100).toInt()
                    listener.onProgress(progress)
                }
                fos.flush();
                // 下载完成
                listener.onSuccess(saveFile);
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onFail()
            } finally {
                try {
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    })
}

//登录和绑定手机号获取权限
var refusePermission: Boolean = false
fun downLoginBg(videoUrl: String?) {
    loginBgVideoUrl=videoUrl
    if (refusePermission||!SoulPermission.getInstance().checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).isGranted)return
    val permissions = Permissions.build(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val success = {
        downFile(GlideUtils.handleImgUrl(videoUrl), object : OnDownloadListener {
            override fun onFail() {
                "登录视频下载失败".logE()
            }

            override fun onProgress(progress: Int) {
//                "${progress}--".logE()
            }

            override fun onSuccess(file: File) {
//            "${file.path}".logE()
                MConstant.isDownLoginBgSuccess = true
            }
        })
    }
    val fail = {
        refusePermission = true
    }
    PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//    SoulPermission.getInstance().checkAndRequestPermissions(
//        Permissions.build(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ), object : CheckRequestPermissionsListener {
//            override fun onAllPermissionOk(allPermissions: Array<Permission>) {
//                downFile(GlideUtils.handleImgUrl(videoUrl), object : OnDownloadListener {
//                    override fun onFail() {
//                        "登录视频下载失败".logE()
//                    }
//
//                    override fun onProgress(progress: Int) {
////                "${progress}--".logE()
//                    }
//
//                    override fun onSuccess(file: File) {
////            "${file.path}".logE()
//                        MConstant.isDownLoginBgSuccess = true
//                    }
//                })
//            }
//
//            override fun onPermissionDenied(refusedPermissions: Array<Permission>) {
//                refusePermission = true
//            }
//        })

}

fun getDiskCacheDir(rootName: String, dirName: String): File {
    return File(rootName + File.separator + dirName)
}

fun getDiskCachePath(context: Context): String? {
    return if ((Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) && context.externalCacheDir != null) {
        context.externalCacheDir!!.path
    } else {
        context.cacheDir.path
    }
}


interface OnDownloadListener {

    fun onFail()

    fun onProgress(progress: Int)

    fun onSuccess(file: File)
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun Int.toIntPx() = dpToPx(MyApp.mContext, this.toFloat()).toInt()
