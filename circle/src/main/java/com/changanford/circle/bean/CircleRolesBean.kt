package com.changanford.circle.bean

 data class CircleRolesBean(
    val circleStarRoleDtos: ArrayList<CircleStarRoleDto>
)

data class CircleStarRoleDto(
    var circleId: String,
    val circleStarRoleId: String,
    val existsStarNum: Int,
    val full: Boolean,
    var isApply: Int,
    val isFull: Any,
    val orderNum: Int,
    val starAuthority: String,
    val starName: String,
    val starNum: Int
)