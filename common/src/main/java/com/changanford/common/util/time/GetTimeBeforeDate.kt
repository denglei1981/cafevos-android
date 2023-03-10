package com.changanford.common.util.time

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 *Author lcw
 *Time on 2023/3/9
 *Purpose
 */
object GetTimeBeforeDate {

    @SuppressLint("SimpleDateFormat")
    fun getTimeDate(before: Int) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -before)
        val date = calendar.time
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")

        val stringDate: String = simpleDateFormat.format(date)



    }
}