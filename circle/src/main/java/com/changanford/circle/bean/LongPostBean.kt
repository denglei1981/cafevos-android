package com.changanford.circle.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.luck.picture.lib.entity.LocalMedia

data class LongPostBean(
    var content: String? = "",
    var localMedias: LocalMedia?=null,
    var hintStr:String=""
)  {



}

