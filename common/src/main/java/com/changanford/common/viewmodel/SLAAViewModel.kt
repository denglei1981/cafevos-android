package com.changanford.common.viewmodel

import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author: niubobo
 * @date: 2024/12/9
 * @description：
 */
class SLAAViewModel : BaseViewModel() {

    fun addWbOrder(
        content: String,
        orderNo: String,
        signPic: String,
        block: () -> Unit
    ) {
        viewModelScope.launch {
            fetchRequest(true) {
                val body = HashMap<String, Any>()
                body["content"] = content
                body["orderNo"] = orderNo
                body["signPic"] = signPic
                body["signTime"] = formatTimestampToDate()
                val rKey = getRandomKey()
                apiService.addOrUpdateWbOrderSign(body.header(rKey), body.body(rKey))
                    .onSuccess { block.invoke() }
                    .onWithMsgFailure { it?.toast() }
            }
        }
    }

    private fun formatTimestampToDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(System.currentTimeMillis())
        return dateFormat.format(date)
    }
}