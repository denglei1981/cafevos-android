package com.changanford.common.bean

import androidx.annotation.DrawableRes

/**
 *Author lcw
 *Time on 2022/10/18
 *Purpose
 */
data class ShareEditBean(@DrawableRes val topDrawable: Int, var name: String, val type: Int) {
}