package com.changanford.common.util.gio

import com.growingio.android.sdk.autotrack.GrowingAutotracker

/**
 *Author lcw
 *Time on 2023/1/17
 *Purpose
 */

fun trackCustomEvent(type: String, map: HashMap<String, String>) {
    GrowingAutotracker.get().trackCustomEvent(type, map)
}