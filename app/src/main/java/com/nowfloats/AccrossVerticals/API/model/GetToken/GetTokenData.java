package com.nowfloats.AccrossVerticals.API.model.GetToken;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTokenData {

	@SerializedName("WebActions")
	private List<WebActionsItem> webActions;

	@SerializedName("Token")
	private String token;

	public List<WebActionsItem> getWebActions(){
		return webActions;
	}

	public void setWebActions(List<WebActionsItem> webActions){
		this.webActions = webActions;
	}

	public String getToken(){
		return token;
	}

	public void setToken(String token){
		this.token = token;
	}
}