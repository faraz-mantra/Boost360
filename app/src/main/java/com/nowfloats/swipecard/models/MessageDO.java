package com.nowfloats.swipecard.models;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class MessageDO {

    private String messageId;
    private String fpId;
    private int status;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}