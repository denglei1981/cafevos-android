package com.changanford.circle.bean

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity

data class MechanicData(
    val identityType: String,
    val qaUjId: Int,
    val tecnicianVoList: MutableList<TecnicianVo>
)

data class TecnicianVo(
    val avater: String,
    val nickName: String,
    val qaTechnicianId: Int

)


data class AskListMainData(
    val adopt: String,
    val answerCount: Int,
    val conQaQuestionId: Int,
    val content: String,
    val createTime: String,
    val fbReward: Int,
    var imgs: String = "",
    val jumpType: String,
    val jumpValue: String,
    val qaAnswer: QaAnswer? = null,
    val questionType: String,
    val questionTypeName:String,
    val title: String,
    val viewVal: Int,
    var pisList: List<String>? = null,
) : MultiItemEntity {
    private fun getItemTypeLocal(): Int {
        qaAnswer?.let {
            return 1
        }
        return 2 //  mei you hui da
    }
    override var itemType: Int = getItemTypeLocal()

     fun getPicLists(): List<String>? {
        if (imgs.isEmpty()) {
            return pisList
        } else {
            return  imgs.split(",")
        }
    }


}

data class QaAnswer(
    val adopt: String,
    val answerTime: String,
    val conQaAnswerId: Int,
    val content: String,
    val qaUserVO: QaUserVO,
    val replyCount: Int
)

data class QaUserVO(
    val avater: String,
    val conQaTechnicianId: Int,
    val conQaUjId: Int,
    val identity: String,
    val modelCode: String,
    val modelName: String,
    val nickName: String,
    val userId: Int
)