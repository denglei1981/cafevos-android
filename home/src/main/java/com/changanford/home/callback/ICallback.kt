package com.changanford.home.callback

import com.changanford.home.data.ResultData

interface ICallback {
    fun onResult(result: ResultData)
}