package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.MultiItemEntity


data class ChooseCircleBean(
    val name: String="",
    val url:String="",
    val title:String="",
    private val ItemType: Int = 2
) : MultiItemEntity {
    override val itemType: Int
        get() = ItemType
}
