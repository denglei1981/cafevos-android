package com.changanford.circle.bean

import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.Imag

/**
 *Author lcw
 *Time on 2021/10/9
 *Purpose
 */
data class ChildCommentListBean(
    val avatar: String,
    var childCount: Int,
    val content: String,
    val createTime: String,
    val groupId: String,
    val id: String,
    var isLike: Int,
    var likesCount: Int,
    val nickname: String,
    val phoneModel: String,
    val headFrameImage: String,
    val carOwner: String,
    val role: String,
    val authorBaseVo: AuthorBaseVo,
    val timeStr: String,
    val userId: String,
    val memberIcon: String,
    val ipAddr: String?,
    val parentVo: List<CommentParentVo>?,
    var isOpenParent: Boolean = false,
    val imags: List<Imag>
){
    fun getTimeAndAddress(): String {
        return "$timeStr ${if (ipAddr.isNullOrEmpty()) "" else ipAddr}"
    }
}

data class CommentParentVo(
    val avatar: String,
    val content: String,
    val createTime: Long,
    val groupId: String,
    val id: Int,
    val nickname: String,
    val phoneModel: String,
    val pid: Int,
    val userId: String
)