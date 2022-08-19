package com.changanford.common.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */

data class FordPhotosBean(
    val categoryOfPhotos: ArrayList<CategoryOfPhoto> = arrayListOf()
)

@Parcelize
data class CategoryOfPhoto(
    val categoryName: String = "",
    val imgUrls: ArrayList<String> = arrayListOf()
): Parcelable