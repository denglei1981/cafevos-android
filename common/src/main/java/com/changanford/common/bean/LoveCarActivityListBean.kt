package com.changanford.common.bean

import com.alibaba.fastjson.JSONObject

data class LoveCarActivityListBean(
    var activityList: ArrayList<ActivityListBean>,
    var carSeriesId: Int,//	车系id	integer(int32)
    var carSeriesName: String,//	车系名称	string
) {
}

data class ActivityListBean(
    var activityId: Int,//	活动id	integer(int32)
    var activityName: String,//	活动名称	string
    var bgImgs: String,//	活动背景图，逗号分隔	string
    var carCircleId: Int,//	关联圈子id	integer(int64)
    var carCircleName: String,//	关联圈子名称	string
    var carSeriesId: Int,//	关联车系id	integer(int32)
    var carSeriesName: String,//	关联车系名称	string
    var coverImg: String,//	活动封面图	string
    var createBy: String = "",//		string
    var createTime: String,//	创建时间	string(date-time)
    var dataState: Int,//	数据状态（0-删除，1-未删除）,可用值:DataStateEnum.DELETED(code=0, dbCode=0, message=已删除),DataStateEnum.EXIST(code=1, dbCode=1, message=未删除)	string
    var initHigh: Int = 0,//	模块起始高度	integer(int32)
    var operator: String = "",//	操作人id	string
    var params: JSONObject?,//		object
    var recommendSort: Int,//	推荐排序号，不推荐不填	integer(int32)
    var remark: String = "",//		string
    var searchValue: String = "",//		string
    var sort: Int,//	排序值	integer(int32)
    var status: String,//	状态 1已上架 0 已下架,可用值:LoveCarStatusEnum.OPEN(code=OPEN, dbCode=1, message=已上架),LoveCarStatusEnum.CLOSE(code=CLOSE, dbCode=0, message=已下架)	string
    var updateBy: String = "",//		string
    var updateTime: String,//	修改时间	string(date-time)
    var validTimeBegin: String,//	有效期开始(活动时间开始)	string(date-time)
    var validTimeEnd: String,//	有效期结束(活动时间结束)	string(date-time)
)