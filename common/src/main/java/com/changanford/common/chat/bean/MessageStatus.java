package com.changanford.common.chat.bean;

/**
 * 文件名：MessageStatus
 * 创建者: zcy
 * 创建日期：2020/10/13 16:40
 * 描述: TODO
 * 修改描述：TODO
 */
public enum MessageStatus {
    DEFAULT,//默认
    MESSAGE_LOADING,//发送中
    MESSAGE_FAILED,//发送失败
    MESSAGE_SUCCESS //已发送
}
