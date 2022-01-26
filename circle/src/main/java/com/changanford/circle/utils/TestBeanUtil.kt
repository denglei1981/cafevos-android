package com.changanford.circle.utils

import com.changanford.circle.bean.MultiBean
import com.changanford.common.bean.AuthorBaseVo

object TestBeanUtil {
    fun getPicList():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        return list
    }

    fun getOnePicList():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("ford-manager/2022/01/22/e89ea220e36a4385862e91e9fcaf3f1c.jpg")
        return list
    }


    fun getContent():String{
        return "回答的很好下次不要回答了"
    }
    fun getAskContent():String{
        return "问得很好,下次别问了"
    }

    fun getAuthor(): AuthorBaseVo {
        return AuthorBaseVo(authorId = "1",avatar = "http://fuss10.elemecdn.com/3/28/bbf893f792f03a54408b3b7a7ebf0jpeg.jpeg",nickname = "福特")
    }
    fun  getTestHasAnswerOnePicBean():MultiBean{
        return MultiBean(picList = getOnePicList(),content = getContent(),authorBaseVo = getAuthor(),answerContent = getAskContent())
    }

    fun  getTestHasAnswerBean():MultiBean{
        return MultiBean(picList = getPicList(),content = getContent(),authorBaseVo = getAuthor(),answerContent = getAskContent())
    }
    fun  getTestNOAnswerBean():MultiBean{
        return MultiBean(picList = getPicList(),content = getContent(),authorBaseVo = getAuthor())
    }

}