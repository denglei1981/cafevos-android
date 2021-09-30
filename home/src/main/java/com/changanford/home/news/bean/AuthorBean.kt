package com.changanford.home.bean

data class AuthorBean(
    var authorId: Int,
    var avatar: String,
    var isFollow: Int,
    var isLike: Int,
    var medalImage: Any,
    var medalName: Any,
    var memberIcon: Any,
    var memberId: Int,
    var memberName: Any,
    var nickname: String,
    var picCount: Int,
    var timeStr: String,
    var imags: ImagesBean
)

data class ImagesBean(
    var img: String,
    var jumpDataType: Int,
    var jumpDataValue: String
)
