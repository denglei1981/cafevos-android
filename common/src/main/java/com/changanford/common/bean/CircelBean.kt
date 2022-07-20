package com.changanford.common.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 *  文件名：CircelBean
 *  创建者: zcy
 *  创建日期：2021/9/26 19:37
 *  描述: TODO
 *  修改描述：TODO
 */
data class CircleListBean(
    val dataList: List<CircleItemBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)

data class CircleMangerBean(
    val circles: List<CircleItemBean>? = arrayListOf(),
    val type: Int,
    val typeStr: String,
    val isAudit: String,
)


data class CircleItemBean(
    override var itemType: Int = 0,
    val circleId: String = "0",
    val description: String = "",
    val userId: Int = 0,
    val name: String = "",
    val nameColor: String? = "",
    val hotIcon: String = "",
    val isHot: String = "",//是否热门 1 是 0 不是
    val isRecommend: String = "",//是否推荐 1是 0 不是
    val isAudit: Int = 0,//是否推荐 0没有管理权限 1 有管理权限
    val pic: String = "",
    val checkStatus: String = "",//状态 1 审核通过 2 待审核  3认证失败
    val checkPassTime: String = "",
    val createTime: String = "",
    val userCount: Int = 0,
    val isGrounding: Int = 0,//0上架 1下架
    val postsCount: String = "",
    val applyerCount: Int = 0, //申请人数
    val checkNoReason: String = "", //审核不通过原因
    var typeStr: String,//圈子角色名称
    var isShowTitle: Boolean = false,//是否为圈子角色
    var tags:List<NewCirceTagBean>?=null,
    var tagIds:ArrayList<Int>?=null,
    val type:String,
    val needAudit:String,
    var star:String?=null,
    var status:String?=null,//1审核中 2通过

) : MultiItemEntity,Serializable


data class CircleMemberBean(
    val dataList: List<CircleMemberData>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int,
    val extend: CircleExtendBean
)

//1 显示设置按钮  0 不显示	 isStarRole
//是否圈主 1是 0否	isCircler
data class CircleExtendBean(val isCircler: Int, val isStarRole: String)

data class CircleMemberData(
    override var itemType: Int,
    val avatar: String,
    val circleId: String,
    val createTime: String,
    val memberIcon: String,
    val memberId: Int,
    val memberName: String,
    val nickname: String,
    val status: String,
    val userId: String,
    val name: String,
    val pic: String,
    val checkStatus: Int,
    val description: String,
    val postsCount: Int,
    val userCount: Int,
    val starOrderNumStr: String,
    val starOrderNum: String,
    val level: Int
) : MultiItemEntity


data class CircleTagBean(
    val refuse: List<Refuse>?
)

data class Refuse(
    val type: String
)

data class CircleUserBean(
    val userApplyCount: Int,
    val userCount: Int
)

//管理员身份
data class CircleStatusItemBean(
    val circleStarRoleId: Int,
    val createTime: Long,
    val memo: String,
    val operatorName: String,
    val orderNum: Int,
    val starAuthority: String,
    val starImgUrl: String,
    val starName: String,
    val starNum: Int,
    val status: Int,
    val surplusNum: Int,
    val updateTime: Long
)



/**
 *  我的推荐圈子
 * */
data class  MineRecommendCircle(var circleId:Long,var name:String,var userCount:Long,var pic:String, var avatars:ArrayList<String>,var posts:List<PostDataBean>,var sort:Int){

}