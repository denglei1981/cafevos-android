package com.changanford.circle.bean

import com.luck.picture.lib.entity.LocalMedia
import com.google.gson.annotations.SerializedName
data class LongPostBean(
    var content:String,
    var localMedias: LocalMedia?
)

