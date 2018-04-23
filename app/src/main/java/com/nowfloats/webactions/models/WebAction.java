package com.nowfloats.webactions.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebAction {

@SerializedName("ActionId")
@Expose
private String actionId;
@SerializedName("UserId")
@Expose
private String userId;
@SerializedName("UserName")
@Expose
private Object userName;
@SerializedName("Name")
@Expose
private String name;
@SerializedName("Type")
@Expose
private String type;
@SerializedName("Visibility")
@Expose
private Integer visibility;
@SerializedName("DisplayName")
@Expose
private String displayName;
@SerializedName("Description")
@Expose
private Object description;
@SerializedName("UpdatedOn")
@Expose
private String updatedOn;
@SerializedName("Properties")
@Expose
private List<Property> properties = null;

public String getActionId() {
return actionId;
}

public void setActionId(String actionId) {
this.actionId = actionId;
}

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

public Object getUserName() {
return userName;
}

public void setUserName(Object userName) {
this.userName = userName;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public Integer getVisibility() {
return visibility;
}

public void setVisibility(Integer visibility) {
this.visibility = visibility;
}

public String getDisplayName() {
return displayName;
}

public void setDisplayName(String displayName) {
this.displayName = displayName;
}

public Object getDescription() {
return description;
}

public void setDescription(Object description) {
this.description = description;
}

public String getUpdatedOn() {
return updatedOn;
}

public void setUpdatedOn(String updatedOn) {
this.updatedOn = updatedOn;
}

public List<Property> getProperties() {
return properties;
}

public void setProperties(List<Property> properties) {
this.properties = properties;
}

}