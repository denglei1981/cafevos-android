package com.changanford.common.bean

/**
 * 投票发送和返回
 */
data class VoteBean(

    var allowMultipleChoice: String = "",//是否允许多选,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    var allowViewResult: String = "",//是否允许查看结果,可用值:YesNoNumInDBEnum.YES(code=YES, dbCode=0, message=是, isTrue=true),YesNoNumInDBEnum.NO(code=NO, dbCode=1, message=否, isTrue=false)
    var beginTime: String = "",
    var circleId: String = "",
    var coverImg: String = "",
    var endTime: String = "",
    var optionList: ArrayList<VoteOptionBean> = ArrayList(),//	选项集合
    var title: String = "",
    var voteDesc: String = "",
    var voteType: String = "",//0 文字，1图文

) {
}


data class VoteOptionBean(
    var optionDesc: String = "",
    var optionImg: String = ""

)