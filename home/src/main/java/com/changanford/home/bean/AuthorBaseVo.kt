package com.changanford.home.bean

import android.text.TextUtils

data class AuthorBaseVo(
    var authorId: Int,
    var avatar: String,
    var headFrameImage: Any,
    var imags: List<LabelBean>,
    var isFollow: Int,
    var medalImage: Any,
    var medalName: Any,
    var memberIcon: String,
    var memberId: Int,
    var memberName: String,
    var nickname: String
) {
    fun getMemberNames(): String {
        if (TextUtils.isEmpty(memberName)) {
            return "车迷级公民"
        }
        return memberName
    }

}