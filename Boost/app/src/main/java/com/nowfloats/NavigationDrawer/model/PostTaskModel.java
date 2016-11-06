package com.nowfloats.NavigationDrawer.model;

/**
 * Created by guru on 27-05-2015.
 */
public class PostTaskModel {
    public String clientId;
    public String message;
    public Boolean isPictureMessage;
    public String merchantId;
    public String parentId;
    public Boolean sendToSubscribers;
    public String socialParameters;

    public PostTaskModel(String id,String msg, String socialParameters,Boolean pic,
                         String merchantId,String parentId,Boolean sendToSubscribers){
        this.clientId = id;
        this.message  = msg;
        this.isPictureMessage = pic;
        this.merchantId = merchantId;
        this.parentId = parentId;
        this.sendToSubscribers = sendToSubscribers;
        this.socialParameters = socialParameters;
    }
}
