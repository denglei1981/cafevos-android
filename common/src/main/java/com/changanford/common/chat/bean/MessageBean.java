package com.changanford.common.chat.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.changanford.common.chat.adapter.ChatAdapter;

/**
 * 文件名：MessageBean
 * 创建者: zcy
 * 创建日期：2020/10/13 10:25
 * 描述: TODO
 * 修改描述：TODO
 */
public class MessageBean implements MultiItemEntity {

    public MessageBean() {

    }
    private String uuid;
    private MessageType messageType;//消息类型
    private String headIcon;//头像地址
    private String nickName;//昵称
    private MessageBody messageBody;//消息体
    private MessageStatus messageStatus;//发送状态
    private long sendTime;//发送时间
    private String messageId;
    private boolean isOneselfSend;//是否时自己发送

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isOneselfSend() {
        return isOneselfSend;
    }

    public void setOneselfSend(boolean oneself) {
        isOneselfSend = oneself;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public MessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(MessageBody messageBody) {
        this.messageBody = messageBody;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public int getItemType() {
        if (getMessageType() == MessageType.TEXT) {
            return isOneselfSend() ? ChatAdapter.TYPE_SEND_TEXT : ChatAdapter.TYPE_RECEIVE_TEXT;
        } else if (getMessageType() == MessageType.IMAGE) {
            return isOneselfSend() ? ChatAdapter.TYPE_SEND_IMAGE : ChatAdapter.TYPE_RECEIVE_IMAGE;
        }
        return 0;
    }
}
