package com.nowfloats.NavigationDrawer.model;

import java.util.ArrayList;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class DomainDetails {

    public String fpTag;
    private String domainName;
    private String domainType;
    private String ActivatedOn;
    private String ExpiresOn;
    private boolean hasDomain;
    private boolean isActive;
    private boolean isExpired;
    private String ErrorMessage;
    private boolean isPending;
    private boolean isFailed;
    private boolean isLinked;
    private ArrayList<String> NameServers;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getActivatedOn() {
        return ActivatedOn;
    }

    public void setActivatedOn(String activatedOn) {
        ActivatedOn = activatedOn;
    }

    public String getExpiresOn() {
        return ExpiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        ExpiresOn = expiresOn;
    }

    public boolean isHasDomain() {
        return hasDomain;
    }

    public void setHasDomain(boolean hasDomain) {
        this.hasDomain = hasDomain;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean linked) {
        isLinked = linked;
    }

    public ArrayList<String> getNameServers() {
        return NameServers;
    }

    public void setNameServers(ArrayList<String> nameServers) {
        NameServers = nameServers;
    }
}
