package com.nowfloats.CustomPage.Model;

import java.util.List;
 import com.google.gson.annotations.SerializedName;

 public class ItemsItem{

	@SerializedName("keywords")
	private List<Object> keywords;

	@SerializedName("name")
	private String name;

	@SerializedName("html")
	private String html;

	@SerializedName("id")
	private String id;

	@SerializedName("updatedon")
	private String updatedon;

	@SerializedName("createdon")
	private String createdon;

	@SerializedName("url")
	private Url url;

	public void setKeywords(List<Object> keywords){
		this.keywords = keywords;
	}

	public List<Object> getKeywords(){
		return keywords;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setHtml(String html){
		this.html = html;
	}

	public String getHtml(){
		return html;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setUpdatedon(String updatedon){
		this.updatedon = updatedon;
	}

	public String getUpdatedon(){
		return updatedon;
	}

	public void setCreatedon(String createdon){
		this.createdon = createdon;
	}

	public String getCreatedon(){
		return createdon;
	}

	public void setUrl(Url url){
		this.url = url;
	}

	public Url getUrl(){
		return url;
	}
}