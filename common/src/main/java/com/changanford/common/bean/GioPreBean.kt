package com.changanford.common.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *Author lcw
 *Time on 2023/2/22
 *Purpose
 */
@Parcelize
data class GioPreBean(var prePageName: String = "", var prePageType: String = ""): Parcelable {
}