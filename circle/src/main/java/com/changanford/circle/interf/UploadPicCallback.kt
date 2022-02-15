package com.changanford.circle.interf

interface UploadPicCallback {


    fun onUploadSuccess(files: ArrayList<String>)

    fun onUploadFailed(errCode: String)

    fun onuploadFileprogress(progress: Long)

}