package com.changanford.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.AccBean
import com.changanford.common.bean.InfoBean
import com.changanford.common.bean.PostBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 *  文件名：ActViewModel
 *  创建者: zcy
 *  创建日期：2021/9/29 9:36
 *  描述: TODO
 *  修改描述：TODO
 */
class ActViewModel : ViewModel() {

    /**
     * 我收藏的 资讯 1
     */
    fun queryMineCollectInfo(pageNo: Int, result: (CommonResponse<InfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineCollectList(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我的资讯
     * 0:查询当前登录用户发布
     */
    fun queryMineSendInfoList(
        userId: String,
        pageNo: Int, result: (CommonResponse<InfoBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["isPage"] = true
                body["queryParams"] = mapOf("userId" to userId)
                var rkey = getRandomKey()
                apiService.queryMineSendInfoList(body.header(rkey), body.body(rkey))
            })
        }
    }

    /*-----------------我的收藏------------------------*/
    /**
     * 我收藏的帖子
     */
    fun queryMineCollectPost(pageNo: Int, result: (CommonResponse<PostBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineCollectInfo(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我收藏的活动
     */
    fun queryMineCollectAc(pageNo: Int, result: (CommonResponse<AccBean>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineCollectAc(body.header(rkey), body.body(rkey))
            })
        }
    }


    /*----------------------我的足迹------------------------*/
    /**
     * 我的足迹  资讯 1
     */
    fun queryMineFootInfo(pageNo: Int, result: (CommonResponse<InfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()

                apiService.queryMineFootprintList(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     *我的足迹帖子
     */
    fun queryMineFootPost(pageNo: Int, result: (CommonResponse<PostBean>) -> Unit) {


        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineFootprintInfo(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我的足迹活动
     */
    fun queryMineFootAc(pageNo: Int, result: (CommonResponse<AccBean>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineFootAc(body.header(rkey), body.body(rkey))
            })
        }
    }


    /*-----------------------我的发布---------------------*/


    /**
     *我发布帖子
     */
    fun queryMineSendPost(
        userId: String,
        pageNo: Int,
        result: (CommonResponse<PostBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = mapOf("userId" to userId)
                var rkey = getRandomKey()
                apiService.queryMineSendPost(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我发布活动
     */
    fun queryMineSendAc(
        userId: String,
        pageNo: Int,
        result: (CommonResponse<AccBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["page"] = true
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()

                apiService.queryMineSendAc(body.header(rkey), body.body(rkey))
            })
        }
    }


    fun queryTaSendAc(
        userId: String,
        pageNo: Int,
        result: (CommonResponse<AccBean>) -> Unit
    ) {


        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["page"] = true
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = mapOf("userId" to userId)
                var rkey = getRandomKey()
                apiService.queryTaSendAc(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 我参与的活动
     */
    fun queryMineJoinAc(pageNo: Int, result: (CommonResponse<AccBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                var rkey = getRandomKey()
                apiService.queryMineJoinAc(body.header(rkey), body.body(rkey))
            })
        }
    }

    //删除资讯
    fun deleteInfo(artIds: ArrayList<Int>, result: (CommonResponse<String>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["artIds"] = artIds
                var rkey = getRandomKey()
                apiService.deleteInfo(body.header(rkey), body.body(rkey))
            })
        }
    }


    //删除帖子
    fun deletePost(artIds: ArrayList<Int>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["postIds"] = artIds
                var rkey = getRandomKey()
                apiService.deletePost(body.header(rkey), body.body(rkey))
            })
        }
    }
}