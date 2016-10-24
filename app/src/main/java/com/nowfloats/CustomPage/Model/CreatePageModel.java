package com.nowfloats.CustomPage.Model;

/**
 * Created by guru on 26/08/2015.
 */
public class CreatePageModel {
    public String DisplayName;
    public String HtmlCode;
    public String Tag;
    public String clientId;

    public CreatePageModel(String displayName,String htmlCode,String tag,String id){
            this.DisplayName = displayName;
            this.HtmlCode = htmlCode;
            this.Tag = tag;
            this.clientId = id;
    }
}