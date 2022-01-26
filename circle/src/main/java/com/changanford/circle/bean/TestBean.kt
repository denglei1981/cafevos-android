package com.changanford.circle.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

data class TestBean( val avatar: String,
                     val id: String,
                     var isLike: Int,
                     val headFrameImage: String,
                ) {
}

data class MultiBean( val avatar: String="",
                     val id: String="",
                     var isLike: Int=0,
                     val headFrameImage: String="",
) : MultiItemEntity {
    override var itemType: Int = 0
        get() = 0

}