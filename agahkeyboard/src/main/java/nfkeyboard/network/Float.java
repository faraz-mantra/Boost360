package nfkeyboard.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nfkeyboard.models.AllSuggestionModel;

public class Float {

@SerializedName("_id")
@Expose
private String id;
@SerializedName("createdOn")
@Expose
private String createdOn;
@SerializedName("htmlString")
@Expose
private String htmlString;
@SerializedName("imageUri")
@Expose
private String imageUri;
@SerializedName("isHtmlString")
@Expose
private Boolean isHtmlString;
@SerializedName("keywords")
@Expose
private List<String> keywords = null;
@SerializedName("message")
@Expose
private String message;
@SerializedName("messageIndex")
@Expose
private Integer messageIndex;
@SerializedName("tileImageUri")
@Expose
private Object tileImageUri;
@SerializedName("type")
@Expose
private Object type;
@SerializedName("url")
@Expose
private String url;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getCreatedOn() {
return createdOn;
}

public void setCreatedOn(String createdOn) {
this.createdOn = createdOn;
}

public String getHtmlString() {
return htmlString;
}

public void setHtmlString(String htmlString) {
this.htmlString = htmlString;
}

public String getImageUri() {
return imageUri;
}

public void setImageUri(String imageUri) {
this.imageUri = imageUri;
}

public Boolean getIsHtmlString() {
return isHtmlString;
}

public void setIsHtmlString(Boolean isHtmlString) {
this.isHtmlString = isHtmlString;
}

public List<String> getKeywords() {
return keywords;
}

public void setKeywords(List<String> keywords) {
this.keywords = keywords;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public Integer getMessageIndex() {
return messageIndex;
}

public void setMessageIndex(Integer messageIndex) {
this.messageIndex = messageIndex;
}

public Object getTileImageUri() {
return tileImageUri;
}

public void setTileImageUri(Object tileImageUri) {
this.tileImageUri = tileImageUri;
}

public Object getType() {
return type;
}

public void setType(Object type) {
this.type = type;
}

public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

public AllSuggestionModel toAllSuggestion() {
    AllSuggestionModel model =  new AllSuggestionModel(message, imageUri);
    model.setUrl(getUrl());
    model.setId(id);
    return model;
}

}