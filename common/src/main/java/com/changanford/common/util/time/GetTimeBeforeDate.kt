package com.changanford.common.util.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 *Author lcw
 *Time on 2023/3/9
 *Purpose
 */
object GetTimeBeforeDate {

    @SuppressLint("SimpleDateFormat")
    fun getTimeDate(before: Int): String {

        val useData = if (before == 0) 0 else -before
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, useData)
        val date = calendar.time
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")

        return simpleDateFormat.format(date)
    }
}