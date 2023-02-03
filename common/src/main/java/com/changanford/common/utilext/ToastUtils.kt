package com.changanford.common.utilext

import android.widget.Toast
import com.changanford.common.MyApp
import com.changanford.common.util.toast.ToastUtils

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.ToastUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 11:27
 * @Description: 　
 * *********************************************************************************
 */

@Synchronized
fun String.toast() {
//    Toast.makeText(MyApp.mContext, this, Toast.LENGTH_SHORT).show()
    if (this.isNotEmpty()) {
        ToastUtils.showShortToast(this, MyApp.mContext)
    }
}

fun toastShow(s: String) {
    s.toast()
}