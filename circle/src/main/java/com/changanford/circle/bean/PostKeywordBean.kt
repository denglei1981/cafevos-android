package com.changanford.circle.bean

/**
 * @Author: hpb
 * @Date: 2020/5/26
 * @Des:
 */
data class  PostTagData(val tagMaxCount:Int,val type:Int,val typeName:String,val tags:ArrayList<PostKeywordBean>){

}



data class PostKeywordBean(
    val createTime: Long,
    val id: String,
    val status: Int,
    val tagName: String,
    val tagNameLike: Any,
    val type: Int,
    var isselect:Boolean = false
)