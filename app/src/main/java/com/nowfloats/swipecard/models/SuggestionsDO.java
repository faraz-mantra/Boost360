package com.nowfloats.swipecard.models;

import java.util.List;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SuggestionsDO {

    private String Action;
    private long date;
    private String fpId;
    private String messageId;
    private String type;
    private String value;
    private int status=-1;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private List<SugProducts> products = null;
    private List<SugUpdates> updates = null;

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<SugProducts> getProducts() {
        return products;
    }

    public void setProducts(List<SugProducts> products) {
        this.products = products;
    }

    public List<SugUpdates> getUpdates() {
        return updates;
    }

    public void setUpdates(List<SugUpdates> updates) {
        this.updates = updates;
    }
}
