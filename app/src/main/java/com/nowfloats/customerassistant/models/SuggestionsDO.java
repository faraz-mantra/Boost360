package com.nowfloats.customerassistant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SuggestionsDO implements Serializable,Cloneable{

    public String Action;
    public long date;
    public String fpId;
    public String messageId;
    public String type;
    public String source;
    public String value;
    public String actualMessage;
    public String enquiredProduct;
    public String logoUrl;
    public String contactName;
    public String expiryTimeOfMessage;
    public String shortText;
    public long expiryDate;
    public int status = -1;
    public boolean isEmptyLayout;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getEnquiredProduct() {
        return enquiredProduct;
    }

    public void setEnquiredProduct(String enquiredProduct) {
        this.enquiredProduct = enquiredProduct;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isEmptyLayout() {
        return isEmptyLayout;
    }

    public void setEmptyLayout(boolean emptyLayout) {
        isEmptyLayout = emptyLayout;
    }

    public String getExpiryTimeOfMessage() {
        return expiryTimeOfMessage;
    }

    public void setExpiryTimeOfMessage(String expiryTimeOfMessage) {
        this.expiryTimeOfMessage = expiryTimeOfMessage;
    }

    public boolean isShareEnabled = false;
    public boolean isExpandGroup = false;
    public List<SugProducts> products = null;
    public List<SugUpdates> updates = null;

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpandGroup() {
        return isExpandGroup;
    }

    public void setExpandGroup(boolean expandGroup) {
        isExpandGroup = expandGroup;
    }

    public boolean isShareEnabled() {
        return isShareEnabled;
    }

    public void setShareEnabled(boolean shareEnabled) {
        isShareEnabled = shareEnabled;
    }

    public String getActualMessage() {
        return actualMessage;
    }

    public void setActualMessage(String actualMessage) {
        this.actualMessage = actualMessage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

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

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
