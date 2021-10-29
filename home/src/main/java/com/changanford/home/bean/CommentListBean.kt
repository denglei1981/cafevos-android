package com.changanford.home.bean

import com.changanford.common.bean.Imag
import com.changanford.common.util.CountUtils
import java.io.Serializable

/**
 * @Author: hpb
 * @Date:  评论 实体类。
 * @Des:
 */
data class CommentListBean(
    val avatar: String = "",
    var childCount: Int = 0,
    val content: String = "",
    val createTime: String = "",
    val groupId: String = "",
    val id: String = "",
    var isLike: Int = 0,
    val headFrameImage: String = "",
    var likesCount: Int = 0,
    val nickname: String = "",
    val phoneModel: String = "",
    val role: String = "",
    val timeStr: String = "",
    val userId: String = "",
    val memberIcon: String = "",
    val imags: List<Imag> = arrayListOf(),
    val parentVo: List<CommentParentVo> = arrayListOf(),
    var isOpenParent: Boolean = false,
    var typeNull: Int = 0 // 没有数据1
) : Serializable {
    override fun toString(): String {
        return "CommentListBean(avatar='$avatar', childCount=$childCount, content='$content', createTime='$createTime', groupId='$groupId', id='$id', isLike=$isLike, likesCount=$likesCount, nickname='$nickname', phoneModel='$phoneModel', role='$role', timeStr='$timeStr', userId=$userId)"
    }
    fun getChildCounts(): String {
        if (childCount == 0) {
            return "回复"
        }
        return CountUtils.formatNum(childCount.toString(), false).toString().plus("回复")
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