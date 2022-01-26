package com.changanford.circle.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.PostKeywordBean

data class TestBean( val avatar: String,
                     val id: String,
                     var isLike: Int,
                     val headFrameImage: String,
                ) {
}

data class MultiBean(val authorBaseVo: AuthorBaseVo? = null,
                     val circleId: Int? = 0,
                     var itemImgHeight: Int = 0,
                     val collectCount: Int = 0,
                     var commentCount: Long = 0,
                     val content: String = "",
                     val contentLike: Any? = Any(),
                     val createBy: Any? = Any(),
                     val createTime: Long = 0,
                     val isCheck: Int = 0,
                     val isDeleted: Int = 0,
                     val isGood: Int = 0,//是否加精 1：加精，2：不加精, 3: 加精审核中
                     val isHot: Int = 0,
                     var isLike: Int = 0,
                     val isPrivate: Int = 0,
                     val isPublish: String = "",
                     val isRecommend: Int = 0,
                     val isTop: Int = 0,
                     val keywords: String = "",
                     var likesCount: Long = 0,
                     val likesCountBase: Int = 0,
                     val likesCountMul: Int = 0,
                     val picCount: Int = 0,
                     val pics: String = "",
                     val plate: Int = 0,
                     val postsId: Int = 0,
                     val publishTime: Any? = Any(),
                     val rejectReason: Any? = Any(),
                     val remark: Any? = Any(),
                     val searchValue: Any? = Any(),
                     val shareCount: Int = 0,
                     val sortOrder: Any? = Any(),
                     val status: Int = 0,
                     val timeStr: String = "",
                     val title: String? = "",
                     val city: String? = "",
                     val topicName: String? = "",
                     val topTime: Any? = Any(),
                     val topicId: Int? = 0,
                     val type: Int = 0,
                     val updateBy: Any? = Any(),
                     val updateTime: Long = 0,
                     val userId: Int = 0,
                     val videoTime: Any? = Any(),
                     val videoUrl: Any? = Any(),
                     val viewsCount: Long = 0,
                     val viewsCountBase: Int = 0,
                     val viewsCountMul: Int = 0,
                     val picList: List<String>? = null,
                     val lat: Double = 0.0,
                     val lon: Double = 0.0,
                     val address:String="",
                     var tags:MutableList<PostKeywordBean>?=null,
                     var addrName: String?="",
                     var answerContent:String=""
) : MultiItemEntity {
    private fun getItemTypeLocal(): Int {
        if(TextUtils.isEmpty(answerContent)){
            return 0
        }
        return 1
    }
    override var itemType: Int = getItemTypeLocal()


}

