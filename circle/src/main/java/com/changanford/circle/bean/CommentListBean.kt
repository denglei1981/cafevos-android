package com.changanford.circle.bean

import com.changanford.common.bean.Imag
import java.io.Serializable

/**
 * @Author: hpb
 * @Date: 2020/5/19
 * @Des:
 */
data class CommentListBean(
    val avatar: String,
    var childCount: Int,
    val content: String,
    val createTime: String,
    val groupId: String,
    val id: String,
    var isLike: Int,
    val headFrameImage: String,
    var likesCount: String,
    val nickname: String,
    val phoneModel: String,
    val role: String,
    val timeStr: String,
    val userId: String,
    val memberIcon: String,
    val imags: List<Imag>
) : Serializable {
    override fun toString(): String {
        return "CommentListBean(avatar='$avatar', childCount=$childCount, content='$content', createTime='$createTime', groupId='$groupId', id='$id', isLike=$isLike, likesCount=$likesCount, nickname='$nickname', phoneModel='$phoneModel', role='$role', timeStr='$timeStr', userId=$userId)"
    }
}