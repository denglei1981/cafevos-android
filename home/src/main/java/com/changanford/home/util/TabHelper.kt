package com.changanford.home.util

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.tabs.TabLayout

fun TabLayout.newTabLayout(@LayoutRes layoutId: Int, selected:Boolean): View {
    val view = LayoutInflater.from(context).inflate(layoutId, null, false)
    newTab().also {
        it.customView = view
        addTab(it,selected)
    }
    return view
}