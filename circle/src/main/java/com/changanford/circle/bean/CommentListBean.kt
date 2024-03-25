package com.changanford.circle.bean

import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.Imag
import java.io.Serializable

/**
 * @Author: hpb
 * @Date: 2020/5/19
 * @Des:
 */
data class CommentListBean(
    val avatar: String = "",
    val artId: String = "",
    var childCount: Int = 0,
    var content: String = "",
    val createTime: String = "",
    val groupId: String = "",
    var parentVo: ArrayList<ParentVo> = arrayListOf(),
    val authorBaseVo: AuthorBaseVo = AuthorBaseVo(),
    val ipAddr: String? = "",
    val id: String = "",
    var isLike: Int = 0,
    val headFrameImage: String = "",
    var likesCount: Int = 0,
    var nickname: String = "",
    val phoneModel: String = "",
    val role: String = "",
    val timeStr: String = "",
    val userId: String = "",
    val bizId: String = "",
    var carOwner: String = "",
    val memberIcon: String = "",
    val imags: ArrayList<Imag> = arrayListOf(),
    var isFollow: Int = 0, //1 是已关注
    var childVo: MutableList<CommentListBean> = arrayListOf()
) : Serializable {
    override fun toString(): String {
        return "CommentListBean(avatar='$avatar', childCount=$childCount, content='$content', createTime='$createTime', groupId='$groupId', id='$id', isLike=$isLike, likesCount=$likesCount, nickname='$nickname', phoneModel='$phoneModel', role='$role', timeStr='$timeStr', userId=$userId)"
    }

    fun getTimeAndAddress(): String {
        return "$timeStr ${if (ipAddr.isNullOrEmpty()) "" else ipAddr}"
    }
}

data class ParentVo(val nickname: String) {}