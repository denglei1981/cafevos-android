package com.changanford.circle.bean

import com.changanford.common.bean.AnswerContentsData

data class MechanicData(
    var identityType: String? = "",
    val qaUjId: Int,
    val tecnicianVoList: MutableList<TecnicianVo>,
    var moreTecnicians: moreJumpData? = null,
)

data class moreJumpData(
    val jumpCode: String = "",
    val jumpVal: String = ""
)

data class TecnicianVo(
    val avater: String,
    val nickName: String,
    val userId: String,
    val qaTechnicianId: Int,
    var isFollow: Int = 0,
    val huDongCount: Int = 0,
    val conQaUjId: String? = null,
    var anserRankNum: Int = 0
)


//data class CircleAskListMainData(
//    var adopt: String = "",
//    var answerCount: Int = -1,
//    var conQaQuestionId: Int = -1,
//    var content: String = "",
//    var createTime: String = "",
//    var fbReward: Int = 0,
//    var imgs: String? = "",
//    var jumpType: String = "",
//    var jumpValue: String = "",
//    var qaAnswer: QaAnswer? = null,
//    var questionType: String = "",
//    var questionTypeName: String = "",
//    var title: String = "",
//    var viewVal: Int = -1,
//    var pisList: List<String>? = arrayListOf(),
//    var emptyType: Int = -1,
//) : MultiItemEntity {
//    private fun getItemTypeLocal(): Int {
//
//
//        if (emptyType == 1) {
//            return 3
//        }
//        return 2 //  mei you hui da
//    }
//
//    override var itemType: Int = getItemTypeLocal()
//
//    fun getPicLists(): List<String>? {
//        if (qaAnswer?.answerContents.isNullOrEmpty()) {
//            return null
//        } else {
//            val mList = ArrayList<String>()
//            qaAnswer?.answerContents?.forEach {
//                if (!it.imgUrl.isNullOrEmpty()){
//                    mList.add(it.imgUrl)
//                }
//            }
//            return mList
//        }
////        if (imgs == null) {
////            return pisList
////        }
////        return if (imgs?.isEmpty() == true) {
////            pisList
////        } else {
////            imgs?.split(",")
////        }
//    }
//
//
//}

data class QaAnswer(
    val adopt: String,
    var answerTime: Long = 0,
    val conQaAnswerId: Int,
    val content: String,
    val qaUserVO: QaUserVO,
    val replyCount: Int,
    val answerContents: ArrayList<AnswerContentsData>?,
)

data class QaUserVO(
    val avater: String?,
    val conQaTechnicianId: Int,
    val conQaUjId: Int,
    val identity: String,
    val modelCode: String,
    val modelName: String,
    val nickName: String,
    val userId: Int
)