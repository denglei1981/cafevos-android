package com.changanford.common.bean

/**
 * @Author: lcw
 * @Date: 2020/9/1
 * @Des:
 */
data class MineFeedbackRecordBean(var name: String) {
}

//{"title":"我的意见","subtitle":"请发表你的意见","jumpData":{"jumpDataType":2,"jumpDataValue":"https://www.baidu.com"}}

data class FeedbackSettingBean(val title: String, val subtitle: String, val jumpData: JumpDataBean)
