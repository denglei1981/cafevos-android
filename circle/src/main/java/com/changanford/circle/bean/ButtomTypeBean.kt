package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.MultiItemEntity


data class ButtomTypeBean(
    var content:String,
    var visibility:Int,
    private val ItemType:Int
) : MultiItemEntity {
    override var itemType: Int = 0
        get() = ItemType
}
