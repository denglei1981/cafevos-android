package com.changanford.home.bean

import com.changanford.common.bean.Imag
import java.io.Serializable

/**
 * @Author: hpb
 * @Date:  评论 实体类。
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
    var likesCount: Int,
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