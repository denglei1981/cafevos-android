package com.changanford.circle.bean

import com.luck.picture.lib.entity.LocalMedia

data class LongPostBean @JvmOverloads constructor(
    var content:String,
    var localMedias: LocalMedia?
)

