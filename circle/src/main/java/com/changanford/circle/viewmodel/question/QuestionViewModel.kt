package com.changanford.circle.viewmodel.question

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.QuestionInfoBean
import com.changanford.common.net.*
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.WConstant

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionViewModel
 */
class QuestionViewModel:BaseViewModel() {
    val questionInfoBean= MutableLiveData<QuestionInfoBean?>()
    var questionListBean: MutableLiveData<QuestionInfoBean?> = MutableLiveData()
    var questTagList: MutableLiveData<ArrayList<QuestionData>?> = MutableLiveData()
    /**
     * 我/TA的问答
     * [conQaUjId]被查看人的问答参与表id
     * */
    fun personalQA(conQaUjId:String,showLoading:Boolean=false){
        launch(showLoading = showLoading, block = {
            body.clear()
            body["conQaUjId"]=conQaUjId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().personalQA(body.header(rKey), body.body(rKey)).onSuccess {
                questionInfoBean.postValue(it)
            }.onWithMsgFailure {
                questionInfoBean.postValue(null)
                it?.toast()
            }
        })
    }
    /**
     *我/TA 的 提问/回答/被采纳
     * [conQaUjId]被查看人的问答参与表id
     * [personalPageType] QUESTION、ANSWER、ADOPT
     * */
    fun questionOfPersonal(conQaUjId:String,personalPageType:String,pageNo:Int=1,pageSize:Int=this.pageSize){
        launch(block = {
            body.clear()
            body["pageNo"]=pageNo
            body["pageSize"]=pageSize
            body["queryParams"]=HashMap<String,Any>().also {
                it["conQaUjId"]=conQaUjId
                it["personalPageType"]=personalPageType
            }
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().questionOfPersonal(body.header(rKey), body.body(rKey)).onSuccess {
                questionListBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }
    /**
     *技师邀请回答列表
     * */
    fun questionOfInvite(pageNo:Int=1,pageSize:Int=this.pageSize){
        launch(block = {
            body.clear()
            body["pageNo"]=pageNo
            body["pageSize"]=pageSize
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().questionOfInvite(body.header(rKey), body.body(rKey)).onSuccess {
                questionListBean.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        })
    }
    /**
     *获取技师标签
     * */
    fun getQuestionType(){
        if(WConstant.questionTagList!=null&&WConstant.questionTagList!!.isNotEmpty()){
            questTagList.postValue(WConstant.questionTagList)
            return
        }
        launch(block = {
            body.clear()
            body["dictType"] = "qa_question_type"
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getQuestionType(body.header(rKey), body.body(rKey)).onSuccess {
                WConstant.questionTagList=it
                questTagList.postValue(it)
            }.onWithMsgFailure {
                questTagList.postValue(null)
                it?.toast()
            }
        })
    }
}