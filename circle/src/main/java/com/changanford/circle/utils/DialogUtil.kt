package com.changanford.circle.utils

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

fun setDialogParams(
    context: Context,
    dialog: Dialog,
    gravity: Int
) {
    val win = dialog.window
    win!!.setGravity(gravity)
    val params = win.attributes
    val dm = DisplayMetrics()
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(dm)
    val width = dm.widthPixels
    params.width = width
    win.attributes = params
}