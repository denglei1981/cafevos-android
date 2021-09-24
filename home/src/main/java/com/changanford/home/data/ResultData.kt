package com.changanford.home.data


class ResultData(var resultCode: Int, var data: Any?) {



    companion object {
        const val OK = 9999
        const val ERROR = 9997
    }

    override fun toString(): String {
        return "[resultCode: " + resultCode + " data: " + (if (data == null) "null" else data.toString()) + "]"
    }
}