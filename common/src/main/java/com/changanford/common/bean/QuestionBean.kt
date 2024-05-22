package com.changanford.common.bean

import android.content.Context
import com.changanford.common.R
import com.changanford.common.util.MConstant
import com.changanford.common.wutil.WConstant

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionBean
 */
data class QuestionInfoBean(
    val id:String?=null,
    val pic:String?=null,
    val imgs:List<String>?=null,
    val adoptNum: String = "0",
    val adoptRankNum: String? = null,
    val anserNum: String? = "0",
    val anserRankNum: String? = null,
    val introduction: String? = null,
    val questionNum: String? = "0",
    val user: QuestionUserInfo = QuestionUserInfo(),
    val dataList:ArrayList<QuestionItemBean>?=null,
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val totalPage: Int = 0,
    val total: Int = 0,
    val questionTypesCode:ArrayList<String>?=null,
    var tagNameArr:ArrayList<String>?=null,
){
    /**
     * 是否是自己
    * */
    fun isOneself():Boolean{
       return MConstant.userId==user.userId
    }
    /**
     * 提问人身份 0 普通  1 技师  2车主
    * */
    fun getIdentity():Int{
        return when(user.identity){
            "NORMAL"->0
            "TECHNICIAN"->1
            "CAR_OWNER"->2
            else ->0
        }
    }
    /**
     * 是否可以提问
     * 是自己并且不是技师才可以提问----118版本修改 技师也可以提问
    * */
    fun getIsQuestion():Boolean{
//        return isOneself()&&getIdentity()!=1
        return isOneself()
    }
    /**
     * 获取tab
    * */
    fun getTabs(context:Context):ArrayList<QuestionTagBean>{
        val isOneself=isOneself()
        val tabs= arrayListOf<QuestionTagBean>()
        when(user.identity){
            //技师
            "TECHNICIAN"->{
                tabs.apply {
                    if(isOneself){
                        add(QuestionTagBean(context.getString(R.string.str_myQuestions),"QUESTION"))
                        add(QuestionTagBean(context.getString(R.string.str_invitedToAnswer),"TECHNICIAN"))
                        add(QuestionTagBean(context.getString(R.string.str_myAnswer),"ANSWER"))
//                        add(QuestionTagBean(context.getString(R.string.str_answerAccepted),"ADOPT"))//118版本隐藏
                    }else{
                        add(QuestionTagBean(context.getString(R.string.str_taQuestions),"QUESTION"))
                        add(QuestionTagBean(context.getString(R.string.str_taAnswer),"ANSWER"))
                    }
                }
            }
            //普通 、 车主
            else->{
                tabs.apply {
                    if(isOneself){
                        add(QuestionTagBean(context.getString(R.string.str_myQuestions),"QUESTION"))
                        add(QuestionTagBean(context.getString(R.string.str_myAnswer),"ANSWER"))
                    }else{
                        add(QuestionTagBean(context.getString(R.string.str_taQuestions),"QUESTION"))
                        add(QuestionTagBean(context.getString(R.string.str_taAnswer),"ANSWER"))
                    }
//                    add(QuestionTagBean(context.getString(R.string.str_answerAccepted),"ADOPT"))
                }
            }
        }
        return tabs
    }
    /**
     * 个人中心 types
    * */
    fun getStatisticalTypes(context:Context):List<QuestionTagBean>{
        val tags= arrayListOf<QuestionTagBean>()
        when(user.identity){
            //技师
            "TECHNICIAN"->{
                tags.apply {
                    add(QuestionTagBean(context.getString(R.string.str_answerTotalNumber),anserNum))
                    add(QuestionTagBean(context.getString(R.string.str_acceptedTotalNumber),adoptNum))
                    add(QuestionTagBean(context.getString(R.string.str_replyToList),anserRankNum))
                    add(QuestionTagBean(context.getString(R.string.str_adoptionList),adoptRankNum))
                }
            }
            //普通 、 车主
            else->{
                tags.apply {
                    add(QuestionTagBean(context.getString(R.string.str_questionsTotalNumber),questionNum))
                    add(QuestionTagBean(context.getString(R.string.str_answerTotalNumber),anserNum))
                    add(QuestionTagBean(context.getString(R.string.str_acceptedTotalNumber),adoptNum))
                }
            }
        }
        return tags
    }
    fun setTagNames(tagIdList:ArrayList<String>?=questionTypesCode){
        tagNameArr= arrayListOf()
        tagIdList?.forEach {tagId->
            WConstant.questionTagList?.find { it.dictValue==tagId }?.dictLabel?.apply {
                tagNameArr?.add(this)
            }
        }
    }
}
data class QuestionUserInfo(
    val avater: String? = null,
    val conQaTechnicianId: String? = null,
    val conQaUjId: String? = null,
    val memberIcon: String? = null,
    val imags: ArrayList<Imag> = arrayListOf(),
    val identity: String = "",
    val modelCode: String? = null,
    val modelName: String? = null,
    val nickName: String? = null,
    val userId: String? = null
)
data class QuestionTagBean(var tagName:String?=null,var tag:String?=null)
data class QuestionItemBean(
    val adopt: String? = null,
    val answerCount: String? = null,
    val conQaQuestionId: String? = null,
    val content: String = "",
    val createTime: Long = 0,
    val fbReward: Int = 0,
    val imgs: String? = null,
    val jumpType: Int = 0,
    val jumpValue: String? = null,
    val qaAnswer: AnswerInfoBean? = null,
    val questionType: String = "",
    val questionTypeName: String = "",
    val title: String? = null,
    val viewVal: String? = null,
){
    fun getPicLists(): List<String>? {
        if (qaAnswer?.answerContents.isNullOrEmpty()) {
            return null
        } else {
            val mList = ArrayList<String>()
            qaAnswer?.answerContents?.forEach {
                if (!it.imgUrl.isNullOrEmpty()){
                    mList.add(it.imgUrl)
                }
            }
            return mList
        }
//        if (imgs == null) {
//            return pisList
//        }
//        return if (imgs?.isEmpty() == true) {
//            pisList
//        } else {
//            imgs?.split(",")
//        }
    }
}
data class AnswerInfoBean(
    val adopt:String?=null,
    val answerTime:Long?=null,
    val conQaAnswerId:String?=null,
    val content:String?=null,
    val replyCount:String?=null,
    val qaUserVO:QuestionUserInfo?=null,
    val answerContents: ArrayList<AnswerContentsData>?,
)

data class AnswerContentsData(val imgUrl: String, val imgDesc: String)