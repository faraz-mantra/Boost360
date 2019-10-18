package com.nowfloats.CustomPage.Model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

 public class CustomPageLink{

	@SerializedName("Total")
	private int total;

	@SerializedName("Skip")
	private int skip;

	@SerializedName("Items")
	private List<ItemsItem> items;

	@SerializedName("Limit")
	private int limit;

	@SerializedName("Count")
	private int count;

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setSkip(int skip){
		this.skip = skip;
	}

	public int getSkip(){
		return skip;
	}

	public void setItems(List<ItemsItem> items){
		this.items = items;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	public void setLimit(int limit){
		this.limit = limit;
	}

	public int getLimit(){
		return limit;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}
}