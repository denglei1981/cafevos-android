package com.changanford.common.util.toolbar

import com.changanford.common.R


import android.app.Activity
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.gyf.immersionbar.ImmersionBar

fun Toolbar.config(activity: Activity, builder: Builder) {
    val textTitle: TextView = findViewById(R.id.tv_title)
    textTitle.text = builder.title
    /**
     * 设置背景
     */
    if (builder.backgroundColor != 0) {
        setBackgroundColor(
            builder.backgroundColor
        )
    }
    /**
     * 设置返回按钮
     */
    if (builder.isShowLeftButton) {
        setNavigationIcon(if (builder.leftButtonRes == 0) R.mipmap.back_xhdpi else builder.leftButtonRes)
        setNavigationOnClickListener { view ->
            if (builder.leftButtonClickListener != null) {
                builder.leftButtonClickListener!!.onClick(view)
            } else {
                activity.finish()
            }
        }
    }
    /**
     * 设置menu
     */
    if (builder.rightMenuRes != 0) {
        inflateMenu(builder.rightMenuRes)
        setOnMenuItemClickListener { item ->
            if (builder.rightMenuClickListener != null) {
                builder.rightMenuClickListener!!.onClick(item)
            }
            false
        }
    }
}

fun Toolbar.initTitleBar(activity: Activity,builder: Builder) {
    this.config(activity, builder)
    ImmersionBar.with(activity).titleBar(this)
        .statusBarDarkFont(builder.barDarkFont).keyboardEnable(false).init()
}
