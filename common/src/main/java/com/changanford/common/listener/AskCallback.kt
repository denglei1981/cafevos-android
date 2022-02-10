package com.changanford.common.listener

import com.changanford.common.bean.ResultData


interface AskCallback {
    fun onResult(result: ResultData)
}