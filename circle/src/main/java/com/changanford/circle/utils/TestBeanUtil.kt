package com.changanford.circle.utils

import com.changanford.circle.bean.MultiBean
import com.changanford.common.bean.AuthorBaseVo

object TestBeanUtil {
    fun getPicList():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("http://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
        list.add("http://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
        list.add("https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg")
        list.add("http://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
        list.add("http://fuss10.elemecdn.com/3/28/bbf893f792f03a54408b3b7a7ebf0jpeg.jpeg")
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


    fun  getTestHasAnswerBean():MultiBean{
        return MultiBean(picList = getPicList(),content = getContent(),authorBaseVo = getAuthor(),answerContent = getAskContent())
    }
    fun  getTestNOAnswerBean():MultiBean{
        return MultiBean(picList = getPicList(),content = getContent(),authorBaseVo = getAuthor())
    }

}