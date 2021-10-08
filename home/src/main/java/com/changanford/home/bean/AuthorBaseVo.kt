package com.changanford.home.bean

data class AuthorBaseVo(
    var authorId: Int,
    var avatar: String,
    var headFrameImage: Any,
    var imags: List<Imag>,
    var isFollow: Int,
    var medalImage: Any,
    var medalName: Any,
    var memberIcon: Any,
    var memberId: Int,
    var memberName: Any,
    var nickname: String
)