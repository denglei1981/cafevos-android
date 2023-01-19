package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2023/1/19
 *Purpose
 */
data class QuestionCreateBean(
    val adopt: String = "",
    val adoptTime: Any? = Any(),
    val adoptedConQaAnswerId: Any? = Any(),
    val adoptedConQaUjId: Any? = Any(),
    val adoptedUserId: Any? = Any(),
    val aliReveiw: Any? = Any(),
    val answerCount: Int = 0,
    val approveStatus: String = "",
    val conQaQuestionId: Int = 0,
    val content: String = "",
    val createTime: Long = 0,
    val dataState: String = "",
    val imgs: Any? = Any(),
    val memo: Any? = Any(),
    val newestAnswerTime: Any? = Any(),
    val newestConQaUjId: Any? = Any(),
    val newestQaAnswerId: Any? = Any(),
    val newestUserId: Any? = Any(),
    val onShelve: String = "",
    val `operator`: Any? = Any(),
    val questionType: String = "",
    val questionerConQaUjId: Int = 0,
    val questionerUserId: Int = 0,
    val rewardFb: Int = 0,
    val sensiveWord: String = "",
    val sensiveWordText: Any? = Any(),
    val title: String = "",
    val updateTime: Long = 0,
    val viewVal: Int = 0
)