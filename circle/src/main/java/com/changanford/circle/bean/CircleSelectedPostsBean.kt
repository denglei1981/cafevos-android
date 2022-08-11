package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2022/8/11
 *Purpose
 */
data class CircleSelectedPostsBean(
    val circles: List<ChooseCircleData> = listOf(),
    val typeName: String = ""
)
